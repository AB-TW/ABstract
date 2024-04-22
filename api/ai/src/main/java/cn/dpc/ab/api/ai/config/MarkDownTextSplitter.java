package cn.dpc.ab.api.ai.config;

import org.springframework.ai.transformer.splitter.TextSplitter;

import java.util.ArrayList;
import java.util.List;

/**
 * A text splitter that splits text into chunks of a target size in tokens.
 */
public class MarkDownTextSplitter extends TextSplitter {
    /**
     * The default chunk size.
     */
    private int defaultChunkSize = 800;

    /**
     * The minimum chunk size in characters.
     */
    private int minChunkSizeChars = 350;


    public MarkDownTextSplitter() {
    }


    public MarkDownTextSplitter(int defaultChunkSize, int minChunkSizeChars) {
        this.defaultChunkSize = defaultChunkSize;
        this.minChunkSizeChars = minChunkSizeChars;
    }

    @Override
    protected List<String> splitText(String text) {
        return split(text, this.defaultChunkSize);
    }

    /**
     * Splits the given text into chunks of the target size.
     *
     * @param text      the text to split
     * @param chunkSize the target size of each chunk
     * @return the list of chunks
     */
    public List<String> split(String text, int chunkSize) {
        if (text == null || text.trim().isEmpty()) {
            return new ArrayList<>();
        }

        List<String> chunks = new ArrayList<>();
        if (text.length() < minChunkSizeChars) {
            chunks.add(text);
            return chunks;
        }

        String[] splitText = text.split("\n");

        String parentTitle = "";
        split(chunkSize, splitText, "# ", parentTitle, chunks);

        return chunks;
    }

    private void split(int chunkSize, String[] splitText, String splitToken, String parentTitle, List<String> chunks) {
        int index = 0;
        while (index < splitText.length) {
            String title = "";
            String line = splitText[index++];
            if (line.trim().startsWith(splitToken)) {
                title = line.trim();
            }

            List<String> contents = new ArrayList<>();

            while (index < splitText.length && !splitText[index].trim().startsWith(splitToken)) {
                contents.add(splitText[index]);
                index++;
            }

            int count = parentTitle.length()
                    + (title + "\n").length()
                    + contents.stream().map(item -> (item + "\n").length()).reduce(0, Integer::sum);
            if (count < chunkSize) {
                chunks.add(parentTitle + title + "\n" + String.join("\n", contents));
            } else {
                split(chunkSize, contents.toArray(new String[0]), "#" + splitToken, parentTitle + title + "\n", chunks);
            }
        }
    }


}
