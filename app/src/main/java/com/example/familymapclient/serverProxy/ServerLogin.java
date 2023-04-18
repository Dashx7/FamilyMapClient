package com.example.familymapclient.serverProxy;

import static com.example.familymapclient.serverProxy.ServerReadWrite.readString;

import android.app.ActivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.example.familymapclient.cache.DataCache;
import com.example.familymapclient.ui.login.LoginFragment;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import Request.LoginRequest;
import Result.LoginResult;

public class ServerLogin implements Runnable {

    LoginRequest theRequest;
    String serverHost;
    String serverPort;
    Handler theHandler;

    public ServerLogin(LoginRequest theRequest, String serverHost, String serverPort, Handler theHandler) {
        this.theRequest = theRequest;
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.theHandler = theHandler;
    }
    public ServerLogin(LoginRequest theRequest, String serverHost, String serverPort) {
        this.theRequest = theRequest;
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    @Override
    public void run() {
        LoginResult loginResult = new LoginResult();

        // Login is a post request
        try {
            // Create a URL indicating where the server is running, and which
            // web API operation we want to call
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/user/login");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();// Start constructing our HTTP request
            http.setRequestMethod("POST"); // Specify that we are sending an HTTP POST request
            http.setDoOutput(true);// Http will have a request body

            // Add an auth token to the request in the HTTP "Authorization" header
            //http.addRequestProperty("Authorization", "afj232hj2332");
            // Specify that we would like to receive the server's response in JSON.
            http.addRequestProperty("Accept", "application/json");

            // Connect to the server and send the HTTP request, all the magic happens here
            http.connect();

            // This is the JSON string we will send in the HTTP request body
            Gson gson = new Gson();
            String reqData = gson.toJson(theRequest, LoginRequest.class);

            // Get the output stream containing the HTTP request body
            OutputStream reqBody = http.getOutputStream();

            // Write the JSON data to the request body
            ServerReadWrite.writeString(reqData, reqBody);

            // Close the request body output stream, indicating that the
            // request is complete
            reqBody.close();

            // Make sure we succeeded
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // Get the input stream containing the HTTP response body
                InputStream respBody = http.getInputStream();

                // Extract JSON data from the HTTP response body
                String respData = readString(respBody);

                //Make the result
                loginResult = gson.fromJson(respData, LoginResult.class);

                // Display the JSON data returned from the server
                System.out.println(respData);

                //Cache the data
                DataCache.getInstance().loginResult = loginResult;
                DataCache.getInstance().authToken = loginResult.getAuthtoken();
                System.out.println("Data Cached for login");

                if(theHandler!=null){
                    Bundle myBundle = new Bundle();
                    myBundle.putBoolean("SuccessMessage", true);
                    Message message = Message.obtain();
                    message.setData(myBundle);

                    theHandler.sendMessage(message);
                }


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

                if(theHandler!=null) {
                    Bundle myBundle = new Bundle();
                    myBundle.putBoolean("SuccessMessage", false);
                    Message message = Message.obtain();
                    message.setData(myBundle);

                    theHandler.sendMessage(message);
                }
            }
        } catch (IOException e) {
            // An exception was thrown, so display the exception's stack trace
            e.printStackTrace();
        }


    }
}

