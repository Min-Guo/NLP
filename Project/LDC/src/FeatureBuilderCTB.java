/**
 * Created by Min on 4/27/16.
 */
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Filter;

/**
 * Created by ChenChen on 4/19/16.
 */
public class FeatureBuilderCTB {
  public enum FileType {
    TRAINING, DEVELOP, TESTING;
  }

  private String              dataPath;
  private String              resultPath;
  private FileType            fileType;
  private String              dicPath;
  private int                 statusType;
//  private DictionaryFeature   dict;

  private static final List<Character> chineseNumber = Arrays.asList('零', '一', '二', '三', '四', '五',
          '六', '七', '八', '九', '十');
  private static final List<Character> chineseDates = Arrays.asList('年', '月', '日');

  FeatureBuilderCTB(String dp, String rp, FileType ft, String dicp, int st) {
    dataPath    = dp;
    resultPath  = rp;
    fileType    = ft;
    dicPath     = dicp;
    statusType  = st;

    if (fileType == FileType.TRAINING) {
      resultPath += File.separator + "trainingFeatures";
    } else if (fileType == FileType.DEVELOP){
      resultPath += File.separator + "developFeatures";
    }

//    dict = DictionaryFeature.getInstance();
//    dict.readDict(dicPath);
  }


  private void Process() {
    int sentence = 0;
    int lineCount = 0;
    String curline = "";
    List<String> words = new ArrayList<String>();
    List<List<String>> states = new ArrayList<>();

    try {
      InputStreamReader read = new InputStreamReader(new FileInputStream(new File(dataPath)),"UTF-8");
      BufferedReader bufferedReader = new BufferedReader(read);

      File featurefile = new File(resultPath);
      if (!featurefile.exists()) {
        featurefile.createNewFile();
      }
      FileOutputStream fos = new FileOutputStream(featurefile);
      OutputStreamWriter write = new OutputStreamWriter(fos, "UTF-8");
      BufferedWriter bufferedWriter = new BufferedWriter(write);

      while((curline = bufferedReader.readLine()) != null){
        if (curline.equals("")) {
          List<List<String>> features = GenerateFeature(words, states);
          if (!words.isEmpty()) {
            sentence++;
          }
          StoreFeaturesFile(features, bufferedWriter);
          ClearList(words, states);
//                    if (words.isEmpty()) {
//                        System.out.println("Sentence: " + sentence);
//                        System.out.println("Line: " + (lineCount+1) + "\n");
//                    }
        } else {
          String[] pair = curline.split("\\s+");
          words.add(pair[0]);
          if (fileType == FileType.TRAINING || fileType == FileType.DEVELOP) {
            List<String> stateList = new ArrayList<>();
            stateList.add(pair[1]);
            stateList.add(pair[2]);
            states.add(stateList);
          }
        }
        lineCount++;
      }

      bufferedWriter.close();
      write.close();
      fos.close();
      bufferedReader.close();
      read.close();

    } catch (FileNotFoundException e) {
      System.out.println("Data file doesn't exit");
      System.exit(1);
    } catch (UnsupportedEncodingException e) {
      System.out.println("Doesn't exit the encoding way");
      System.exit(1);
    } catch (IOException e) {
      System.out.println("Cannot create result file");
      System.exit(1);
    }
  }

  /*
   * key method
   */
  private List<List<String>> GenerateFeature(List<String> words, List<List<String>> states) {
    List<List<String>> features = new ArrayList<List<String>>();

//    String[] predict = dict.analysis(words, statusType);     // feature -- predict status
//    if (predict.length == 0) {
//      return features;
//    }
    for (int i = 0; i < words.size(); i++) {
      List<String> wordFeature = new ArrayList<String>();
      //current word -- 1
      String curWord = words.get(i);
      wordFeature.add(curWord);

      String preWord = "NULL";
      String nextWord = "NULL";
      String prepreWord = "NULL2";
      String nextnextWord = "NULL2";

      //first previous word
      if (i > 0) {
        preWord = words.get(i - 1);
      }

      if (i < words.size() - 1) {
        nextWord = words.get(i + 1);
      }

      if (i > 1) {
        prepreWord = words.get(i - 2);
      }

      // second next word
      if (i < words.size() - 2) {
        nextnextWord = words.get(i + 2);
      }

      //current word is identical to first previous word  -- 2
      if (curWord.equals(preWord)) {
        wordFeature.add("AA");
      } else {
        wordFeature.add("AB");
      }

      // 5 chars window format    -- 3
      List<String> window = new ArrayList<String>();
      window.add(prepreWord);
      window.add(preWord);
      window.add(curWord);
      window.add(nextWord);
      window.add(nextnextWord);

      // predict status   -- 4
//      wordFeature.add(predict[i]);


      //status for current word   -- 5
      if (fileType == FileType.TRAINING || fileType == FileType.DEVELOP) {
        wordFeature.add(states.get(i).get(0));
        wordFeature.add(states.get(i).get(1));
      }

      features.add(wordFeature);
    }
    return features;
  }

  /*
   * store features based on CRF++
   */
  private void StoreFeaturesFile(List<List<String>> features, BufferedWriter bufferedWriter) {
    int size = features.size();
    try {
      for (int i = 0; i < size; i++) {
        String templine = "";
        int features_size = features.get(i).size();
        for (int j = 0; j < features_size; j++) {
          if (j == features_size-1) {
            templine += features.get(i).get(j);
          } else {
            templine += features.get(i).get(j) + "\t";
          }
        }
        templine += "\n";
        bufferedWriter.write(templine);
      }
      bufferedWriter.write("\n");
    } catch (IOException e) {
      System.out.println("Cannot write features into file!");
      System.out.println(e);
    }
  }

  private void ClearList(List<String> words, List<List<String>> states) {
    words.clear();
    if (fileType == FileType.TRAINING || fileType == FileType.DEVELOP) {
      states.clear();
    }
  }

  public static void main(String[] args) {
    String dataPath     = "";
    String resultPath   = "";
    String dicPath      = "";
    FileType fileType   = null;
    int statusType      = 2;

    // command line parameters analysis
    for (int i = 0; i < args.length; i++) {
      if ("-d".equals(args[i])) {
        dataPath = args[i + 1];
        i++;
      } else if ("-r".equals(args[i])) {
        resultPath = args[i + 1];
        i++;
      } else if ("-t".equals(args[i])) {
        if ("train".equals(args[i + 1])) {
          fileType = FileType.TRAINING;
        } else if ("develop".equals(args[i+1])) {
          fileType = FileType.DEVELOP;
        } else {
          System.out.println("Type can only be: train, develop or test");
          System.exit(1);
        }
        i++;
      } else if ("-dic".equals(args[i])) {
        dicPath = args[i+1];
        i++;
      } else if ("-st".equals(args[i])) {
        statusType = Integer.parseInt(args[i+1]);
        i++;
      }
    }

    FeatureBuilderCTB fb = new FeatureBuilderCTB(dataPath, resultPath, fileType, dicPath, statusType);
    fb.Process();
  }

}