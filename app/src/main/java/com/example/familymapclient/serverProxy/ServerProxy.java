package com.example.familymapclient.serverProxy;

import static com.example.familymapclient.serverProxy.ServerReadWrite.readString;

import android.os.Handler;

import com.example.familymapclient.cache.DataCache;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Request.EventRequest;
import Request.LoginRequest;
import Request.RegisterRequest;
import Result.EventResult;

public class ServerProxy { //extends Thread

    String serverHost = "10.0.2.2";
    String serverPort = "8080";


    public ServerProxy(String host, String port) {
        serverHost = host;
        serverPort = port;
    }


    public void register(RegisterRequest registerRequest, Handler theHandler){
        //Executor
        ExecutorService executor = Executors.newSingleThreadExecutor();

        ServerRegister serverRegister = new ServerRegister(registerRequest,serverHost,serverPort, theHandler);
        executor.execute(serverRegister);

    }
    public void login(LoginRequest theRequest, Handler theHandler){
        ExecutorService executor = Executors.newSingleThreadExecutor();

        ServerLogin serverLogin = new ServerLogin(theRequest, serverHost, serverPort, theHandler);
        executor.execute(serverLogin);
    }

    public void cacheEvents(EventRequest eventRequest, Handler theHandler) {
        //TODO
        ExecutorService executor = Executors.newSingleThreadExecutor();

        //ServerLogin serverLogin = new (eventRequest, serverHost, serverPort, theHandler);
        //executor.execute(serverLogin);
    }
    public void cachePeople(String authtoken, Handler theHandler) {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        ServerPerson serverPerson = new ServerPerson(authtoken, serverHost, serverPort, theHandler);
        executor.execute(serverPerson);
    }
    public void cachePeopleWithID(String authtoken, Handler theHandler, String ID) {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        ServerPersonWithID serverPersonWithID = new ServerPersonWithID(authtoken, ID, serverHost, serverPort, theHandler);
        executor.execute(serverPersonWithID);
    }



    public EventResult getEvents(EventRequest theRequest) {
        return null;
    }



    //EXAMPLE
    // The getGameList method calls the server's "/games/list" operation to
    // retrieve a list of games running in the server in JSON format
    private static void getGameList(String serverHost, String serverPort) {

        // This method shows how to send a GET request to a server

        try {
            // Create a URL indicating where the server is running, and which
            // web API operation we want to call
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/games/list");


            // Start constructing our HTTP request
            HttpURLConnection http = (HttpURLConnection) url.openConnection();


            // Specify that we are sending an HTTP GET request
            http.setRequestMethod("GET");

            // Indicate that this request will not contain an HTTP request body
            http.setDoOutput(false);


            // Add an auth token to the request in the HTTP "Authorization" header
            http.addRequestProperty("Authorization", "afj232hj2332");

            // Specify that we would like to receive the server's response in JSON
            // format by putting an HTTP "Accept" header on the request (this is not
            // necessary because our server only returns JSON responses, but it
            // provides one more example of how to add a header to an HTTP request).
            http.addRequestProperty("Accept", "application/json");


            // Connect to the server and send the HTTP request
            http.connect();

            // By the time we get here, the HTTP response has been received from the server.
            // Check to make sure that the HTTP response from the server contains a 200
            // status code, which means "success".  Treat anything else as a failure.
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {

                // Get the input stream containing the HTTP response body
                InputStream respBody = http.getInputStream();

                // Extract JSON data from the HTTP response body
                String respData = readString(respBody);

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
    }

}
