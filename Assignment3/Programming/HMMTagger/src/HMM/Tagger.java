package HMM;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;


public class Tagger {
    private static String preTag;
    private static String currentTag;
    private static Map<String, WordTag> wordMap = new HashMap<>();
    private static Map<String, TagTag> tagMap = new HashMap<>();
    private static List<WordPath> optionalOutput = new ArrayList<>();
    private static List<String> totalTags = new ArrayList<>();
    private static Stack<PathInfo> path = new Stack<>();

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
        return tagMap.get(preTag).getTagCount(currentTag) / tagMap.get(preTag).getTotalCount();
    }

    static boolean isValidNUmber (String word) {
        for (char c: word.toCharArray()) {
            if (Character.isDigit(c)) {
                continue;
            } else if (c == '.') {
                continue;
            } else if (c == '-'){
                continue;
            } else if (c == ','){
                continue;
            } else {
                return false;
            }
        }
        return true;
    }

    static TagInfo unknownWordTagInfo (String curTag, List<TagInfo> preTagList) {
        double maximumProb = 0.0;
        TagInfo newTagInfo = new TagInfo();
        String fromTag = null;
        for (TagInfo tagInfo : preTagList) {
            double prob;
            if(tagMap.get(tagInfo.getTag()).getTagCount(curTag) == 0) {
                prob = Math.max(maximumProb, tagInfo.getProb() * 1.0 * (1.0 / tagMap.get(tagInfo.getTag()).getTotalCount()) * 10.0);
            } else {
                prob = Math.max(maximumProb, tagInfo.getProb() * 1.0 * transPro(tagInfo.getTag(), curTag) * 10.0);}
            if (maximumProb != prob) {
                maximumProb = prob;
                fromTag = tagInfo.getTag();
            }
        }

//        newTagInfo.setFromTag(fromTag);
//        newTagInfo.setProb(maximumProb);
//        newTagInfo.setTag(curTag);
//        return newTagInfo;
        return newTagInfo.createTagInfo(fromTag, curTag, maximumProb);
    }

    static List<TagInfo> specialTagCase (String curTag, List<TagInfo> currTagList, List<TagInfo> preTagList) {
        TagInfo newTagInfo = unknownWordTagInfo(curTag, preTagList);
        currTagList.add(newTagInfo);
        return currTagList;
    }

    static List<TagInfo> calcUnknownWord (String word, List<TagInfo> preTagList) {
        List<TagInfo> currTagList = new ArrayList<>();
        if (word.substring(word.length() - 1).equals("s")) {
//            String  curTag = "NNS";
//            TagInfo newTagInfo = unknownWordTagInfo(curTag, preTagList);
//            currTagList.add(newTagInfo);
            specialTagCase("NNS", currTagList, preTagList);
        }
        else if (word.charAt(0) != '-' && word.contains("-")) {
//            String curTag = "JJ";
//            TagInfo newTagInfo = unknownWordTagInfo(curTag, preTagList);
//            currTagList.add(newTagInfo);
//            currTagList.add(newTagInfo);
            specialTagCase("JJ", currTagList, preTagList);
        }
        else if (word.length() >= 2 && word.substring(word.length() - 2).equals("ed")) {
//            String curTag = "VBN";
//            TagInfo newTagInfo = unknownWordTagInfo(curTag, preTagList);
//            currTagList.add(newTagInfo);
//            currTagList.add(newTagInfo);
            specialTagCase("VBN", currTagList, preTagList);
        } else if (word.length() >= 2 && word.substring(word.length() - 2).equals("ly")) {
//            String curTag = "RB";
//            TagInfo newTagInfo = unknownWordTagInfo(curTag, preTagList);
//            currTagList.add(newTagInfo);
//            currTagList.add(newTagInfo);
            specialTagCase("RB", currTagList, preTagList);
        } else if (word.length() >= 4 && word.substring(word.length() - 4).equals("able")) {
//            String curTag = "JJ";
//            TagInfo newTagInfo = unknownWordTagInfo(curTag, preTagList);
//            currTagList.add(newTagInfo);
//            currTagList.add(newTagInfo);
            specialTagCase("JJ", currTagList, preTagList);
        } else if (word.length() >= 3 && word.substring(word.length() - 3).equals("ing")) {
//            String curTag = "VBG";
//            TagInfo newTagInfo = unknownWordTagInfo(curTag, preTagList);
//            currTagList.add(newTagInfo);
//            currTagList.add(newTagInfo);
            specialTagCase("VBG", currTagList, preTagList);
        } else if (Character.isUpperCase(word.charAt(0)) && word.substring(word.length() - 1).equals("s")) {
//            String curTag = "NNPS";
//            TagInfo newTagInfo = unknownWordTagInfo(curTag, preTagList);
//            currTagList.add(newTagInfo);
//            currTagList.add(newTagInfo);
            specialTagCase("NNPS", currTagList, preTagList);
        } else if (Character.isUpperCase(word.charAt(0))) {
//            String curTag = "NNP";
//            TagInfo newTagInfo = unknownWordTagInfo(curTag, preTagList);
//            currTagList.add(newTagInfo);
//            currTagList.add(newTagInfo);
            specialTagCase("NNP", currTagList, preTagList);
        } else if (isValidNUmber(word)) {
//            String curTag = "CD";
//            TagInfo newTagInfo = unknownWordTagInfo(curTag, preTagList);
//            currTagList.add(newTagInfo);
//            currTagList.add(newTagInfo);
            specialTagCase("CD", currTagList, preTagList);
        } else {
            for (String curTag : totalTags) {
                double maximumProb = 0.0;
                TagInfo newTagInfo = new TagInfo();
                String fromTag = null;
                for (TagInfo tagInfo : preTagList) {
                    double prob = Math.max(maximumProb, tagInfo.getProb() * 1.0 * transPro(tagInfo.getTag(), curTag) * 10.0);
                    if (maximumProb != prob) {
                        maximumProb = prob;
                        fromTag = tagInfo.getTag();
                    }
                }

//                    newTagInfo.setFromTag(fromTag);
//                    newTagInfo.setProb(maximumProb);
//                    newTagInfo.setTag(curTag);
                newTagInfo.createTagInfo(fromTag, curTag, maximumProb);
                currTagList.add(newTagInfo);
            }
        }
        WordPath wordPath = new WordPath();
        wordPath.setWordPath(word, currTagList);
        optionalOutput.add(wordPath);
        return currTagList;
    }

    static List<TagInfo> calcProb (String word, List<TagInfo> preTagList) {
        if (wordMap.containsKey(word)) {
            List<TagInfo> currTagList = new ArrayList<>();
            WordTag tags;
            tags = wordMap.get(word);
            for (Map.Entry<String, Double> entry : tags.getTagMap().entrySet()) {
                double maximumProb = 0.0;
                TagInfo newTagInfo = new TagInfo();
                String fromTag = null;
                String curTag = entry.getKey();
                for (TagInfo tagInfo : preTagList) {
                    int mapSize = tags.getTagMap().size();
                    double prob;
                    if (mapSize == 1 && tagMap.get(tagInfo.getTag()).getTagCount(curTag) == 0) {
                        prob = Math.max(maximumProb, tagInfo.getProb() * emitPro(curTag, tags) * (1.0 / tagMap.get(tagInfo.getTag()).getTotalCount()) * 10.0);
                    } else {
                        prob = Math.max(maximumProb, tagInfo.getProb() * emitPro(curTag, tags) * transPro(tagInfo.getTag(), curTag) * 10.0);
                    }
                    if (maximumProb != prob) {
                        maximumProb = prob;
                        fromTag = tagInfo.getTag();
                    }
                }
//                newTagInfo.setFromTag(fromTag);
//                newTagInfo.setProb(maximumProb);
//                newTagInfo.setTag(curTag);
                newTagInfo.createTagInfo(fromTag, curTag, maximumProb);
                currTagList.add(newTagInfo);
            }
            WordPath wordPath = new WordPath();
            wordPath.setWordPath(word, currTagList);
            optionalOutput.add(wordPath);
            return currTagList;
        } else {
            return calcUnknownWord(word, preTagList);
        }
//            if (word.substring(word.length() - 1).equals("s")) {
//                String  curTag = "NNS";
//                TagInfo newTagInfo = unknownWordTagInfo(curTag, preTagList);
//                currTagList.add(newTagInfo);
//            } else if (word.length() >= 2 && word.substring(word.length() - 2).equals("ed")) {
//                String curTag = "VBN";
//                TagInfo newTagInfo = unknownWordTagInfo(curTag, preTagList);
//                currTagList.add(newTagInfo);
//                currTagList.add(newTagInfo);
//            } else if (word.length() >= 4 && word.substring(word.length() - 4).equals("able")) {
//                String curTag = "JJ";
//                TagInfo newTagInfo = unknownWordTagInfo(curTag, preTagList);
//                currTagList.add(newTagInfo);
//                currTagList.add(newTagInfo);
//            } else if (word.length() >= 3 && word.substring(word.length() - 3).equals("ing")) {
//                String curTag = "VBG";
//                TagInfo newTagInfo = unknownWordTagInfo(curTag, preTagList);
//                currTagList.add(newTagInfo);
//                currTagList.add(newTagInfo);
//            } else if (Character.isUpperCase(word.charAt(0)) && word.substring(word.length() - 1).equals("s")) {
//                String curTag = "NNPS";
//                TagInfo newTagInfo = unknownWordTagInfo(curTag, preTagList);
//                currTagList.add(newTagInfo);
//                currTagList.add(newTagInfo);
//            } else if (Character.isUpperCase(word.charAt(0))) {
//                String curTag = "NNP";
//                TagInfo newTagInfo = unknownWordTagInfo(curTag, preTagList);
//                currTagList.add(newTagInfo);
//                currTagList.add(newTagInfo);
//            } else {
//                for (String curTag : totalTags) {
//                    double maximumProb = 0.0;
//                    TagInfo newTagInfo = new TagInfo();
//                    String fromTag = null;
//                    for (TagInfo tagInfo : preTagList) {
//                        double prob = Math.max(maximumProb, tagInfo.getProb() * 1.0 * transPro(tagInfo.getTag(), curTag) * 10.0);
//                        if (maximumProb != prob) {
//                            maximumProb = prob;
//                            fromTag = tagInfo.getTag();
//                        }
//                    }
//
////                    newTagInfo.setFromTag(fromTag);
////                    newTagInfo.setProb(maximumProb);
////                    newTagInfo.setTag(curTag);
//                    newTagInfo.createTagInfo(fromTag, curTag, maximumProb);
//                    currTagList.add(newTagInfo);
//                }
//            }
//            WordPath wordPath = new WordPath();
//            wordPath.setWordPath(word, currTagList);
//            optionalOutput.add(wordPath);
//        }
//        return currTagList;
    }

    static void calcEndPro (List<TagInfo> preTagList) {
        List<TagInfo> currTagList = new ArrayList<>();
        double maximumProb = 0.0;
        TagInfo newTagInfo = new TagInfo();
        String fromTag = null;
        for (TagInfo tagInfo : preTagList) {
            double prob = Math.max(maximumProb, tagInfo.getProb() * transPro(tagInfo.getTag(), "End") * 10.0);
            if (maximumProb != prob) {
                maximumProb = prob;
                fromTag = tagInfo.getTag();
            }
        }
//        newTagInfo.setFromTag(fromTag);
//        newTagInfo.setProb(maximumProb);
//        newTagInfo.setTag("End");
        newTagInfo.createTagInfo(fromTag, "End", maximumProb);
        currTagList.add(newTagInfo);
        WordPath wordPath = new WordPath();
        wordPath.setWordPath("", currTagList);
        optionalOutput.add(wordPath);
    }

    static void tagPath (BufferedWriter bw) throws IOException{
        List<TagInfo> tagInfoList;
        tagInfoList = optionalOutput.get(optionalOutput.size() - 1).getPath();
        TagInfo tagInfo = tagInfoList.get(0);
        String toTag = tagInfo.getFromTag();
        for (int i = optionalOutput.size() - 2; i >= 0 ; i--) {
            String word;
            word = optionalOutput.get(i).getWord();
            tagInfoList = optionalOutput.get(i).getPath();
            for (TagInfo info : tagInfoList) {
                if (info.getTag().equals(toTag)) {
                    PathInfo pathInfo = new PathInfo();
                    pathInfo.setPathInfo(word, toTag);
                    path.push(pathInfo);
                    toTag = info.getFromTag();
                    break;
                }
            }
        }
        optionalOutput.clear();
        writeFile(bw);
    }


    static List<TagInfo> intiPreTagList() {
        TagInfo tagInfo = new TagInfo();
        tagInfo.setTag("Start");
        tagInfo.setProb(1.0);
        List<TagInfo> intiTagInfo = new ArrayList<>();
        intiTagInfo.add(tagInfo);
        return intiTagInfo;
    }

    static void taggingWords (String testSet, BufferedWriter bw) throws IOException {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(testSet));
            String line;
            List<TagInfo> preTagList = intiPreTagList();
            while ((line = reader.readLine()) != null) {
                if (line.length() > 0) {
                    preTagList = calcProb(line, preTagList);
                } else {
                    calcEndPro(preTagList);
                    tagPath(bw);
                    System.out.println("\n");
                    preTagList.clear();
                    preTagList = intiPreTagList();
                }
            }

        } catch (IOException e) {
            System.out.println("Not found test set.");
        }
    }

    public static void writeFile(BufferedWriter bw) throws IOException {
        while(!path.empty()) {
            PathInfo pathInfo;
            pathInfo = path.pop();
            bw.write(pathInfo.getWord() + "\t" + pathInfo.getPath());
            bw.newLine();
        }
        bw.newLine();
    }

    public static void main (String[] args) throws IOException {
        File file = new File("24_better.pos");
        FileOutputStream fos = new FileOutputStream(file);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
        parseCorpus(args[0]);
        taggingWords(args[1], bw);
        bw.close();
    }
}
