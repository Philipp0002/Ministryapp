package tk.phili.dienst.dienst;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;


public class HttpUtils {

    public static String getUrlAsString(String url)
    {
        try
        {
            URL urlObj = new URL(url);
            URLConnection con = urlObj.openConnection();

            con.setDoOutput(true); // we want the response
            con.connect();

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "ISO-8859-1"));

            StringBuilder response = new StringBuilder();
            String inputLine;

            String newLine = System.getProperty("line.separator");
            while ((inputLine = in.readLine()) != null)
            {
                response.append(inputLine + newLine);
            }

            in.close();

            return response.toString();
        }
        catch (Exception e)
        {
            //throw new RuntimeException(e);
            return "ERROR";
        }
    }

    public static String getContents(String url) {
        String contents ="";

        try {
            URLConnection conn = new URL(url).openConnection();

            InputStream in = conn.getInputStream();
            contents = convertStreamToString(in);
        } catch (MalformedURLException e) {
            Log.v("ERROR","MALFORMED URL EXCEPTION");
        } catch (IOException e) {
            //e.printStackTrace();
        }

        return contents;
    }

    private static String convertStreamToString(InputStream is) throws UnsupportedEncodingException {

        BufferedReader reader = new BufferedReader(new
                InputStreamReader(is, "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "n");
            }
        } catch (IOException e) {
            //e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                //e.printStackTrace();
            }
        }
        return sb.toString();
    }

}
