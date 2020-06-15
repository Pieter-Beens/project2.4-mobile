package com.example.sodine.util;

import android.util.Log;
import android.widget.EditText;

import com.google.gson.JsonParser;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class API {

    public static String GET(String subdomain, String...args) {
        String urlString = "http://sodine.nl:5000/api/v1.0/" + subdomain;

        String response = "";

        return response;
    }
}
