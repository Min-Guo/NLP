import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class NameTagger {
    static String curWord;
    static String nextWord;
    static String prePos;
    static String preChunk;
    static String preNameTag;
    static String curPos;
    static String curChunk;
    static String curNameTag;
    static String nextPos;
    static String nextChunk;
    static String nextNameTag;

    static void initalize() {
        curWord = "";
        nextWord = "";
        prePos = "";
        preChunk = "";
        preNameTag = "";
        curPos = "";
        curChunk = "";
        curNameTag = "";
        nextPos = "";
        nextChunk = "";
        nextNameTag = "";
    }

    static String getFileExtension(String fileName) {
        String extension = "";
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i + 1);
        }
        return extension;
    }

    static String outPutLine() {
        return curWord + "\t" + "prePos=" + prePos + "\t" + "preChunk=" + preChunk + "\t" + "preNameTag=" +
                preNameTag + "\t" + "curPos=" + curPos + "\t" + "curChunk=" + curChunk + "\t" + "curNameTag=" +
                curNameTag + "\t" + "nextPos=" + nextPos + "\t" + "nextChunk=" + nextChunk + "\t" + "nextNameTag=" +
                nextNameTag + "\t";
    }

    static void setParameter() {
        prePos = curPos;
        preChunk = curChunk;
        preNameTag = curNameTag;
        curWord = nextWord;
        curPos = nextPos;
        curChunk = nextChunk;
        curNameTag = nextNameTag;
    }

    static void parseCorpus(String fileName, BufferedWriter bw) throws IOException {
        initalize();
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line;
        if (getFileExtension(fileName).equals("pos-chunk-name")) {
            while ((line = br.readLine()) != null) {
                if(line.equals("-DOCSTART-\t-X-\tO\tO")) {
                    bw.write(line);
                    bw.newLine();
                } else if (line.length() == 0 && preNameTag =="" && prePos =="" && preChunk == "") {
                    bw.write(line);
                    bw.newLine();
                    initalize();
                } else if (line.length() == 0) {
                    nextPos = "";
                    nextChunk = "";
                    nextNameTag = "";
                    bw.write(outPutLine());
                    bw.newLine();
                    bw.write(line);
                    bw.newLine();
                    initalize();
                } else {
                    if(prePos == "" && preChunk == "" && preNameTag == "") {
                        String[] curLineSplit = line.split("\t");
                        curWord = curLineSplit[0];
                        curPos = curLineSplit[1];
                        curChunk = curLineSplit[2];
                        curNameTag = curLineSplit[3];
                        line = br.readLine();
                        if (line != null) {
                            if (line.length() != 0) {
                                String[] nextLineSpilt = line.split("\t");
                                nextWord = nextLineSpilt[0];
                                nextPos = nextLineSpilt[1];
                                nextChunk = nextLineSpilt[2];
                                nextNameTag = nextLineSpilt[3];
                            }
                            String outPutString = outPutLine();
                            bw.write(outPutString);
                            bw.newLine();
                            if (line.length() == 0) {
                                bw.write(line);
                                bw.newLine();
                                initalize();
                            } else {
                                setParameter();
                            }
                        } else {
                            break;
                        }
                    } else {
                        String[] nextLineSplit = line.split("\t");
                        nextWord = nextLineSplit[0];
                        nextPos = nextLineSplit[1];
                        nextChunk = nextLineSplit[2];
                        nextNameTag = nextLineSplit[3];
                        String outPutString = outPutLine();
                        bw.write(outPutString);
                        bw.newLine();
                        setParameter();
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        File file = new File("feature-enhanced");
        FileOutputStream fos = new FileOutputStream(file);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
        parseCorpus(args[0], bw);
        bw.close();
    }
}
