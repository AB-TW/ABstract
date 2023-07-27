import dayjs from "dayjs"
import _ from "lodash"
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
    defaultValue: temp.dataType == "BOOLEAN" ? Boolean(temp.defaultValue) : temp.defaultValue,
    key: temp.key || "",
    description: temp.description || "",
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
  if (info.dataType == "BOOLEAN") {
    if (_.hasIn(info, "value")) {
      return info.value
    } else {
      return info.defaultValue
    }
  } else {
    return info.value || info.defaultValue
  }
}
// FeatureConfg data数据处理函数
export function convertConfigObject(data) {
  if (data.dataType === "OBJECT") {
    const obj = {}
    for (const child of data.children) {
      obj[child.key] = convertConfigObject(child)
    }
    return obj
  } else if (data.dataType === "ARRAY") {
    const arr = []
    for (const child of data.children) {
      arr.push(convertConfigObject(child))
    }
    return arr
  } else {
    return getValue(data)
  }
}
