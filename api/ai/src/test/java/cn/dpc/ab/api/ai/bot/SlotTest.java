package cn.dpc.ab.api.ai.bot;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SlotTest {

    @Test
    void extractSlotsFromContent() {
        String content = """
                assistant: [
                {
                    “title”: “特性键”,
                    “value”: “HomeHome”,
                    “description”: “特性的唯一标识，只能由英文字母数字和下划线组成”,
                    “required”: true,
                    “transform”: null,
                    “util”: null
                },{
                    “title”: “特性名称”,
                    “value”: null,
                    “description”: “特性的名称”,
                    “required”: true,
                    “transform”: null,
                    “util”: null
                },{
                    “title”: “特性描述”,
                    “value”: null,
                    “description”: “特性的描述”,
                    “required”: false,
                    “transform”: null,
                    “util”: null
                }]
                """;
        List<Slot> slots = Slot.extractSlotsFromContent(content, new ObjectMapper());
        assertEquals(3, slots.size());
    }
}