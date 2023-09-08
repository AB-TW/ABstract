import http from '@/utils/http/http'
import {Features} from "@/domain/Features";
import {describe, expect, vi, it} from 'vitest'
describe("getFeatures service test", () => {
  const featureInstance = new Features();
  const tempResult = {
    "content": [
      {
        "featureKey": "a1",
        "name": "11333",
        "description": "555",
        "enabled": false,
        "createdAt": null,
        "updatedAt": "2023-07-01 14:12:16",
        "template": {
          "dataType": "BOOLEAN",
          "key": "a2",
          "name": "666",
          "description": "111",
          "defaultValue": "false",
          "validator": {
            "required": false,
            "maxLength": 10,
            "max": 2,
            "min": 0,
            "pattern": "123"
          },
          "items": []
        }
      },
      {
        "featureKey": "a3",
        "name": "12345",
        "description": "123",
        "enabled": false,
        "createdAt": "2023-07-01 13:17:07",
        "updatedAt": "2023-07-01 14:12:20",
        "template": {
          "dataType": "OBJECT",
          "key": "a4",
          "name": "333444",
          "description": "555666",
          "defaultValue": "",
          "validator": {
            "required": false,
            "maxLength": 0,
            "max": 0,
            "min": 0,
            "pattern": ""
          },
          "items": [
            {
              "dataType": "BOOLEAN",
              "key": "a123904567",
              "name": "111",
              "description": "123",
              "defaultValue": "false",
              "validator": {
                "required": false,
                "maxLength": 10,
                "max": 10,
                "min": 0,
                "pattern": "111"
              },
              "items": []
            }
          ]
        }
      }
    ],
    "total": 2,
    "page": 0,
    "pageSize": 10
  };
  it('getFeatures list success', async () => {

    vi.spyOn(http, 'get').mockResolvedValue(Promise.resolve({
      code: 200,
      message: "Success",
      data: tempResult
    }) as any)
    const result = await featureInstance.fetchAll(0, 10, "");
    console.log(result)
    expect(result).toStrictEqual(tempResult)
  })

  it('getFeatures sad path', async () => {
    try {
      vi.spyOn(http, 'get').mockRejectedValue({
        "code": 401, "message": "获取列表失败", "data": null
      });
      await featureInstance.fetchAll(0, 10, "");
    } catch (e: any) {
      expect(e.message).toEqual("获取列表失败")
    }
  })

  it('getFeatures AP', async () => {
    try {
      vi.spyOn(http, 'get').mockRejectedValue({error: {request: "failed"}});
      await featureInstance.fetchAll(0, 10, "");
    } catch (e: any) {
      expect(e.error.request).toEqual("failed")
    }
  })
})
