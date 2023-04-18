import com.example.familymapclient.cache.DataCache;
import com.example.familymapclient.serverProxy.ServerProxy;

import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import Model.Person;
import Request.LoginRequest;
import Request.RegisterRequest;
import Result.LoginResult;
import Result.RegisterResult;

public class ServerProxyTest {
    //Test cases for
    //Login method
    //Registering a new user
    //Retrieving people related to a logged in/registered user
    //Retrieving events related to a logged in/registered user
    @Before
    public void cleanUp(){
        DataCache.resetCacheForTesting();
    }
    @Test
    public void registerNewUser(){
        RegisterRequest registerRequest= new RegisterRequest();
        registerRequest.setUsername(UUID.randomUUID().toString().substring(0,8));
        registerRequest.setGender("m");
        registerRequest.setPassword("Password");
        registerRequest.setFirstName("FirstName");
        registerRequest.setLastName("LastName");
        registerRequest.setEmail("Email@gmail.com");
        ServerProxy proxy = new ServerProxy("localhost", "8080"); //LOCALHOST for java testing and 10.0.2.2 for emulator

        proxy.register(registerRequest);
        //DataCache dataCache = DataCache.getInstance(); //dataCaches are there for debug
        assert DataCache.getInstance().authToken!=null;
        RegisterResult result = DataCache.getInstance().registerResult;
        assert result.isSuccess();
    }
    @Test
    public void registerNewUserNegative(){
        RegisterRequest registerRequest= new RegisterRequest();
        registerRequest.setUsername("username"); //Should already have username in there
        registerRequest.setGender("m");
        registerRequest.setPassword("Password");
        registerRequest.setFirstName("FirstName");
        registerRequest.setLastName("LastName");
        registerRequest.setEmail("Email@gmail.com");
        ServerProxy proxy = new ServerProxy("localhost", "8080"); //LOCALHOST for java testing and 10.0.2.2 for emulator

        proxy.register(registerRequest);
        //DataCache dataCache = DataCache.getInstance();
        assert DataCache.getInstance().authToken==null;
        RegisterResult result = DataCache.getInstance().registerResult;
        assert result==null;
    }
    @Test
    public void registerNewUserNegative2(){
        RegisterRequest registerRequest= new RegisterRequest();
        registerRequest.setUsername("username"); //Should already have username in there
        registerRequest.setGender("m/f"); //NOT valid
        registerRequest.setPassword("Password");
        registerRequest.setFirstName("FirstName");
        registerRequest.setLastName("LastName");
        registerRequest.setEmail("Email@gmail.com");
        ServerProxy proxy = new ServerProxy("localhost", "8080"); //LOCALHOST for java testing and 10.0.2.2 for emulator, so this should fail

        proxy.register(registerRequest);
        //DataCache dataCache = DataCache.getInstance();
        assert DataCache.getInstance().authToken==null;
        RegisterResult result = DataCache.getInstance().registerResult;
        assert result==null;
    }
    @Test
    public void login(){
        //Make sure that the local host 8080 default has been put in, and hasn't been cleared recently otherwise it will fail
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("username");
        loginRequest.setPassword("password");

        ServerProxy proxy = new ServerProxy("localhost", "8080"); //LOCALHOST for java testing and 10.0.2.2 for emulator

        proxy.login(loginRequest);
        //DataCache dataCache = DataCache.getInstance();
        assert DataCache.getInstance().authToken!=null;
        LoginResult result = DataCache.getInstance().loginResult;
        assert result.isSuccess();
    }
    @Test
    public void login2(){
        //Make sure that the local host 8080 default has been put in, and hasn't been cleared recently otherwise it will fail
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("sheila");
        loginRequest.setPassword("parker");

        ServerProxy proxy = new ServerProxy("localhost", "8080"); //LOCALHOST for java testing and 10.0.2.2 for emulator

        proxy.login(loginRequest);
        //DataCache dataCache = DataCache.getInstance();
        assert DataCache.getInstance().authToken!=null;
        LoginResult result = DataCache.getInstance().loginResult;
        assert result.isSuccess();
    }
    @Test
    public void loginNegative(){
        //Make sure that the local host 8080 default has been put in, and hasn't been cleared recently otherwise it will fail
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(UUID.randomUUID().toString().substring(0,8));
        loginRequest.setPassword("parker");

        ServerProxy proxy = new ServerProxy("localhost", "8080"); //LOCALHOST for java testing and 10.0.2.2 for emulator

        proxy.login(loginRequest);
        //DataCache dataCache = DataCache.getInstance();
        assert DataCache.getInstance().authToken==null;
        LoginResult result = DataCache.getInstance().loginResult;
        assert result==null;
    }
    @Test
    public void createDataFromNewUser(){
        //Previous register
        RegisterRequest registerRequest= new RegisterRequest();
        String username = UUID.randomUUID().toString().substring(0,8);
        registerRequest.setUsername(username);
        registerRequest.setGender("m");
        registerRequest.setPassword("Password");
        registerRequest.setFirstName("FirstName");
        registerRequest.setLastName("LastName");
        registerRequest.setEmail("Email@gmail.com");
        ServerProxy proxy = new ServerProxy("localhost", "8080"); //LOCALHOST for java testing and 10.0.2.2 for emulator

        proxy.register(registerRequest);
        //DataCache dataCache = DataCache.getInstance();
        assert DataCache.getInstance().authToken!=null;
        RegisterResult result = DataCache.getInstance().registerResult;
        assert result.isSuccess();


        //Grabbing new data
        proxy.cacheUserPeopleWithID(DataCache.getInstance().registerResult.getAuthtoken(),
                DataCache.getInstance().registerResult.getPersonID());
        Person user = DataCache.getInstance().theUserPerson;
        assert user!=null;
        assert user.getFirsName().compareToIgnoreCase("FirstName")==0;
        assert user.getAssociatedUsername().compareToIgnoreCase(username)==0;
        assert user.getGender().compareToIgnoreCase("m")==0;
        assert user.getMotherID()!=null;
        assert user.getFatherID()!=null;
    }
    @Test
    public void createDataFromNewUserNegative(){
        //Previous register
        RegisterRequest registerRequest= new RegisterRequest();
        String username = UUID.randomUUID().toString().substring(0,8);
        registerRequest.setUsername(username);
        registerRequest.setGender("m");
        registerRequest.setPassword("Password");
        registerRequest.setFirstName("FirstName");
        registerRequest.setLastName("LastName");
        registerRequest.setEmail("Email@gmail.com");
        ServerProxy proxy = new ServerProxy("localhost", "8080"); //LOCALHOST for java testing and 10.0.2.2 for emulator

        proxy.register(registerRequest);
        //DataCache dataCache = DataCache.getInstance();
        assert DataCache.getInstance().authToken!=null;
        RegisterResult result = DataCache.getInstance().registerResult;
        assert result.isSuccess();


        RegisterResult registerResultBad = result;
        registerResultBad.setAuthtoken("No AuthToken To See Here"); //Breaks it

        //Grabbing new data
        proxy.cacheUserPeopleWithID(registerResultBad.getAuthtoken(),
                DataCache.getInstance().registerResult.getPersonID());
        Person user = DataCache.getInstance().theUserPerson;
        assert user==null;
    }
    @Test
    public void createDataLogin(){
        //Previous login
        //Make sure that the local host 8080 default has been put in, and hasn't been cleared recently otherwise it will fail
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("sheila");
        loginRequest.setPassword("parker");

        ServerProxy proxy = new ServerProxy("localhost", "8080"); //LOCALHOST for java testing and 10.0.2.2 for emulator

        proxy.login(loginRequest);
        //DataCache dataCache = DataCache.getInstance();
        assert DataCache.getInstance().authToken!=null;
        LoginResult result = DataCache.getInstance().loginResult;
        assert result.isSuccess();


//        Grabbing new data
        proxy.cacheUserPeopleWithID(result.getAuthtoken(),
                result.getPersonID());
        Person user = DataCache.getInstance().theUserPerson;
        assert user!=null;
        assert user.getFirsName().compareToIgnoreCase("sheila")==0;
        assert user.getAssociatedUsername().compareToIgnoreCase("sheila")==0;
        assert user.getGender().compareToIgnoreCase("f")==0;
        String mother = user.getMotherID();
        String father = user.getFatherID();
        assert mother.compareTo("Betty_White")==0;
        assert father.compareTo("Blaine_McGary")==0;
    }
    @Test
    public void createDataLoginNegative(){
        //Previous login
        //Make sure that the local host 8080 default has been put in, and hasn't been cleared recently otherwise it will fail
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("sheila");
        loginRequest.setPassword("parker");

        ServerProxy proxy = new ServerProxy("localhost", "8080"); //LOCALHOST for java testing and 10.0.2.2 for emulator

        proxy.login(loginRequest);
        //DataCache dataCache = DataCache.getInstance();
        assert DataCache.getInstance().authToken!=null;
        LoginResult result = DataCache.getInstance().loginResult;
        assert result.isSuccess();

        result.setAuthtoken("No more Authtoken"); //Should break it

//        Grabbing new data
        proxy.cacheUserPeopleWithID(result.getAuthtoken(),
                result.getPersonID());
        Person user = DataCache.getInstance().theUserPerson;
        assert user==null;
    }

}
