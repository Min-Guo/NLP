package HMM;


import java.util.HashMap;
import java.util.Map;

public class TagTag {
    private Map<String, Double> tagMap;
    private Double totalTagCount = 0.0;

    public TagTag () {
        this.tagMap = new HashMap<>();
    }

    public Double getTotalCount () {
        return this.totalTagCount;
    }

    public Double getTagCount (String tag) {
        if (tagMap.containsKey(tag)) {
            return tagMap.get(tag);
        } else {
            return 0.0;
        }
    }

    public void setTagMap (String tag) {
        totalTagCount ++;
        if (tagMap.containsKey(tag)) {
            Double count = tagMap.get(tag);
            tagMap.put(tag, count + 1.0);
        } else {
            tagMap.put(tag, 1.0);
        }
    }
}
