package com.example.familymapclient.serverProxy;

import static com.example.familymapclient.serverProxy.ServerReadWrite.readString;

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

import Model.Event;
import Request.EventRequest;
import Result.EventResult;

public class ServerEvent implements Runnable{

    EventRequest theRequest;
    String serverHost;
    String serverPort;
    Handler theHandler;
    EventResult eventResult;
    String authToken;

    public ServerEvent(EventRequest theRequest, String serverHost, String serverPort, Handler theHandler, String authtoken) {
        this.theRequest = theRequest;
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.theHandler = theHandler;
        this.authToken = authtoken;
    }
    public ServerEvent(EventRequest theRequest, String serverHost, String serverPort, String authtoken) {
        this.theRequest = theRequest;
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.authToken = authtoken;
    }

    @Override
    public void run() {
        try {
            // Create a URL indicating where the server is running, and which
            // web API operation we want to call
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/event");
            HttpURLConnection http = (HttpURLConnection) url.openConnection(); // Start constructing our HTTP request
            http.setRequestMethod("GET"); // Specify that we are sending an HTTP GET request
            http.setDoOutput(false);// Http will NOT have a request body as it is just grabbing events
            // Add an auth token to the request in the HTTP "Authorization" header
            http.addRequestProperty("Authorization", authToken);
            // Specify that we would like to receive the server's response in JSON.
            http.addRequestProperty("Accept", "application/json");

            // Connect to the server and send the HTTP request, all the magic happens here
            http.connect();

            // This is the JSON string we will send in the HTTP request body
            Gson gson = new Gson();
            String reqData = gson.toJson(theRequest, theRequest.getClass());

            // Make sure we succeeded
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // Get the input stream containing the HTTP response body
                InputStream respBody = http.getInputStream();

                // Extract JSON data from the HTTP response body
                String respData = readString(respBody);

                //Make the result
                eventResult = gson.fromJson(respData, EventResult.class);

                // Display the JSON data returned from the server
                System.out.println(respData);

                DataCache.getInstance().events = eventResult.getEventList(); //Everyone associated with the user is here now
                DataCache.getInstance().fillEvents(eventResult.getEventList()); //This one with the maps
                System.out.println("Data Cached for Event");

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

        if(theHandler!=null){
            Bundle myBundle = new Bundle();
            myBundle.putBoolean("SuccessMessage", true);
            Message message = Message.obtain();
            message.setData(myBundle);

            theHandler.sendMessage(message);
        }
    }
}
