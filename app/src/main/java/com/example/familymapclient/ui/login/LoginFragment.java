package com.example.familymapclient.ui.login;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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

import com.example.familymapclient.cache.DataCache;
import com.example.familymapclient.databinding.FragmentLoginBinding;
import com.example.familymapclient.serverProxy.ServerProxy;

import Request.LoginRequest;
import Request.RegisterRequest;

public class LoginFragment extends Fragment {

    private LoginViewModel loginViewModel;
    private FragmentLoginBinding binding;

    public int DEFAULT_USER_DEBUGGING = 1;


    private Listener listener;

    public interface Listener {
        void notifyDone();
    }

    public void registerListener(Listener listener) {
        this.listener = listener;
    }


    @Nullable
    @Override
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

        //So I can quickly test things
        if(DEFAULT_USER_DEBUGGING ==0){
            usernameEditText.setText("username");
            passwordEditText.setText("password");
            hostEditText.setText("10.0.2.2");
            portEditText.setText("8080");
        }
        else if(DEFAULT_USER_DEBUGGING ==1){
            usernameEditText.setText("sheila");
            passwordEditText.setText("parker");
            hostEditText.setText("10.0.2.2");
            portEditText.setText("8080");
        }


        final RadioGroup genderRadioGroup = binding.radioGroup2;
        final RadioButton radioButton1 = binding.radioButton;
        final RadioButton radioButton2 = binding.radioButton2;


        final Button loginButton = binding.login;
        final Button registerButton = binding.Register;

        final ProgressBar loadingProgressBar = binding.loading;

        loginViewModel.getLoginFormState().observe(getViewLifecycleOwner(), new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValidForLogin());
                registerButton.setEnabled(loginFormState.isDataValidForAll);

                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });
        loginViewModel.getLoginFormStateForLogin().observe(getViewLifecycleOwner(), new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValidForLogin());
                registerButton.setEnabled(loginFormState.isDataValidForAll);

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
                if (radioID == radioButton1.getId()) {
                    gender = "M";
                } else if (radioID == radioButton2.getId()) {
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

        genderRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int id) {
                int radioID = genderRadioGroup.getCheckedRadioButtonId();
                String gender = null;
                //Or just say radioID = id;
                if (radioID == radioButton1.getId()) {
                    gender = "M";
                } else if (radioID == radioButton2.getId()) {
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

        //Password thing
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

                //Each handler is another "step", each one waits till the last one to finish to start
                Handler handler = new Handler(Looper.getMainLooper()){
                    @Override
                    public void handleMessage(@NonNull Message msg){
                        super.handleMessage(msg);
                        if(msg.getData().getBoolean("SuccessMessage")){
                            Handler handler2 = new Handler(Looper.getMainLooper()) {
                                @Override
                                public void handleMessage(@NonNull Message msg) {
                                    super.handleMessage(msg);

                                    //DataCache test = DataCache.getInstance(); //For debug
                                    String welcome = "Welcome: " + DataCache.getInstance().theUserPerson.getFirsName() + ", " +
                                            DataCache.getInstance().theUserPerson.getLastName();
                                    Toast.makeText(getContext(), welcome, Toast.LENGTH_SHORT).show();

                                    DataCache.getInstance().serverHost = hostEditText.getText().toString();
                                    DataCache.getInstance().serverPort = portEditText.getText().toString();

                                    //This is the problem figure it out
                                    Handler handler3 = new Handler(Looper.getMainLooper()){
                                        @Override
                                        public void handleMessage(@NonNull Message msg) {
                                            listener.notifyDone();
                                        }
                                    };
                                    serverProxy.cachePeople(DataCache.getInstance().loginResult.getAuthtoken(), handler3);

                                }
                            };
                            serverProxy.cacheUserPeopleWithID(DataCache.getInstance().loginResult.getAuthtoken(), handler2,
                                    DataCache.getInstance().loginResult.getPersonID());

                        }
                        else if(!msg.getData().getBoolean("SuccessMessage")){
                            Toast.makeText(getContext(), "Error, Login not successful", Toast.LENGTH_SHORT).show();
                        }
                    }
                };
                serverProxy.login(loginRequest, handler);

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
                if (radioID == radioButton1.getId()) {
                    registerRequest.setGender("m");
                } else if (radioID == radioButton2.getId()) {
                    registerRequest.setGender("f");
                }

                //Create a serverProxy
                ServerProxy serverProxy = new ServerProxy(hostEditText.getText().toString(),
                        portEditText.getText().toString());

                //Each handler is another "step", each one waits till the last one to finish to start
                Handler handler = new Handler(Looper.getMainLooper()){
                    @Override
                    public void handleMessage(@NonNull Message msg){
                        super.handleMessage(msg);
                        if(msg.getData().getBoolean("SuccessMessage")){
                            Handler handler2 = new Handler(Looper.getMainLooper()) {
                                @Override
                                public void handleMessage(@NonNull Message msg) {
                                    if(msg.getData().getBoolean("SuccessMessagePersonWithID")){
                                        super.handleMessage(msg);
                                        String welcome = "Welcome: " + DataCache.getInstance().theUserPerson.getFirsName() + ", " +
                                                DataCache.getInstance().theUserPerson.getLastName();

                                        //Toast.makeText(getContext(), welcome, Toast.LENGTH_SHORT).show();
                                        //Doesn't work, don't question it

                                        DataCache.getInstance().serverHost = hostEditText.getText().toString();
                                        DataCache.getInstance().serverPort = portEditText.getText().toString();

                                        Handler handler3 = new Handler(Looper.getMainLooper()){
                                            @Override
                                            public void handleMessage(@NonNull Message msg) {
                                                listener.notifyDone();
                                            }
                                        };

                                        serverProxy.cachePeople(DataCache.getInstance().registerResult.getAuthtoken(), handler3);
                                    }
                                    else {
                                        Toast.makeText(getContext(), "Error, Register not successful", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            };
                            serverProxy.cacheUserPeopleWithID(DataCache.getInstance().registerResult.getAuthtoken(), handler2,
                                    DataCache.getInstance().registerResult.getPersonID());
                        }
                        else{
                            Toast.makeText(getContext(), "Error, Register not successful", Toast.LENGTH_SHORT).show();
                        }

                    }
                };
                serverProxy.register(registerRequest,handler); //register it

            }
        });
    }


    //Black magic coding stuff that came with the loginFragment
    private void updateUiWithUser(LoggedInUserView model) {
    }

    private void showLoginFailed(@StringRes Integer errorString) {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}