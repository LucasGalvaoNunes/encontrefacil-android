package opetbrothers.com.encontrefacil.Util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Created by Lucas Galvao Nunes on 24/04/2017.
 */

public class HttpMetods {

    public final static String URL = "http://35.184.152.136/EncontreFacilWs/rest/";
    public static int serverResponseCode;
    public static String serverResponseMessage;


    public static String GET(String pUrl)
    {
        String contentAsString = null;
        HttpURLConnection urlConnection = null;
        InputStream is = null;
        int length = 500;

        try {
            java.net.URL url = new URL(URL + pUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            urlConnection.connect();

            int response = urlConnection.getResponseCode();
            is = urlConnection.getInputStream();

            // Convert the InputStream into a string
            contentAsString = Util.webToString(is);

            return contentAsString;

        } catch (Exception e) {
            Log.e("Error", "Error ", e);
            return null;
        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }
    
    public static String POST(String pUrl, String pJson)
    {
        HttpURLConnection urlConnection = null;
        try {
            java.net.URL url = new URL(URL + pUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json; charset=utf8");
            DataOutputStream outputStream = new DataOutputStream(urlConnection.getOutputStream());

            outputStream.write(pJson.getBytes(Charset.forName("UTF-8")));

            serverResponseCode = urlConnection.getResponseCode();
            serverResponseMessage = urlConnection.getResponseMessage();

            String result = webToString(urlConnection.getInputStream());

            outputStream.flush();
            outputStream.close();

            return result;
        } catch (Exception e) {
            Log.e("Error", "Error ", e);
            return null;
        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    public static String PUT(String pUrl, String pJson)
    {

        HttpURLConnection urlConnection = null;
        try {
            java.net.URL url = new URL(URL + pUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("PUT");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json; charset=utf8");

            DataOutputStream outputStream = new DataOutputStream(urlConnection.getOutputStream());

            outputStream.write(pJson.getBytes(Charset.forName("UTF-8")));

            serverResponseCode = urlConnection.getResponseCode();
            serverResponseMessage = urlConnection.getResponseMessage();

            String result = webToString(urlConnection.getInputStream());

            outputStream.flush();
            outputStream.close();

            return result;
        } catch (Exception e) {
            Log.e("Error", "Error ", e);
            return null;
        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    public static String DELETE(String pUrl, String pJson)
    {

        HttpURLConnection urlConnection = null;
        try {
            java.net.URL url = new URL(URL + pUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("DELETE");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json; charset=utf8");

            DataOutputStream outputStream = new DataOutputStream(urlConnection.getOutputStream());

            outputStream.write(pJson.getBytes(Charset.forName("UTF-8")));

            serverResponseCode = urlConnection.getResponseCode();
            serverResponseMessage = urlConnection.getResponseMessage();

            String result = webToString(urlConnection.getInputStream());

            outputStream.flush();
            outputStream.close();

            return result;
        } catch (Exception e) {
            Log.e("Error", "Error ", e);
            return null;
        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }
    public static String webToString(InputStream inputStream) {
        InputStream localStream = inputStream;
        String localString = "";
        Writer writer = new StringWriter();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(localStream, "UTF-8"));
            String line = reader.readLine();
            while (line != null) {
                writer.write(line);
                line = reader.readLine();
            }
            localString = writer.toString();
            writer.close();
            reader.close();
            localStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return localString;
    }
}
