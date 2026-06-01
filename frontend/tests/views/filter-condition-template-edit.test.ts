import { mount } from "@vue/test-utils"
import ElementPlus from "element-plus"
import { describe, expect, it, vi } from "vitest"
import FilterConditionTemplateEdit from "@/views/filter-condition-template/edit.vue"

vi.mock("vue-router", () => ({
  useRoute: () => ({
    params: {},
    query: {}
  }),
  useRouter: () => ({
    go: vi.fn(),
    push: vi.fn()
  })
}))

const mountPage = () =>
  mount(FilterConditionTemplateEdit, {
    global: {
      plugins: [ElementPlus],
      stubs: {
        FilterConditionComponent: {
          template:
            '<button data-test="emit-filter-condition" @click="$emit(\'update:modelValue\', nextValue)">update</button>',
          emits: ["update:modelValue"],
          data() {
            return {
              nextValue: {
                templateName: "",
                grayReleaseCondition: {
                  releasePercentage: 50
                },
                attributeConditions: []
              }
            }
          }
        }
      },
      components: {
        Upload: {
          template: "<span />"
        }
      }
    }
  })

describe("FilterCondition template edit page", () => {
  it("merges child filter-condition updates into the form without replacing the const reactive root", async () => {
    const wrapper = mountPage()
    ;(wrapper.vm as any).filterConditionTemplateForm.templateName = "vip users"

    await expect(wrapper.find('[data-test="emit-filter-condition"]').trigger("click")).resolves.toBeUndefined()
    expect((wrapper.vm as any).filterConditionTemplateForm.grayReleaseCondition.releasePercentage).toBe(50)
    expect((wrapper.vm as any).filterConditionTemplateForm.templateName).toBe("vip users")
  })
})
