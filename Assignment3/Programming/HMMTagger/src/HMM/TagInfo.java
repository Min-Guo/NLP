package HMM;


public class TagInfo {
    private String tag;
    private double prob;
    private String fromTag;

    public String getTag () {
        return this.tag;
    }

    public double getProb () {
        return this.prob;
    }

    public void setFromTag (String tag) {
        this.fromTag = tag;
    }

    public void setProb (double prob) {
        this.prob = prob;
    }

    public void setTag (String tag) {
        this.tag = tag;
    }
    public String getFromTag() {
        return this.fromTag;
    }

    public TagInfo createTagInfo (String fromTag, String tag, double prob) {
        this.fromTag = fromTag;
        this.tag = tag;
        this.prob = prob;
        return this;
    }
}
