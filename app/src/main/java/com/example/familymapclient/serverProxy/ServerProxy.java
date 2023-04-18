package com.example.familymapclient.serverProxy;
//To avoid code duplications


//Imports
import android.os.Handler;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Request.EventRequest;
import Request.LoginRequest;
import Request.RegisterRequest;

public class ServerProxy {

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
    public void register(RegisterRequest registerRequest){
        ServerRegister serverRegister = new ServerRegister(registerRequest,serverHost,serverPort);
        serverRegister.run();
    }
    public void login(LoginRequest theRequest, Handler theHandler){
        ExecutorService executor = Executors.newSingleThreadExecutor();

        ServerLogin serverLogin = new ServerLogin(theRequest, serverHost, serverPort, theHandler);
        executor.execute(serverLogin);
    }
    public void login(LoginRequest theRequest){
        ServerLogin serverLogin = new ServerLogin(theRequest, serverHost, serverPort);
        serverLogin.run();
    }

    public void cacheEvents(EventRequest eventRequest, Handler theHandler, String authToken) {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        ServerEvent serverLogin = new ServerEvent(eventRequest, serverHost, serverPort, theHandler, authToken);
        executor.execute(serverLogin);
    }
    public void cacheEvents(EventRequest eventRequest, String authToken) {
        ServerEvent serverLogin = new ServerEvent(eventRequest, serverHost, serverPort, authToken);
        serverLogin.run();
    }
    public void cachePeople(String authtoken, Handler theHandler) {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        ServerPerson serverPerson = new ServerPerson(authtoken, serverHost, serverPort, theHandler);
        executor.execute(serverPerson);
    }
    public void cachePeople(String authtoken) {
        ServerPerson serverPerson = new ServerPerson(authtoken, serverHost, serverPort);
        serverPerson.run();
    }
    public void cacheUserPeopleWithID(String authtoken, Handler theHandler, String ID) {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        ServerPersonWithID serverPersonWithID = new ServerPersonWithID(authtoken, ID, serverHost, serverPort, theHandler);
        executor.execute(serverPersonWithID);
    }
    public void cacheUserPeopleWithID(String authtoken, String ID) {
        ServerPersonWithID serverPersonWithID = new ServerPersonWithID(authtoken, ID, serverHost, serverPort);
        serverPersonWithID.run();
    }

}
