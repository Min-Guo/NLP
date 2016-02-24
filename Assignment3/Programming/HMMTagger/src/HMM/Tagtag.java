package HMM;


import java.util.HashMap;
import java.util.Map;

public class TagTag {
    private Map<String, Integer> tagMap;
    private int totalTagCount;

    public TagTag () {
        this.tagMap = new HashMap<>();
    }

    public Integer getTotalCount () {
        return this.totalTagCount;
    }

    public Integer getTagCount (String tag) {
        if (tagMap.containsKey(tag)) {
            return tagMap.get(tag);
        } else {
            return 0;
        }
    }

    public void setTagMap (String tag) {
        totalTagCount ++;
        if (tagMap.containsKey(tag)) {
            Integer count = tagMap.get(tag);
            tagMap.put(tag, count + 1);
        } else {
            tagMap.put(tag, 1);
        }
    }
}
