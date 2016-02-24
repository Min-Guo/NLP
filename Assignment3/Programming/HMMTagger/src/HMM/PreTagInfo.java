package HMM;


public class PreTagInfo {
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
}
