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
    static String nextCapital;
    static String curCapital;

    static void initalize(String extension) {
        curWord = "";
        nextWord = "";
        prePos = "";
        preChunk = "";
        curPos = "";
        curChunk = "";
        nextPos = "";
        nextChunk = "";
        curCapital = "";
        nextCapital = "";
        if (extension.equals("pos-chunk-name")) {
            preNameTag = "";
            curNameTag = "";
            nextNameTag = "";
        }
        if (extension.equals("pos-chunk")) {
            preNameTag = "@@";
//            curNameTag = "@@";
//            nextNameTag = "@@";
        }
    }

    static String getFileExtension(String fileName) {
        String extension = "";
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i + 1);
        }
        return extension;
    }

    static String outPutLine(String extension) {
        if (extension.equals("pos-chunk-name")) {
            return curWord + "\t" + "prePos=" + prePos + "\t" +  "preNameTag=" +
                    preNameTag + "\t" + "curPos=" + curPos + "\t" + "nextPos=" +
                    nextPos + "\t" + "firstCapital=" + curCapital + "\t" + "nextWord" + nextWord +"\t" + curNameTag ;
        }

        if (extension.equals("pos-chunk")) {
            return curWord + "\t" + "prePos=" + prePos + "\t" + "preNameTag=" +
                    "@@" + "\t" + "curPos=" + curPos + "\t" + "nextPos=" + nextPos +
                    "\t" + "firstCapital=" + curCapital + "\t" + "nextWord" + nextWord ;
        }
        return  "";
    }

    static void setParameter(String extension) {
        prePos = curPos;
        preChunk = curChunk;
        curWord = nextWord;
        curPos = nextPos;
        curChunk = nextChunk;
        curCapital = nextCapital;
        if (extension.equals("pos-chunk-name")) {
            preNameTag = curNameTag;
            curNameTag = nextNameTag;
        }
    }

    static void checknextCapital(String word) {
        if (Character.isUpperCase(word.charAt(0))) {
            nextCapital = "Y";
        } else {
            nextCapital = "N";
        }
    }

    static void checkcurCapital(String word) {
        if (Character.isUpperCase(word.charAt(0))) {
            curCapital = "Y";
        } else {
            curCapital = "N";
        }
    }

    static void parseDiffCorpus(BufferedWriter bw, BufferedReader br, String extension) throws IOException{
        String line;
        while ((line = br.readLine()) != null) {
            if(line.equals("-DOCSTART-\t-X-\tO\tO")) {
                bw.write(line);
                bw.newLine();
            } else if (line.length() == 0 && prePos =="" && preChunk == "") {
                bw.write(line);
                bw.newLine();
                initalize(extension);
            } else if (line.length() == 0) {
                nextPos = "";
                nextChunk = "";
                if (extension.equals("pos-chunk-name")) {
                    nextNameTag = "";
                }
                bw.write(outPutLine(extension));
                bw.newLine();
                bw.write(line);
                bw.newLine();
                initalize(extension);
            } else {
                if(prePos == "" && preChunk == "") {
//                    prePos = "Start";
                    String[] curLineSplit = line.split("\t");
                    curWord = curLineSplit[0];
                    checkcurCapital(curWord);
                    curPos = curLineSplit[1];
                    curChunk = curLineSplit[2];
                    if (extension.equals("pos-chunk-name")) {
                        curNameTag = curLineSplit[3];
                    }
                    line = br.readLine();
                    if (line != null) {
                        if (line.length() != 0) {
                            String[] nextLineSpilt = line.split("\t");
                            nextWord = nextLineSpilt[0];
                            checknextCapital(nextWord);
                            nextPos = nextLineSpilt[1];
                            nextChunk = nextLineSpilt[2];
                            if (extension.equals("pos-chunk-name")) {
                                nextNameTag = nextLineSpilt[3];
                            }
                        }
                        String outPutString = outPutLine(extension);
                        bw.write(outPutString);
                        bw.newLine();
                        if (line.length() == 0) {
                            bw.write(line);
                            bw.newLine();
                            initalize(extension);
//                            nextPos = "End";
                        } else {
                            setParameter(extension);
                        }
                    } else {
                        break;
                    }
                } else {
                    String[] nextLineSplit = line.split("\t");
                    nextWord = nextLineSplit[0];
                    checknextCapital(nextWord);
                    nextPos = nextLineSplit[1];
                    nextChunk = nextLineSplit[2];
                    if (extension.equals("pos-chunk-name")) {
                        nextNameTag = nextLineSplit[3];
                    }
                    String outPutString = outPutLine(extension);
                    bw.write(outPutString);
                    bw.newLine();
                    setParameter(extension);
                }
            }
        }
    }

    static void parseCorpus(String fileName, BufferedWriter bw) throws IOException {
        String fileExtension = getFileExtension(fileName);
        initalize(fileExtension);
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        if (fileExtension.equals("pos-chunk-name")) {
           parseDiffCorpus(bw, br, "pos-chunk-name");
        }
        if (fileExtension.equals("pos-chunk")) {
            parseDiffCorpus(bw, br, "pos-chunk");
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
