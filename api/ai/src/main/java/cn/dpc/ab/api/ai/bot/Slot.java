package cn.dpc.ab.api.ai.bot;

import com.fasterxml.jackson.databind.ObjectMapper;
import groovy.util.logging.Slf4j;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Slot of chatBot.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class Slot {
    private static final Logger log = LoggerFactory.getLogger(Slot.class);
    private String title;
    private String value;
    private String description;
    private boolean required;
    private String transform;
    private String util;

    /**
     * The constant SLOT_DESCRIPTION.
     */
    public static final String SLOT_DESCRIPTION = """
            JSON每个元素代表一个参数信息，我先给你提供一些基本介绍：
            '''
            title字段是参数名称，如果需要让用户填写该参数时你应该告诉用户你需要的参数名称
            description字段是参数描述，可以做为title字段的补充，更好的引导用户补充参数
            transform字段是填写value字段值的格式要求说明
            required字段为true表示该元素的value是必填参数，如果value为空则必须让用户补充该参数信息，如果required字段为false表示该参数不是必须补充的字段
            '''
            """;

    /**
     * Extract slots from content.
     *
     * @param content      the content
     * @param objectMapper the object mapper
     * @return the list
     */
    public static List<Slot> extractSlotsFromContent(String content, ObjectMapper objectMapper) {
        List<Slot> slots = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\{[^}]*}");
        Matcher matcher = pattern.matcher(content.replaceAll("“", "\"").replaceAll("”", "\""));

        while (matcher.find()) {
            try {
                slots.add(objectMapper.reader().forType(Slot.class).readValue(matcher.group()));
            } catch (Exception e) {
                log.warn("Failed to extract slot from content: ", e);
            }
        }
        return slots;
    }
}
