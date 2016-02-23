package HMM;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Tagger {
    private static String preTag;
    private static String currentTag;
    private static Map<String, WordTag> wordMap = new HashMap<>();
    private static Map<String, TagTag> tagMap = new HashMap<>();

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
                String[] splitString = line.split("\t");
                currentTag = splitString[1];
                setWordMap(splitString[0], currentTag);
                setTagMap(preTag, currentTag);
                preTag = currentTag;
            }
        }catch (IOException e) {
            System.out.println("Not found training set.");
        }
    }

    public static void main (String[] args) throws IOException {
        parseCorpus(args[0]);
    }
}
