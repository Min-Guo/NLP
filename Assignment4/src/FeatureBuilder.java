import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class FeatureBuilder {
    static String word;
    static String prePos = "";
    static String curPos = "";
    static String preTag = "";
    static String curTag = "";

    static void initalize () {
        word = "";
        prePos = "";
        curPos = "";
        preTag = "";
        curTag = "";
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
                                 String preTag, String curTag) throws IOException {
            bw.write(word + "\t" + "prePos = " + prePos + "\t" + "curPos = " + curPos + "\t" +
            "preTag = " + preTag + "\t" + curTag);
            bw.newLine();
    }
    static void writePFile(BufferedWriter bw, String word, String prePos, String curPos,
                            String preTag) throws IOException {
        bw.write(word + "\t" + "prePos = " + prePos + "\t" + "curPos = " + curPos + "\t" +
                "preTag = " + preTag + "\t");
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
                    if (line.length() > 0) {
                        String[] splitString = line.split("\t");
                        word = splitString[0];
                        curPos = splitString[1];
                        curTag = splitString[2];
                        writePCFile(bw, word, prePos, curPos, preTag, curTag);
                    } else {
                        writeBlankLine(bw);
                        initalize();
                    }
                    prePos = curPos;
                    preTag = curTag;
                }
            }
            if (getFileExtension(fileName).equals("pos")) {
                while ((line = reader.readLine()) != null) {
                    if (line.length() > 0) {
                        String[] splitString = line.split("\t");
                        word = splitString[0];
                        curPos = splitString[1];
                        writePFile(bw, word, prePos, curPos, "@@");
                    } else {
                        writeBlankLine(bw);
                        initalize();
                    }
                    prePos = curPos;
                }
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
