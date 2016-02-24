package HMM;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class Tagger {
    private static String preTag;
    private static String currentTag;
    private static Map<String, WordTag> wordMap = new HashMap<>();
    private static Map<String, TagTag> tagMap = new HashMap<>();
    private static Map<String, List<PreTagInfo>> optionalOutput = new HashMap<>();

    static void parseFirstLine (String firstLine) {
        String[] splitString = firstLine.split("\t");
        WordTag wordTag = new WordTag();
        wordTag.setTagMap(splitString[1]);
        wordMap.put(splitString[0], wordTag);
        preTag = splitString[1];
    }

    static void setWordMap (String word, String tag) {
        if (wordMap.containsKey(word)) {
            wordMap.get(word).setTagMap(tag);
        } else {
            WordTag wordTag = new WordTag();
            wordTag.setTagMap(tag);
            wordMap.put(word, wordTag);
        }
    }

    static void setTagMap (String preTag, String currentTag) {
        if (tagMap.containsKey(preTag)) {
            tagMap.get(preTag).setTagMap(currentTag);
        } else {
            TagTag tagTag = new TagTag();
            tagTag.setTagMap(currentTag);
            tagMap.put(preTag, tagTag);
        }
    }

    static void parseCorpus (String trainingSet) throws IOException {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(trainingSet));
            String line;
            line = reader.readLine();
            parseFirstLine(line);
            while ((line = reader.readLine()) != null) {
                if (line.length() > 0 ) {
                    String[] splitString = line.split("\t");
                    currentTag = splitString[1];
                    setWordMap(splitString[0], currentTag);
                    setTagMap(preTag, currentTag);
                    preTag = currentTag;
                } else {
                    setTagMap(preTag, "End");
                    preTag = "Start";
                }
            }
        } catch (IOException e) {
            System.out.println("Not found training set.");
        }
    }

    static double emitPro (String word, String tag, WordTag tags) {
        return tags.getTagMap().get(tag) / tagMap.get(tag).getTotalCount();
    }

    static double transPro (String preTag, String currentTag) {
        return tagMap.get(currentTag).getTagCount(preTag) / tagMap.get(preTag).getTotalCount();
    }

    static void calcProb (String word) {
        if (tagMap.containsKey(word)) {
            WordTag tags;
            tags = wordMap.get(word);
            for (Map.Entry<String, Integer> entry : tags.getTagMap().entrySet()) {
                List<PreTagInfo> preTagList = null;
                double maximumProb = 0.0;
                PreTagInfo newTagInfo = new PreTagInfo();
                String fromTag = null;
                String curTag = entry.getKey();
                for (PreTagInfo preTagInfo: preTagList) {
                    double prob = Math.max(maximumProb, preTagInfo.getProb() * emitPro(word, curTag, tags) * transPro(preTagInfo.getTag(), curTag));
                    if (maximumProb != prob) {
                        maximumProb = prob;
                        fromTag = preTagInfo.getTag();
                    }
                }
                newTagInfo.setFromTag(fromTag);
                newTagInfo.setProb(maximumProb);
                newTagInfo.setTag(curTag);
                preTagList.add(newTagInfo);
                optionalOutput.put(word, preTagList);
            }
        } else {

        }
    }

    static void taggingWords (String testSet) throws IOException {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(testSet));
            String line;
            PreTagInfo tagInfo = new PreTagInfo();
            tagInfo.setTag("Start");
            tagInfo.setProb(1.0);
            List<PreTagInfo> intiTagInfo = new ArrayList<>();
            intiTagInfo.add(tagInfo);
            optionalOutput.put("", intiTagInfo);
            while ((line = reader.readLine()) != null) {
                if (line.length() > 0) {

                } else {

                }
            }

        } catch (IOException e) {
            System.out.println("Not found test set.");
        }
    }

    public static void main (String[] args) throws IOException {
        parseCorpus(args[0]);
        System.out.println("code");
    }
}
