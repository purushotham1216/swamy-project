package geotaglabour.nic.com.geotaglabour;


import org.json.JSONObject;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;

public class WsUtility {
    static String targetURL_Global = "";

    public static String executePostHttps(String targetURL, String data, String methodStr) throws IOException, SQLException {
        URL url;
        String jsonData = data;
        targetURL_Global = targetURL;
        System.out.println("targetURL_str: "+targetURL_Global);
        String encode_URL=java.net.URLEncoder.encode(targetURL,"UTF-8");
        System.out.println("encode_URL: "+encode_URL);
        HttpURLConnection connection = null;
        try {
            url = new URL(targetURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(methodStr);
            if(methodStr.equalsIgnoreCase("post")||methodStr.equalsIgnoreCase("put"))
            {
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                connection.setRequestProperty("Content-Length", "" + Integer.toString(data.getBytes("UTF-8").length));
                connection.setRequestProperty("Content-Language", "en-US");
                connection.setUseCaches(false);
                connection.setDoInput(true);
                connection.setDoOutput(true);
                OutputStream os = connection.getOutputStream();
                os.write(data.getBytes("UTF-8"));
                os.flush();
                os.close();

                Log.i("data.getBytes",""+data.getBytes("UTF-8"));
            }
            else if(methodStr.equalsIgnoreCase("get"))
            {
                connection.setRequestProperty("Content-Type", "application/json");
            }


            int responseCode = connection.getResponseCode();

            InputStream is = null;

            if (responseCode >= 200 && responseCode < 400) {
                // Create an InputStream in order to extract the response object
                is = connection.getInputStream();
            }
            else {
                is = connection.getErrorStream();
            }
            System.out.println("responseCode "+responseCode);
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while ((line = rd.readLine()) != null) {
                response.append(line);
            }

            String[] bits = targetURL.split("/");
            String targetURL_str = bits[bits.length-1];

            rd.close();

            //return String.valueOf(responseCode).toString();

            return response.toString();
        } catch (Exception e) {

            System.out.println(e);
            return e.toString();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}

