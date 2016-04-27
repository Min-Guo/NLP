import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.sql.SQLOutput;
import java.util.Arrays;

/**
 * Created by Min on 4/26/16.
 */
public class CTBParseTag {

  public static void segmentation(String sentence, Writer writer_2,
      Writer writer_4, Writer  writer_5) {
    try {
      String[] words = sentence.split("\\s+");
      for (int i = 0; i < words.length; i++) {
        String wordTag = words[i];
        String wordTagSplit[] = wordTag.split("_");
        String word = wordTagSplit[0];
        String tag = wordTagSplit[1];
        if (word.length() == 1) {
          writer_2.write(word + "\t" + "B" + "\t" + tag + "\n");
          writer_4.write(word + "\t" + "S" + "\t" + tag + "\n");
          writer_5.write(word + "\t" + "S" + "\t" + tag + "\n");
          continue;
        }

        for (int pos = 0; pos < word.length(); pos++) {
          char current = word.charAt(pos);
          if (pos == 0) {
            writer_2.write(current + "\t" + "B" + "\t" + tag + "\n");
            writer_4.write(current + "\t" + "B" + "\t" + tag + "\n");
            writer_5.write(current + "\t" + "B" + "\t" + tag + "\n");
          } else if (pos == 1 && pos != word.length() - 1) {
            writer_2.write(current + "\t" + "E" + "\t" + tag + "\n");
            writer_4.write(current + "\t" + "M" + "\t" + tag + "\n");
            writer_5.write(current + "\t" + "B2" + "\t" + tag + "\n");
          } else if (pos == word.length() - 1) {
            writer_2.write(current + "\t" + "E" + "\t" + tag + "\n");
            writer_4.write(current + "\t" + "E" + "\t" + tag + "\n");
            writer_5.write(current + "\t" + "E" + "\t" + tag + "\n");
          } else {
            writer_2.write(current + "\t" + "E" + "\t" + tag + "\n");
            writer_4.write(current + "\t" + "M" + "\t" + tag + "\n");
            writer_5.write(current + "\t" + "M" + "\t" + tag + "\n");
          }
        }
      }
      writer_2.write("\n");
      writer_4.write("\n");
      writer_5.write("\n");
    } catch (IOException e) {}
  }

  public static void parseFiles(String dataPath, String resultPath) {
    File directory = new File(dataPath);
    File[] fileList = directory.listFiles();
    File[] trainingList = Arrays.copyOfRange(fileList, 0, (int)Math.rint(fileList.length * 0.9));
    File[] developList = Arrays.copyOfRange(fileList, (int)Math.rint(fileList.length * 0.9), fileList.length);
    try {
      Writer writer_2 = new BufferedWriter((new OutputStreamWriter(
              new FileOutputStream(resultPath + File.separator + "training_2.txt"))));
      Writer writer_4 = new BufferedWriter((new OutputStreamWriter(
              new FileOutputStream(resultPath + File.separator + "training_4.txt"))));
      Writer writer_5 = new BufferedWriter((new OutputStreamWriter(
              new FileOutputStream(resultPath + File.separator + "training_5.txt"))));

      for (File file: trainingList) {
        if (file.getName().endsWith(".pos")) {
          String line;
          BufferedReader br = new BufferedReader(new FileReader(file));
          while ((line = br.readLine()) != null) {
            segmentation(line, writer_2, writer_4, writer_5);
          }
        }
      }
      writer_2.close();
      writer_4.close();
      writer_5.close();

      Writer writer_2D = new BufferedWriter((new OutputStreamWriter(
              new FileOutputStream(resultPath + File.separator + "develop_2.txt"))));
      Writer writer_4D = new BufferedWriter((new OutputStreamWriter(
              new FileOutputStream(resultPath + File.separator + "develop_4.txt"))));
      Writer writer_5D = new BufferedWriter((new OutputStreamWriter(
              new FileOutputStream(resultPath + File.separator + "develop_5.txt"))));
      for (File file: developList) {
        String line;
        BufferedReader br = new BufferedReader(new FileReader(file));
        while ((line = br.readLine()) != null) {
          segmentation(line, writer_2D, writer_4D, writer_5D);
        }
      }
      writer_2D.close();
      writer_4D.close();
      writer_5D.close();
    } catch (Exception e) {
      //
    }
  }

  public static void main(String[] args) {
    parseFiles(args[0], args[1]);
  }
}
