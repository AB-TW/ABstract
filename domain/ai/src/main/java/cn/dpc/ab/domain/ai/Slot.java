package cn.dpc.ab.domain.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * cn.dpc.ab.domain.ai.Slot of chatBot.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class Slot {
    private String title;
    private String value;
    private String description;
    private boolean required;
    private String transform;

    /**
     * The constant SLOT_DESCRIPTION.
     */
    public static final String SLOT_DESCRIPTION = """
            SLOTS is a JSON Array, Each element of SLOTS represents a parameter and it include the following fields:
            '''
            "title": Parameter name. Inform users when they need to fill it.
            "description": Describes the parameter, complementing the title.
            "transform": Specifies the value format.
            "required": True means the value is mandatory. False means it's optional. Users must fill it if required.
            "value": The value of the parameter.
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
