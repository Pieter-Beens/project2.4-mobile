package com.example.sodine.util;

import android.util.Log;
import android.widget.EditText;

import com.example.sodine.MainActivity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonParser;

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
    public HashMap<String, String> GET(String resource) {
        String jwt = MainActivity.session.getJWT();
        this.targetUrl = URL + resource + "?jwt=" + jwt;
        this.method = "GET";

        HashMap<String, String> result = new HashMap<>();

        try {
            CompletableFuture<HashMap> completableFuture = CompletableFuture.supplyAsync(this::request);
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
    public HashMap<String, String> GET(String resource, String ID) {
        return GET(resource + "/" + ID);
    }

    public String PUT(String resource, String jwt, String...args) {
        String target = URL + resource + "?jwt=" + jwt;

        String response = "";

        return response;
    }

    public String POST(String resource, String jwt, String...args) {
        String target = URL + resource + "?jwt=" + jwt;

        String response = "";

        return response;
    }

    public String DELETE(String resource, String jwt) {
        String target = URL + resource + "?jwt=" + jwt;

        String response = "";

        return response;
    }

    public String validate(String email, String password) {
        Log.d("Email entered", email);
        Log.d("Password entered", password);
        this.targetUrl = "https://sodine.nl:5000/api/v1.0/user/login";
        this.method = "POST";
        this.params = new HashMap<>(2);
        this.params.put("email", email);
        this.params.put("password", password);

        String result = "";
        try {
            System.out.println(multipartRequest().toString()); //TODO: copy multipartrequest code here and don't use convertStream()
        } catch (Exception e) {
            Log.d("Error", e.toString());
        }

        return result;
    }

    private HashMap<String,String> request() {
        HashMap<String,String> result = new HashMap<>();
        try {
            URL url = new URL(this.targetUrl);
            connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(false);
            connection.setUseCaches(false);

            connection.setRequestMethod(this.method);
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");

            if (connection.getResponseCode() != 200 && connection.getResponseCode() != 201 && connection.getResponseCode() != 202) {
                new IOException(this.method + " failed on " + this.targetUrl + ": " + connection.getResponseCode() + " " + connection.getResponseMessage()).printStackTrace();
            }

            inputStream = connection.getInputStream();
            result = convertStreamToHashMap(inputStream);
            inputStream.close();
        } catch (Exception e) {
            new IOException(e).printStackTrace();
        }
        return result;
    }

    private HashMap<String, String> multipartRequest() {
        HashMap<String,String> result = new HashMap<>();
        try {
            URL url = new URL(this.targetUrl);
            connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            connection.setRequestMethod(this.method);
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");
            connection.setRequestProperty("Content-Type", "multipart/form-data; BOUNDARY=" + BOUNDARY);


            if (connection.getResponseCode() != 200 && connection.getResponseCode() != 201 && connection.getResponseCode() != 202) {
                new IOException(this.method + " failed on " + this.targetUrl + ": " + connection.getResponseCode() + " " + connection.getResponseMessage()).printStackTrace();
            }

            inputStream = connection.getInputStream();
            result = convertStreamToHashMap(inputStream);
            inputStream.close();

            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            new IOException(e).printStackTrace();
        }
        return result;
    }

    private HashMap<String,String> convertStreamToHashMap(InputStream is) {
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

        HashMap<String, String> map = new HashMap<>();
        try { //mapmaker
            map = new ObjectMapper().readValue(sb.toString(), HashMap.class);
        } catch (Exception e) {
            Log.d("JSONError", e.toString());
        }

        return map;
    }
}
