import { mount } from "@vue/test-utils"
import ElementPlus from "element-plus"
import { describe, expect, it } from "vitest"
import FilterConditionComponent from "@/components/FilterCondition.vue"

const mountFilterCondition = (modelValue: unknown) =>
  mount(FilterConditionComponent, {
    global: {
      plugins: [ElementPlus],
      components: {
        Upload: {
          template: "<span />"
        }
      }
    },
    props: {
      modelValue: modelValue as any
    }
  })

describe("FilterCondition render test", () => {
  it("renders when backend returns a null filter condition", () => {
    expect(() => mountFilterCondition(null)).not.toThrow()
  })

  it("renders when backend returns a partial filter condition", () => {
    expect(() => mountFilterCondition({ attributeConditions: [] })).not.toThrow()
  })
})
