package com.example.familymapclient.serverProxy;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.example.familymapclient.cache.DataCache;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import Request.RegisterRequest;
import Result.RegisterResult;

public class ServerRegister implements Runnable {

    RegisterRequest theRequest;
    String serverHost;
    String serverPort;
    Handler theHandler;

    public ServerRegister(RegisterRequest theRequest, String serverHost, String serverPort, Handler theHandler) {
        this.theRequest = theRequest;
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.theHandler = theHandler;
    }

    @Override
    public void run() {
        // Login is a post request
        RegisterResult registerResult = new RegisterResult();
        try {
            // Create a URL indicating where the server is running, and which
            // web API operation we want to call
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/user/register");
            // Start constructing our HTTP request
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("POST");      // Specify that we are sending an HTTP POST request
            http.setDoOutput(true);     // Http will have a request body
            http.addRequestProperty("Accept", "application/json");      // Specify that we would like to receive the server's response in JSON.

            // Connect to the server and send the HTTP request, all the magic happens here
            http.connect();

            // This is the JSON string we will send in the HTTP request body
            Gson gson = new Gson();
            String reqData = gson.toJson(theRequest, theRequest.getClass());

            // Get the output stream containing the HTTP request body
            OutputStream reqBody = http.getOutputStream();

            // Write the JSON data to the request body
            ServerReadWrite.writeString(reqData, reqBody);

            // Close the request body output stream, indicating that the
            // request is complete
            reqBody.close();


            // By the time we get here, the HTTP response has been received from the server.
            // Check to make sure that the HTTP response from the server contains a 200
            // status code, which means "success".  Treat anything else as a failure.
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // Get the input stream containing the HTTP response body
                InputStream respBody = http.getInputStream();

                // Extract JSON data from the HTTP response body
                String respData = ServerReadWrite.readString(respBody);

                //Makes the register result from the response
                registerResult = gson.fromJson(respData, registerResult.getClass());
                // Display the JSON data returned from the server
                System.out.println(respData);

                //TODO Cache the answers
                DataCache.getInstance().registerResult = registerResult;
                DataCache.getInstance().authToken = registerResult.getAuthtoken();
                System.out.println("Data Cached for register");

                Bundle myBundle = new Bundle();
                myBundle.putBoolean("SuccessMessage", true);
                Message message = Message.obtain();
                message.setData(myBundle);

                theHandler.sendMessage(message);


            } else {
                // The HTTP response status code indicates an error
                // occurred, so print out the message from the HTTP response
                System.out.println("ERROR: " + http.getResponseMessage());

                // Get the error stream containing the HTTP response body (if any)
                InputStream respBody = http.getErrorStream();

                // Extract data from the HTTP response body
                String respData = ServerReadWrite.readString(respBody);

                // Display the data returned from the server, we failed tho
                System.out.println(respData);

                Bundle myBundle = new Bundle();
                myBundle.putBoolean("SuccessMessage", false);
                Message message = Message.obtain();
                message.setData(myBundle);

                theHandler.sendMessage(message);
            }
        } catch (IOException e) {
            // An exception was thrown, so display the exception's stack trace
            e.printStackTrace();
        }


    }
}





