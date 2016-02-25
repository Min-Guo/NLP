package HMM;


import java.util.HashMap;
import java.util.Map;

public class WordTag {
    private Map<String, Double> tagMap;
    private int totalWordCount;

    public WordTag () {
        this.tagMap = new HashMap<>();
    }

    public Map<String, Double> getTagMap () {
        return this.tagMap;
    }

    public Double getWordTagCount (String tag) {
        if (tagMap.containsKey(tag)) {
            return tagMap.get(tag);
        } else {
            return 0.0;
        }
    }

    public void setTagMap (String tag) {
        totalWordCount ++;
        if (tagMap.containsKey(tag)) {
            Double count = tagMap.get(tag);
            tagMap.put(tag, count + 1);
        } else {
            tagMap.put(tag, 1.0);
        }
    }
}
