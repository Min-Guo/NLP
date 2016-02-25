package HMM;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Tagger {
    private static String preTag;
    private static String currentTag;
    private static Map<String, WordTag> wordMap = new HashMap<>();
    private static Map<String, TagTag> tagMap = new HashMap<>();
    //private static Map<String, List<TagInfo>> optionalOutput = new HashMap<>();
    private static List<String> totalTags = new ArrayList<>();
    private static Stack<List<TagInfo>> optionalOutput = new Stack<>();

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
                    if (!totalTags.contains(splitString[1])) {
                        totalTags.add(splitString[1]);
                    }
                } else {
                    setTagMap(preTag, "End");
                    preTag = "Start";
                }
            }
        } catch (IOException e) {
            System.out.println("Not found training set.");
        }
    }

    static double emitPro (String tag, WordTag tags) {
        return tags.getTagMap().get(tag) / tagMap.get(tag).getTotalCount();
    }

    static double transPro (String preTag, String currentTag) {
        return tagMap.get(currentTag).getTagCount(preTag) / tagMap.get(preTag).getTotalCount();
    }

    static List<TagInfo> calcProb (String word, List<TagInfo> preTagList) {
        List<TagInfo> currTagList = new ArrayList<>();
        if (tagMap.containsKey(word)) {
            WordTag tags;
            tags = wordMap.get(word);
            for (Map.Entry<String, Integer> entry : tags.getTagMap().entrySet()) {
                double maximumProb = 0.0;
                TagInfo newTagInfo = new TagInfo();
                String fromTag = null;
                String curTag = entry.getKey();
                for (TagInfo tagInfo : preTagList) {
                    double prob = Math.max(maximumProb, tagInfo.getProb() * emitPro(curTag, tags) * transPro(tagInfo.getTag(), curTag));
                    if (maximumProb != prob) {
                        maximumProb = prob;
                        fromTag = tagInfo.getTag();
                    }
                }
                newTagInfo.setFromTag(fromTag);
                newTagInfo.setProb(maximumProb);
                newTagInfo.setTag(curTag);
                currTagList.add(newTagInfo);
            }
            optionalOutput.push(currTagList);
        } else {
            for (String curTag: totalTags) {
                double maximumProb = 0.0;
                TagInfo newTagInfo = new TagInfo();
                String fromTag = null;
                for (TagInfo tagInfo : preTagList) {
                    double prob = Math.max(maximumProb, tagInfo.getProb() * 1.0 * transPro(tagInfo.getTag(), curTag));
                    if (maximumProb != prob) {
                        maximumProb = prob;
                        fromTag = tagInfo.getTag();
                    }
                }
                newTagInfo.setFromTag(fromTag);
                newTagInfo.setProb(maximumProb);
                newTagInfo.setTag(curTag);
                currTagList.add(newTagInfo);
            }
        }
        return currTagList;
    }

    static void calcEndPro (List<TagInfo> preTagList) {
        List<TagInfo> currTagList = new ArrayList<>();
        double maximumProb = 0.0;
        TagInfo newTagInfo = new TagInfo();
        String fromTag = null;
        for (TagInfo tagInfo : preTagList) {
            double prob = Math.max(maximumProb, tagInfo.getProb() * transPro(tagInfo.getTag(), "End"));
            if (maximumProb != prob) {
                maximumProb = prob;
                fromTag = tagInfo.getTag();
            }
        }
        newTagInfo.setFromTag(fromTag);
        newTagInfo.setProb(maximumProb);
        newTagInfo.setTag("End");
        currTagList.add(newTagInfo);
        optionalOutput.push(currTagList);
    }

    static void tagPath () {
        Stack<String> path = new Stack<>();
        List<TagInfo> tagInfoList;
        tagInfoList = optionalOutput.pop();
        TagInfo tagInfo = tagInfoList.get(0);
        String toTag = tagInfo.getFromTag();
        while (!optionalOutput.empty()) {
            tagInfoList = optionalOutput.pop();
            for (TagInfo info: tagInfoList) {
                if (info.getTag().equals(toTag)) {
                    toTag = info.getFromTag();
                    path.push(toTag);
                }
            }
        }
    }

    static void taggingWords (String testSet) throws IOException {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(testSet));
            String line;
            TagInfo tagInfo = new TagInfo();
            tagInfo.setTag("Start");
            tagInfo.setProb(1.0);
            List<TagInfo> intiTagInfo = new ArrayList<>();
            intiTagInfo.add(tagInfo);
            optionalOutput.push(intiTagInfo);
            List<TagInfo> preTagList = intiTagInfo;
            while ((line = reader.readLine()) != null) {
                if (line.length() > 0) {
                    preTagList = calcProb(line, preTagList);
                    optionalOutput.push(preTagList);
                } else {
                    calcEndPro(preTagList);
                }
            }

        } catch (IOException e) {
            System.out.println("Not found test set.");
        }
    }

    public static void main (String[] args) throws IOException {
        parseCorpus(args[0]);
        taggingWords(args[1]);
        tagPath();
        System.out.println("code");
    }
}
