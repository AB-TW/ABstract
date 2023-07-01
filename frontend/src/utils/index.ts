import dayjs from "dayjs"

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

export const transTempToTreeDataSource = (temp: any) => {
  const tree = {
    name: temp.name || "",
    dataType: temp.dataType || "",
    defaultValue: temp.defaultValue,
    key: temp.key || "",
    description: temp.description || "",
    validator: {
      min: temp?.validator?.min || 0,
      max: temp?.validator?.max || 0,
      maxLength: temp?.validator?.maxLength || 0,
      required: temp?.validator?.required || false,
      pattern: temp?.validator?.pattern || ""
    },
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
