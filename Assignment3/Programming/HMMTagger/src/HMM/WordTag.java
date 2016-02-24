package HMM;


import java.util.HashMap;
import java.util.Map;

public class WordTag {
    private Map<String, Integer> tagMap;
    private int totalWordCount;

    public WordTag () {
        this.tagMap = new HashMap<>();
    }

    public Map<String, Integer> getTagMap () {
        return this.tagMap;
    }

    public Integer getWordTagCount (String tag) {
        return tagMap.get(tag);
    }

    public void setTagMap (String tag) {
        totalWordCount ++;
        if (tagMap.containsKey(tag)) {
            Integer count = tagMap.get(tag);
            tagMap.put(tag, count + 1);
        } else {
            tagMap.put(tag, 1);
        }
    }
}
