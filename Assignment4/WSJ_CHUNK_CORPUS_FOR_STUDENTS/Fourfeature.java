import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class FeatureBuilder {
    static String word = "";
    static String prePos = "";
    static String curPos = "";
    static String preTag = "";
    static String curTag = "";
    static String nextPos = "";
    static String preCurPos = "";
    static String preWord = "";
    static String preCurTag = "";

    static void initalize() {
        word = "";
        prePos = "";
        curPos = "";
        preTag = "";
        curTag = "";
        nextPos = "";
        preCurPos = "";
        preWord = "";
        preCurTag = "";
    }

    static String getFileExtension(String fileName) {
        String extension = "";
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i + 1);
        }
        return extension;
    }

    static void writePCFile(BufferedWriter bw, String word, String prePos, String curPos,
                                 String preTag, String nextPos, String curTag) throws IOException {
            bw.write(word + "\t" + "prePos=" + prePos + "\t" + "curPos=" + curPos + "\t" +
            "preTag=" + preTag + "\t" + nextPos + "\t" + curTag);
            bw.newLine();
    }
    static void writePFile(BufferedWriter bw, String word, String prePos, String curPos, String nextPos,
                            String preTag) throws IOException {
        bw.write(word + "\t" + "prePos=" + prePos + "\t" + "curPos=" + curPos + "\t" + nextPos + "\t" +
                "preTag=" + preTag + "\t");
        bw.newLine();
    }

    static void writeBlankLine(BufferedWriter bw) throws IOException {
        bw.newLine();
    }

    static void parseCorpus (String fileName, BufferedWriter bw) throws IOException {
        initalize();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line;
            if (getFileExtension(fileName).equals("pos-chunk")) {
                while ((line = reader.readLine()) != null) {
                    if(line.length() > 0) {
                        String[] spiltFirstLine = line.split("\t");
                        preWord = spiltFirstLine[0];
                        preCurPos = spiltFirstLine[1];
                        preCurTag = spiltFirstLine[2];
                        break;
                    } else {
                        writeBlankLine(bw);
                    }

                }
                while ((line = reader.readLine()) != null) {
                    if (line.length() > 0) {
                        String[] splitString = line.split("\t");
                        word = splitString[0];
                        curPos = splitString[1];
                        curTag = splitString[2];
                        nextPos = curPos;
                        writePCFile(bw, preWord, prePos, preCurPos, preTag, nextPos, preCurTag);
//                        writePCFile(bw, word, prePos, curPos, preTag, curTag);
                    } else {
                        writePCFile(bw, preWord, prePos, preCurPos, preTag, "", preCurTag);
//                        writeBlankLine(bw);
                        initalize();
                    }
                    prePos = preCurPos;
                    preTag = preCurTag;
                    preWord = word;
                    preCurPos = curPos;
                    preCurTag = curTag;
//                    prePos = curPos;
//                    preTag = curTag;
                }
//                writePCFile(bw, preWord, prePos, preCurPos, preTag, "", preCurTag);
            }
            if (getFileExtension(fileName).equals("pos")) {
                while ((line = reader.readLine()) != null) {
                    if(line.length() > 0) {
                        String[] spiltFirstLine = line.split("\t");
                        preWord = spiltFirstLine[0];
                        preCurPos = spiltFirstLine[1];
                        break;
                    } else {
                        writeBlankLine(bw);
                    }

                }
                while ((line = reader.readLine()) != null) {
                    if (line.length() > 0) {
                        String[] splitString = line.split("\t");
                        word = splitString[0];
                        curPos = splitString[1];
                        nextPos = curPos;
                        writePFile(bw, preWord, prePos, preCurPos, nextPos, "@@");
                    } else {
                        writePFile(bw, preWord, prePos, preCurPos, "", "@@");
                        initalize();
                    }
                    prePos = preCurPos;
                    preWord = word;
                    preCurPos = curPos;
//                    prePos = curPos;
                }
//                writePFile(bw, preWord, prePos, preCurPos, "", "@@");
            }

        } catch (IOException e) {
            System.out.println("Not found training set.");
        }
    }

    public static void main (String[] args) throws IOException{
        File file = new File("feature-enhanced");
        FileOutputStream fos = new FileOutputStream(file);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
        parseCorpus(args[0], bw);
        bw.close();
    }
}
