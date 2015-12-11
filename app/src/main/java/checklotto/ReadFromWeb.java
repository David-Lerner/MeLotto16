package checklotto;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.net.URLConnection;


/**
 *
 * Class that can open a URL and return it as a string.
 *
 * @author David Lerner
 */
public class ReadFromWeb {

    /**
     * Requests the file from a web site given by URLname and saves it to a string
     *
     * @param URLname the URL of the online text file to read
     * @return the text file as a string
     */
    public static String read(String URLname) throws MalformedURLException, IOException, UnknownHostException
    {
        URL url = new URL(URLname);
        //BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
        URLConnection connection = url.openConnection();
        InputStream stream = connection.getInputStream();
        BufferedReader in = new BufferedReader(new InputStreamReader(stream));
        StringBuilder sb = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null)
        {
            sb.append(inputLine);
            sb.append(System.getProperty("line.separator"));
        }
        String data = sb.toString();
        in.close();

        return data;
    }

    /**
     * Requests the file from a web site given by URLname with a "Referer" header and saves it to a string
     *
     * @param URLname the URL of the online text file to read
     * @param URLreferer the spoofed URL of the referring site 
     * @return the text file as a string
     */
    public static String readWithReferer(String URLname, String URLreferer) throws MalformedURLException, IOException, UnknownHostException
    {
        URL url = new URL(URLname);
        URLConnection connection = url.openConnection();
        connection.setRequestProperty("Referer", URLreferer);
        //connection.setRequestProperty("Referer", "http://www.calottery.com/play/draw-games/powerball/winning-numbers");
        //connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36");
        //connection.setRequestProperty("Upgrade-Insecure-Requests", "1");
        //connection.setRequestProperty("DNT", "1");
        InputStream stream = connection.getInputStream();
        BufferedReader in = new BufferedReader(new InputStreamReader(stream));
        StringBuilder sb = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null)
        {
            sb.append(inputLine);
            sb.append(System.getProperty("line.separator"));
        }
        String data = sb.toString();
        in.close();

        return data;
    }

}
