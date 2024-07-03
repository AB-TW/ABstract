package cn.dpc.ab.domain.ai;

/**
 * A message from a chatBot.
 */
public record ChatbotMessage(String sender, String text) {
}
