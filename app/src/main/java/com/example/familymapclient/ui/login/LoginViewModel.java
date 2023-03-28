package com.example.familymapclient.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.util.Patterns;

import com.example.familymapclient.data.LoginRepository;
import com.example.familymapclient.data.Result;
import com.example.familymapclient.data.model.LoggedInUser;
import com.example.familymapclient.R;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginFormState> loginFormStateForLogin = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository;

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(String username, String password) {
        // can be launched in a separate asynchronous job
        Result<LoggedInUser> result = loginRepository.login(username, password);

        if (result instanceof Result.Success) {
            LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
            loginResult.setValue(new LoginResult(new LoggedInUserView(data.getDisplayName())));
        } else {
            loginResult.setValue(new LoginResult(R.string.login_failed));
        }
    }

    //When the data is changed the form changes
    public void loginDataChanged(String username, String password, String host, String port,
    String firstName, String lastName, String email, String gender) {

        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        }
        else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        }
        else if(host==null || host.compareTo("")==0){
            loginFormState.setValue(new LoginFormState(false, false));
        }
        else if(port==null || port.compareTo("")==0){
            loginFormState.setValue(new LoginFormState(false, false));
        }
        else if(firstName==null || firstName.compareTo("")==0){
            loginFormState.setValue(new LoginFormState(true, false));
        }
        else if(lastName== null || lastName.compareTo("")==0){
            loginFormState.setValue(new LoginFormState(true,false));
        }
        else if(email ==null || email.compareTo("")==0){
            loginFormState.setValue(new LoginFormState(true,false));
        }
        else if(gender==null){
            loginFormState.setValue(new LoginFormState(true,false));
        }
        else {
            //Everything is valid and the login form is correct for both
            loginFormState.setValue(new LoginFormState(true,true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}