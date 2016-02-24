package HMM;


import java.util.List;

public class PreWordTag {
    private String word;
    private List<String> tags;

    public void setWord (String word) {
        this.word = word;
    }

    public void addTag (String tag) {
        tags.add(tag);
    }

    public List<String> getTags () {
        return this.tags;
    }
}
