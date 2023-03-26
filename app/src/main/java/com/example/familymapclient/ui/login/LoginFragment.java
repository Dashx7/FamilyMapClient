package com.example.familymapclient.ui.login;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.familymapclient.databinding.FragmentLoginBinding;

import com.example.familymapclient.R;
import com.example.familymapclient.serverProxy.ServerProxy;

import Request.LoginRequest;
import Request.RegisterRequest;

public class LoginFragment extends Fragment {

    private LoginViewModel loginViewModel;
    private FragmentLoginBinding binding;


    private Listener listener;
    public interface Listener{
        void notifyDone(); //The heck is this?
    }

    public void registerListener(Listener listener){
        this.listener = listener;
    }


    @Nullable @Override
    //When it is created
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText usernameEditText = binding.username;
        final EditText passwordEditText = binding.password;
        final EditText hostEditText = binding.ServerHost;
        final EditText portEditText = binding.ServerPort;
        final EditText firstNameEditText = binding.FirstName;
        final EditText lastNameEditText = binding.LastName;
        final EditText emailEditText = binding.email;

        final RadioGroup genderRadioGroup = binding.radioGroup2;
        RadioButton radioButton1 = binding.radioButton;
        RadioButton radioButton2 = binding.radioButton2;


        final Button loginButton = binding.login;
        final Button registerButton = binding.Register;

        final ProgressBar loadingProgressBar = binding.loading;

        loginViewModel.getLoginFormState().observe(getViewLifecycleOwner(), new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                if(loginFormState.isDataValidForLogin()){
                    loginButton.setEnabled(loginFormState.isDataValidForLogin());
                }
                if(loginFormState.isDataValidForAll){
                    registerButton.setEnabled(loginFormState.isDataValidForLogin());
                }
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(getViewLifecycleOwner(), new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                }
            }
        });


        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                int radioID = genderRadioGroup.getCheckedRadioButtonId();
                String gender = null;
                if (radioID == radioButton1.getId()){
                    gender = "M";
                }
                else if (radioID == radioButton2.getId()){
                    gender = "F";
                }
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString(),
                        hostEditText.getText().toString(),
                        portEditText.getText().toString(),
                        firstNameEditText.getText().toString(),
                        lastNameEditText.getText().toString(),
                        emailEditText.getText().toString(),
                        gender);
            }
        };

        genderRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int id) {
                int radioID = genderRadioGroup.getCheckedRadioButtonId();
                String gender = null;
                //Or just say radioID = id;
                if (radioID == radioButton1.getId()){
                    gender = "M";
                }
                else if (radioID == radioButton2.getId()){
                    gender = "F";
                }
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString(),
                        hostEditText.getText().toString(),
                        portEditText.getText().toString(),
                        firstNameEditText.getText().toString(),
                        lastNameEditText.getText().toString(),
                        emailEditText.getText().toString(),
                        gender);
            }

        });

        //My listeners that let me know when shit is changed
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        hostEditText.addTextChangedListener(afterTextChangedListener);
        portEditText.addTextChangedListener(afterTextChangedListener);
        firstNameEditText.addTextChangedListener(afterTextChangedListener);
        lastNameEditText.addTextChangedListener(afterTextChangedListener);
        emailEditText.addTextChangedListener(afterTextChangedListener);


        //genderRadioGroup has another watcher somewhere else

        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });


        //Sign in and Register buttons
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadingProgressBar.setVisibility(View.VISIBLE);
                loginViewModel.login(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());

                //New login request
                LoginRequest loginRequest = new LoginRequest();
                loginRequest.setUsername(usernameEditText.getText().toString());
                loginRequest.setPassword(passwordEditText.getText().toString());

                ServerProxy serverProxy = new ServerProxy(hostEditText.getText().toString(),
                        portEditText.getText().toString());
                Result.LoginResult result = serverProxy.login(loginRequest);
                if(result.isSuccess()){
                    listener.notifyDone();
                }
                else {

                }

            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                loginViewModel.login(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());

                //Create a request object
                RegisterRequest registerRequest = new RegisterRequest();
                registerRequest.setUsername(usernameEditText.getText().toString());
                registerRequest.setPassword(passwordEditText.getText().toString());
                registerRequest.setEmail(emailEditText.getText().toString());
                registerRequest.setFirstName(firstNameEditText.getText().toString());
                registerRequest.setLastName(lastNameEditText.getText().toString());
                //Radio button logic
                int radioID = genderRadioGroup.getCheckedRadioButtonId();
                if (radioID == radioButton1.getId()){
                    registerRequest.setGender("M");
                }
                else if (radioID == radioButton2.getId()){
                    registerRequest.setGender("F");
                }

                //Create a serverProxy
                ServerProxy serverProxy = new ServerProxy(hostEditText.getText().toString(),
                        portEditText.getText().toString());
                serverProxy.register(registerRequest); //register it

                Result.RegisterResult result = serverProxy.register(registerRequest);
                if(result==null){
                    System.out.println("I messed up");
                }
                else if(result.isSuccess()){
                    listener.notifyDone();
                }
                else {

                }

            }
        });
    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        if (getContext() != null && getContext().getApplicationContext() != null) {
            Toast.makeText(getContext().getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
        }
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        if (getContext() != null && getContext().getApplicationContext() != null) {
            Toast.makeText(
                    getContext().getApplicationContext(),
                    errorString,
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}