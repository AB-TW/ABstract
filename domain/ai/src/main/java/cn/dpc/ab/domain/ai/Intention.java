package cn.dpc.ab.domain.ai;

import lombok.Getter;

import java.util.List;


/**
 * cn.dpc.ab.domain.ai.Intention of the user.
 */
@Getter
public enum Intention {
    QA(),
    QUERY_WITH_FUNCTION(),
    ADD_FEATURE(
            List.of(Slot.builder()
                            .title("key")
                            .description("特性键，特性的的唯一标识，只能由英文字母数字和下划线组成")
                            .required(true)
                            .build(),
                    Slot.builder()
                            .title("name")
                            .description("特性的名称")
                            .required(true)
                            .build(),
                    Slot.builder()
                            .title("description")
                            .description("特性的描述")
                            .required(false)
                            .build()
            ),
            Slot.SLOT_DESCRIPTION
                    + """
                    
                    Instructions:
                    #01 Extract relevant information from user input and update the "value" field in the JSON, returning the updated JSON.
                    #02 Only modify the "value" field; keep other fields unchanged.
                    #03 If there's no value to update, return the JSON as is.
                    #04 Just return JSON array result.
                    
                    Example:
                    ===Example Start===
                    SLOTS: [
                         {{
                             "title": "key",
                             "description": "特性的唯一标识，只能由英文字母数字和下划线组成",
                             "transform": "",
                             "util": "",
                             "value": "",
                             "required": true
                         }},
                         {{
                             "title": "name",
                             "description": "特性的名称",
                             "transform": "",
                             "util": "",
                             "value": "",
                             "required": true
                         }},
                         {{
                             "title": "description",
                             "description": "特性的描述",
                             "transform": "",
                             "util": "",
                             "value": "",
                             "required": false
                         }}
                    ]
                    
                    user: 请帮我创建一个key是HOME_TITLE的特性
                    
                    you return: [
                         {{
                             "title": "key",
                             "description": "特性的唯一标识，只能由英文字母数字和下划线组成",
                             "transform": "",
                             "util": "",
                             "value": "HOME_TITLE",
                             "required": true
                         }},
                         {{
                             "title": "name",
                             "description": "特性的名称",
                             "transform": "",
                             "util": "",
                             "value": "",
                             "required": true
                         }},
                         {{
                             "title": "description",
                             "description": "特性的描述",
                             "transform": "",
                             "util": "",
                             "value": "",
                             "required": false
                         }}
                    ]
                    ===Example End===
                                        
                    SLOTS: {slots}
                    """,

            Slot.SLOT_DESCRIPTION
                    + """
                    
                    Instructions:
                          #01 Prompt users to complete unfilled "value" fields through questions.
                          #02 If there are multiple parameters with unfilled "value" fields, ask questions together.
                          #03 Skip parameters with already filled values.
                          #04 Just return the question in Chinese.
                    
                    Example：
                    ===Example Start===
                    '''
                    SLOTS: [
                        {{
                            "title": "key",
                            "description": "特性的唯一标识，只能由英文字母数字和下划线组成",
                            "transform": "",
                            "util": "",
                            "value": "HOME_TITLE",
                            "required": true
                        }},
                        {{
                            "title": "name",
                            "description": "特性的名称",
                            "transform": "",
                            "util": "",
                            "value": "",
                            "required": true
                        }},
                        {{
                            "title": "description",
                            "description": "特性的描述",
                            "transform": "",
                            "util": "",
                            "value": "",
                            "required": false
                        }}
                    ]
                    
                    you return: 请问特性名称是什么？
                    ===Example End===
                    
                    SLOTS: {slots}
                    """),

    OTHER();


    private final List<Slot> slots;
    private final String updatePrompt;
    private final String queryPrompt;

    Intention() {
        this.slots = List.of();
        this.updatePrompt = "";
        this.queryPrompt = "";
    }

    Intention(List<Slot> slots, String updatePrompt, String queryPrompt) {
        this.slots = slots;
        this.updatePrompt = updatePrompt;
        this.queryPrompt = queryPrompt;
    }

}
