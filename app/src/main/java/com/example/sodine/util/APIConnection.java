package com.example.sodine.util;

import android.util.Log;
import android.widget.EditText;

import com.example.sodine.MainActivity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class APIConnection {

    private static final String URL = "https://sodine.nl:5000/api/v1.0/";

    private static final String TWO_HYPHENS = "--";
    private static final String BOUNDARY = "*****" + System.currentTimeMillis() + "*****";
    private static final String LINE_END = "\r\n";

    private String targetUrl;
    private String method;
    private HashMap<String,String> params;

    private HttpURLConnection connection;
    private DataOutputStream outputStream;
    private InputStream inputStream;

    /**
     * Sends a GET request to the API and returns a HashMap containing the (JSON) response values.
     * @param resource defines the REST resource URI which will be accessed
     * @return a HashMap containing the requested data
     */
    public JSONObject GET(String resource) {
        String jwt = MainActivity.session.getJWT();
        this.targetUrl = URL + resource + "?jwt=" + jwt;
        this.method = "GET";

        JSONObject result = null;
        try {
            CompletableFuture<JSONObject> completableFuture = CompletableFuture.supplyAsync(() -> request(false, true));
            result = completableFuture.get();
        } catch (Exception e) {
            Log.d("Error", e.toString());
        }
        return result;
    }

    /**
     * Called instead of the base GET() method if query requires a user ID.
     * @param resource the REST resource uri to query
     * @param ID contains the user ID of the user whose details should be returned by the API
     * @return a HashMap built from the JSON returned by the API
     */
    public JSONObject GET(String resource, String ID) {
        return GET(resource + "/" + ID);
    }

    public JSONObject PUT(String resource, String jwt, String...args) {
        return null;
    }

    public void POST(String resource, String ID, HashMap<String,String> args) {
        String jwt = MainActivity.session.getJWT();
        this.targetUrl = URL + resource + "/" + ID + "?jwt=" + jwt;
        this.method = "POST";
        this.params = args;

        try {
            new Thread(() -> request(true, false)).start();
        } catch (Exception e) {
            Log.d("Error", e.toString());
        }
    }

    public void DELETE(String resource, String ID, HashMap<String,String> args) {
        String jwt = MainActivity.session.getJWT();
        this.targetUrl = URL + resource + "/" + ID + "?jwt=" + jwt;
        this.method = "DELETE";
        this.params = args;

        try {
            new Thread(() -> request(true, false)).start();
        } catch (Exception e) {
            Log.d("Error", e.toString());
        }
    }

    public JSONObject validate(String email, String password) {
        Log.d("Email entered", email);
        Log.d("Password entered", password);
        this.targetUrl = "https://sodine.nl:5000/api/v1.0/user/login";
        this.method = "POST";
        this.params = new HashMap<>(2);
        this.params.put("email", email);
        this.params.put("password", password);

        JSONObject result = null;
        try {
            result = request(true, true);
        } catch (Exception e) {
            Log.d("Error", e.toString());
        }

        return result;
    }

    private JSONObject request(boolean includeOutput, boolean getInput) {
        JSONObject result = null;
        try {
            URL url = new URL(this.targetUrl);
            connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(includeOutput);
            connection.setUseCaches(false);

            connection.setRequestMethod(this.method);
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");

            if (includeOutput) {
                outputFormData();
                System.out.println("hey dude you sent some stuff, gj");
            }

            if (getInput) {
                inputStream = connection.getInputStream();
                result = streamToJSON(inputStream);
            }

            if (connection.getResponseCode() >= 400) {
                new IOException(this.method + " failed on " + this.targetUrl + ": " + connection.getResponseCode() + " " + connection.getResponseMessage() + " - " + result).printStackTrace();
            }

            if (includeOutput) {
                outputStream.flush();
                outputStream.close();
            }
            if (getInput) {
                inputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (getInput) return result;
        else return null;
    }

    private void outputFormData() throws IOException {
        try {
            this.connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
            this.outputStream = new DataOutputStream(connection.getOutputStream());

            Iterator<String> keys = this.params.keySet().iterator();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = this.params.get(key);

                outputStream.writeBytes(TWO_HYPHENS + BOUNDARY + LINE_END);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + LINE_END);
                outputStream.writeBytes("Content-Type: text/plain" + LINE_END);
                outputStream.writeBytes(LINE_END);
                outputStream.writeBytes(value);
                outputStream.writeBytes(LINE_END);
            }
            outputStream.writeBytes(TWO_HYPHENS + BOUNDARY + TWO_HYPHENS + LINE_END);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    private JSONObject streamToJSON(InputStream is) throws JSONException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String resultString = sb.toString();
        JSONObject result;


        if (!resultString.contains("{")) {
            JSONArray array = new JSONArray(resultString);
            result = new JSONObject();
            try {
                result.put("key", array);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        } else {
            result = new JSONObject(sb.toString());
        }

        return result;
    }
}
