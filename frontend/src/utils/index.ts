import dayjs from "dayjs"
import _ from "lodash"
import lodash from "lodash"
import moment from "moment"
import { TemplateDataType } from "@/domain/Features"

/** 格式化时间 */
export const formatDateTime = (time: string | number | Date) => {
  return time ? dayjs(new Date(time)).format("YYYY-MM-DD HH:mm:ss") : "N/A"
}

/** 用 JS 获取全局 css 变量 */
export const getCssVariableValue = (cssVariableName: string) => {
  let cssVariableValue = ""
  try {
    // 没有拿到值时，会返回空串
    cssVariableValue = getComputedStyle(document.documentElement).getPropertyValue(cssVariableName)
  } catch (error) {
    console.error(error)
  }
  return cssVariableValue
}

/** 用 JS 设置全局 CSS 变量 */
export const setCssVariableValue = (cssVariableName: string, cssVariableValue: string) => {
  try {
    document.documentElement.style.setProperty(cssVariableName, cssVariableValue)
  } catch (error) {
    console.error(error)
  }
}
let localId = 0
export const transTempToTreeDataSource = (temp: any) => {
  const tree = {
    name: temp.name || "",
    dataType: temp.dataType || "",
    defaultValue: temp.dataType == TemplateDataType.BOOLEAN ? Boolean(temp.defaultValue) : temp.defaultValue,
    key: temp.key || "",
    description: temp.description || "",
    choice: temp.choice,
    validator: {
      min: temp?.validator?.min || 0,
      max: temp?.validator?.max || 0,
      maxLength: temp?.validator?.maxLength || 0,
      required: temp?.validator?.required || false,
      pattern: temp?.validator?.pattern || ""
    },
    id: localId++,
    children: []
  }

  if (temp.items && temp.items.length) {
    for (let i = 0; i < temp.items.length; i++) {
      tree.children.push(transTempToTreeDataSource(temp.items[i]))
    }
  }
  return tree
}

export const StatusMaps = {
  add: "新增",
  view: "查看",
  edit: "编辑"
}

export function replaceChildrenWithItems(data) {
  // 如果是一个数组，则递归处理每个元素
  if (Array.isArray(data)) {
    data.forEach((item) => replaceChildrenWithItems(item))
    // 如果是一个对象，则处理其属性
  } else if (typeof data === "object") {
    for (const prop in data) {
      if (prop === "children") {
        data["items"] = data[prop]
        delete data[prop]
        // 递归处理嵌套的子级对象
        replaceChildrenWithItems(data["items"])
      } else {
        if (prop == "dataType") {
          switch (data[prop]) {
            case TemplateDataType.NUMBER:
              data["defaultValue"] = Number(data["defaultValue"])
              break
            case TemplateDataType.BOOLEAN:
              data["defaultValue"] = Boolean(data["defaultValue"])
              break
            default:
              break
          }
        }
        // 递归处理对象的属性
        replaceChildrenWithItems(data[prop])
      }
    }
  }
  return data
}

export const translateDataToTemplateItems = (value) => {
  value.template = value.dataSource[0]
  return value
}

export function getValue(info) {
  if (info.dataType == TemplateDataType.BOOLEAN) {
    if (_.hasIn(info, "value")) {
      return info.value
    } else {
      return Boolean(info.defaultValue)
    }
  } else if (info.dataType == TemplateDataType.NUMBER) {
    return Number(info.value || info.defaultValue)
  } else if (info.dataType == TemplateDataType.GENERATED) {
    return info.value || info.defaultValue
  } else if (info.dataType == TemplateDataType.SELECT) {
    return info.value
  } else {
    return info.value || info.defaultValue
  }
}

// FeatureConfg data数据处理函数
export function convertConfigObject(data) {
  if (data.dataType === TemplateDataType.OBJECT) {
    const obj = {}
    for (const child of data.children) {
      obj[child.key] = convertConfigObject(child)
    }
    return obj
  } else if (data.dataType === TemplateDataType.ARRAY) {
    const arr = []
    for (const child of data.children) {
      arr.push(convertConfigObject(child))
    }
    return arr
  } else {
    return getValue(data)
  }
}

function getModelByKey(key, items, defaultValue = null) {
  if (!!items && items.length > 0) {
    return lodash.find(items, (item) => {
      return item.key == key
    })
  } else {
    return defaultValue || {}
  }
}

//数据转换为template对象
export function mapDataToTemplate(name, obj, model) {
  if (lodash.isObject(obj) && !lodash.isArray(obj)) {
    return {
      name,
      dataType: TemplateDataType.OBJECT,
      ...lodash.omit(model, ["items"]),
      children: Object.entries(obj).map(([key, value]) => {
        return mapDataToTemplate(key, value, getModelByKey(key, model?.items || []))
      })
    }
  }
  if (typeof obj === "string") {
    return {
      name,
      dataType: TemplateDataType.STRING,
      ...model,
      value: obj
    }
  }
  if (typeof obj === "number") {
    return {
      name,
      dataType: TemplateDataType.NUMBER,
      ...model,
      value: obj
    }
  } else if (typeof obj === "boolean") {
    return {
      name,
      dataType: TemplateDataType.BOOLEAN,
      ...model,
      value: obj
    }
  } else if (lodash.isArray(obj)) {
    let extentObj = {}
    if (model.dataType == TemplateDataType.ARRAY) {
      extentObj = lodash.omit(model, ["items"])
    }
    return {
      name,
      ...extentObj,
      dataType: TemplateDataType.ARRAY,
      children: obj.map((it, index) => mapDataToTemplate("", it, model.items[index] || model.items[0]))
    }
  }
  throw new Error(name + obj)
}

export function validateSelectDateLowerThanToday(date: string) {
  return moment(date).isBefore(moment().subtract(1, "day"))
}
