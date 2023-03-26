package com.example.familymapclient.ui.login;

import androidx.annotation.Nullable;

/**
 * Data validation state of the login form.
 */
class LoginFormState {
    @Nullable
    private Integer usernameError;
    @Nullable
    private Integer passwordError;
    public boolean isDataValidForLogin;
    public boolean isDataValidForAll;

    //Default mfing constructor, used for valid for All
    LoginFormState() {
    }
    LoginFormState(@Nullable Integer usernameError, @Nullable Integer passwordError) {
        this.usernameError = usernameError;
        this.passwordError = passwordError;
        this.isDataValidForLogin = false;
    }

    LoginFormState(boolean isDataValid) {
        this.usernameError = null;
        this.passwordError = null;
        this.isDataValidForLogin = isDataValid;
    }
    LoginFormState(boolean isDataValid,boolean isDataValidForAll) {
        this.usernameError = null;
        this.passwordError = null;
        this.isDataValidForLogin = isDataValid;
        this.isDataValidForAll = isDataValidForAll;
    }
    public void LoginFormStateForAll(boolean isDataValid) {
        this.isDataValidForAll = isDataValid;
    }

    @Nullable
    Integer getUsernameError() {
        return usernameError;
    }

    @Nullable
    Integer getPasswordError() {
        return passwordError;
    }

    boolean isDataValidForLogin() {
        return isDataValidForLogin;
    }
}