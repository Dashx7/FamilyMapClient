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
        registerRequest.setGender("M");
        registerRequest.setPassword("Password");
        registerRequest.setFirstName("FirstName");
        registerRequest.setLastName("LastName");
        ServerProxy proxy = new ServerProxy("10.0.2.2", "8080");
        RegisterResult result;
        result = proxy.register(registerRequest);

        assert (result!=null);
        assert (result.isSuccess());

    }

}
