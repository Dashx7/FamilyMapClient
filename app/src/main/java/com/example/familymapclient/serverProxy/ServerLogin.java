package com.example.familymapclient.serverProxy;

import static com.example.familymapclient.serverProxy.ServerReadWrite.readString;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import Request.LoginRequest;
import Result.LoginResult;

public class ServerLogin {

    public LoginResult login(LoginRequest theRequest, String serverHost, String serverPort) {
        LoginResult loginResult = new LoginResult();

        // Login is a post request
        try {
            // Create a URL indicating where the server is running, and which
            // web API operation we want to call
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/user/login");

            // Start constructing our HTTP request
            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            // Specify that we are sending an HTTP POST request
            http.setRequestMethod("POST");

            // Http will have a request body
            http.setDoOutput(true);
            // Add an auth token to the request in the HTTP "Authorization" header
            //http.addRequestProperty("Authorization", "afj232hj2332");
            // Specify that we would like to receive the server's response in JSON.
            http.addRequestProperty("Accept", "application/json");


            // Connect to the server and send the HTTP request, all the magic happens here
            http.connect();

            // Make sure we succeeded
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // Get the input stream containing the HTTP response body
                InputStream respBody = http.getInputStream();

                // Extract JSON data from the HTTP response body
                String respData = readString(respBody);

                //Make the result
                Gson gson = new Gson();
                loginResult = gson.fromJson(respData,LoginResult.class);

                // Display the JSON data returned from the server
                System.out.println(respData);


            } else {
                // The HTTP response status code indicates an error
                // occurred, so print out the message from the HTTP response
                System.out.println("ERROR: " + http.getResponseMessage());

                // Get the error stream containing the HTTP response body (if any)
                InputStream respBody = http.getErrorStream();

                // Extract data from the HTTP response body
                String respData = readString(respBody);

                // Display the data returned from the server
                System.out.println(respData);
            }
        } catch (IOException e) {
            // An exception was thrown, so display the exception's stack trace
            e.printStackTrace();
        }
        return loginResult;
    }
}
