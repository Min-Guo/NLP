import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Created by Min on 4/26/16.
 */
public class CTBParseHtml {

  public static void parseFiles(String dataPath, String resultPath) {
    File directory = new File(dataPath);
    File[] fileList = directory.listFiles();
    String desPath = resultPath + File.separator ;
    String firstLine;

    try {
      for (File file: fileList) {
        BufferedReader br = new BufferedReader(new FileReader(file));
        firstLine = br.readLine();
        if (firstLine.charAt(0) == '<') {
          Writer writer = new BufferedWriter((new OutputStreamWriter((
                  new FileOutputStream((resultPath + File.separator + file.getName()))
          ))));
          Document doc = Jsoup.parse(file, null);
          Elements sentences = doc.select("s");
          for (Element sentence : sentences) {
            if (sentence.text().length() != 0) {
              writer.write(sentence.text() + "\n");
            }
          }
          writer.close();
        } else {
          Files.copy(file.toPath(), (new File(desPath + file.getName())).toPath(),
                  StandardCopyOption.REPLACE_EXISTING);
        }
      }

    } catch (Exception e) {
      //
    }
  }

  public static void main (String[] args) {
    parseFiles(args[0], args[1]);
  }
}
