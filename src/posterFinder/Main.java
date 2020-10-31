package posterFinder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Scanner;
import org.apache.commons.lang3.StringUtils;
public class Main {
    public static void saveImage(String imageUrl, String imageName) throws IOException {
        URL url = new URL(imageUrl);
        String fileName = url.getFile();
        String destName = "./" + imageName + ".jpg";

        InputStream is = url.openStream();
        OutputStream os = new FileOutputStream(destName);

        byte[] b = new byte[2048];
        int length;

        while ((length = is.read(b)) != -1) {
            os.write(b, 0, length);
        }

        is.close();
        os.close();
    }
    public static void main(String[] args) {
        boolean done = false;
        while (!done) {
            Scanner scan = new Scanner(System.in);
            System.out.print("Search for a title: ");
            String title = scan.nextLine();
            String url = "https://www.imdb.com/find?s=tt&q=" + title;
            try {
                final Document document = Jsoup.connect(url).get();
                boolean results = !document.select("h1.findHeader").text().contains("No");
                if (!results) {
                    System.out.println("No titles found for " + title);
                    continue;
                }
                String links[] = new String[10];
                int i = 0;
                for (Element row : document.select(
                        "table.findList tr")) {
                    if (i > 9) {
                        break;
                    }
                    System.out.println(Integer.toString(i+1) + ". " + row.text());
                    links[i] = row.select("td.result_text > a").attr("href");
                    i++;
                }
                System.out.print("Select a title (use its number): ");
                String selection = scan.nextLine();
                while (!(StringUtils.isNumeric(selection) && Integer.parseInt(selection) > 0
                        && Integer.parseInt(selection) < i + 1)) {
                    System.out.print("Made your selection with a number between 1 to " + i + ": ");
                    selection = scan.nextLine();
                }
                final Document titlePage = Jsoup.connect("https://www.imdb.com" +
                        links[Integer.parseInt(selection) - 1]).get();
                final Document imgPage = Jsoup.connect("https://imdb.com" +
                        titlePage.select("div.poster > a").attr("href")).get();
                String imgLink = imgPage.select("div.MediaViewerImagestyles__PortraitContainer-sc-1qk433p-2.gIroZm > img").attr("src");
                if (imgLink.length() == 0) {
                    System.out.println("No poster available.");
                    break;
                }
                saveImage(imgLink, imgPage.title());
                System.out.println("done");
                done = true;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
