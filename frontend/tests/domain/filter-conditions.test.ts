import { describe, expect, it } from "vitest"
import { CompareOperator, isValidCustomAttributeCondition } from "@/domain/FilterConditions"

describe("filter condition domain helpers", () => {
  it("uses backend enum value for equals operator", () => {
    expect(CompareOperator.EQUALS).toBe("EQUALS")
  })

  it("validates scalar compare operators with a value", () => {
    expect(
      isValidCustomAttributeCondition({
        name: "age",
        key: "age",
        operator: CompareOperator.GREATER_THAN,
        value: "18",
        items: []
      })
    ).toBe(true)
  })

  it("validates list compare operators with items", () => {
    expect(
      isValidCustomAttributeCondition({
        name: "country",
        key: "country",
        operator: CompareOperator.IN,
        value: "",
        items: ["CN", "SG"]
      })
    ).toBe(true)
  })

  it("rejects empty list compare operators", () => {
    expect(
      isValidCustomAttributeCondition({
        name: "country",
        key: "country",
        operator: CompareOperator.NOT_IN,
        value: "",
        items: []
      })
    ).toBe(false)
  })
})
