package HMM;


import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class TagTag {
    private Map<String, Integer> tagMap;

    public TagTag () {
        this.tagMap = new HashMap<>();
    }

    public void setTagMap (String tag) {
        if (tagMap.containsKey(tag)) {
            Integer count = tagMap.get(tag);
            tagMap.put(tag, count + 1);
        } else {
            tagMap.put(tag, 1);
        }
    }
}
