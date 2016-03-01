package HMM;



import java.util.List;

public class WordPath {
    private String word;
    private List<TagInfo> path;

    public void setWordPath (String word, List<TagInfo> path) {
        this.word = word;
        this.path = path;
    }

    public List<TagInfo> getPath() {
        return this.path;
    }

    public String getWord() {
        return this.word;
    }
}
