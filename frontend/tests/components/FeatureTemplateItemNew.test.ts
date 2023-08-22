import { shallowMount } from "@vue/test-utils"
import { describe, expect, it } from "vitest"
import FeatureTemplateItemNew from "@/components/FeatureTemplateItemNew.vue"

describe("FeatureTemplateItemNew render test", () => {
  it("正常渲染", () => {
    // 由于引入第三方控件，导致UI界面可测性不强，这里只是做UI测试说明举例
    const wrapper = shallowMount(FeatureTemplateItemNew,{
      props:{
        debugMode:true
      }
    })
    expect(wrapper.classes("custom-tree-container")).toBe(true)
    expect(wrapper.find("div").text()).toContain("this is a UI Test");
  })
})
