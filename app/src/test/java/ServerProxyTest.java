import com.example.familymapclient.serverProxy.ServerProxy;

import org.junit.Test;

import java.util.UUID;

import Request.RegisterRequest;
import Result.RegisterResult;

public class ServerProxyTest {
    @Test
    public void register(){
        RegisterRequest registerRequest= new RegisterRequest();
        registerRequest.setUsername(UUID.randomUUID().toString().substring(0,8));
        registerRequest.setGender("m");
        registerRequest.setPassword("Password");
        registerRequest.setFirstName("FirstName");
        registerRequest.setLastName("LastName");
        registerRequest.setEmail("Email@gmail.com");
        ServerProxy proxy = new ServerProxy("localhost", "8080"); //LOCALHOST for java testing and 10.0.2.2 for emulator
        RegisterResult result;
        result = proxy.register(registerRequest);

        assert (result!=null);
        assert (result.isSuccess());

    }

}
