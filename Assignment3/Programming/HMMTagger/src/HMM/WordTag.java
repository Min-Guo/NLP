package HMM;


import java.util.Map;

public class WordTag {
    private Map<String, Integer> tagCount;

    public void setTag (String tag) {
        if (tagCount.containsKey(tag)) {
            Integer count = tagCount.get(tag);
            tagCount.put(tag, count + 1);
        } else {
            tagCount.put(tag, 1);
        }
    }
}
