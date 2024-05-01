package cn.dpc.ab.api.ai.bot;

import lombok.Getter;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;

import java.util.List;

import static cn.dpc.ab.api.ai.config.EtlConfig.INTENTION;

/**
 * Intention of the user.
 */
@Getter
public enum Intention {
    QA(),

    ADD_FEATURE(
            List.of(Slot.builder()
                            .title("特性键")
                            .description("特性的唯一标识，只能由英文字母数字和下划线组成")
                            .required(true)
                            .build(),
                    Slot.builder()
                            .title("特性名称")
                            .description("特性的名称")
                            .required(true)
                            .build(),
                    Slot.builder()
                            .title("特性描述")
                            .description("特性的描述")
                            .required(false)
                            .build()
            ),
            Slot.SLOT_DESCRIPTION
                    + """
                    \s
                     需求：
                     #01 根据用户输入信息提取有用的信息更新到JSON中的value字段并返回更新后的JSON
                     #02 仅更新value字段，其他字段都原样返回
                     #03 如果没有可更新的value则原样返回
                    \s
                     参考示例：
                     '''
                     JSON: [
                         {{
                             "title": "特性键",
                             "description": "特性的唯一标识，只能由英文字母数字和下划线组成",
                             "transform": "",
                             "util": "",
                             "value": "",
                             "required": true
                         }},
                         {{
                             "title": "特性名称",
                             "description": "特性的名称",
                             "transform": "",
                             "util": "",
                             "value": "",
                             "required": true
                         }},
                         {{
                             "title": "特性描述",
                             "description": "特性的描述",
                             "transform": "",
                             "util": "",
                             "value": "",
                             "required": false
                         }}
                     ]
                    \s
                     user：请帮我创建一个键是HOME_TITLE的特性
                    \s
                     assistant：[
                         {{
                             "title": "特性键",
                             "description": "特性的唯一标识，只能由英文字母数字和下划线组成",
                             "transform": "",
                             "util": "",
                             "value": "HOME_TITLE",
                             "required": true
                         }},
                         {{
                             "title": "特性名称",
                             "description": "特性的名称",
                             "transform": "",
                             "util": "",
                             "value": "",
                             "required": true
                         }},
                         {{
                             "title": "特性描述",
                             "description": "特性的描述",
                             "transform": "",
                             "util": "",
                             "value": "",
                             "required": false
                         }}
                     ]
                     '''
                    \s
                     JSON：{slots}
                    \s""",
            Slot.SLOT_DESCRIPTION
                    + """
                    
                    需求：
                    #01 通过提问让用户补充未填写的value字段
                    #02 如果有多个未填写value的参数则可以一起向用户提问
                    #03 value已经填写的参数不用再次提问
                    
                    参考示例：
                    '''
                    user: [
                        {{
                            "title": "特性键",
                            "description": "特性的唯一标识，只能由英文字母数字和下划线组成",
                            "transform": "",
                            "util": "",
                            "value": "HOME_TITLE",
                            "required": true
                        }},
                        {{
                            "title": "特性名称",
                            "description": "特性的名称",
                            "transform": "",
                            "util": "",
                            "value": "",
                            "required": true
                        }},
                        {{
                            "title": "特性描述",
                            "description": "特性的描述",
                            "transform": "",
                            "util": "",
                            "value": "",
                            "required": false
                        }}
                    ]
                    
                    assistant：请问特性名称是什么？
                    '''
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

    /**
     * Gets intention by message.
     *
     * @param message           the message
     * @param historyVectorStore the history vector store
     * @return the intention by message
     */
    public static Intention getIntentionByMessage(String message, SimpleVectorStore historyVectorStore) {
        List<Document> intentions = historyVectorStore.similaritySearch(
                SearchRequest.query(message)
                        .withSimilarityThreshold(0.85F)
                        .withTopK(1));
        if (intentions.isEmpty()) {
            return Intention.OTHER;
        }

        if (intentions.get(0).getMetadata().get(INTENTION).equals(ADD_FEATURE.name())) {
            return ADD_FEATURE;
        }

        return Intention.QA;
    }

}
