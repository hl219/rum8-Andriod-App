package com.example.rum8.listeners;

public interface RegistrationControllerListener {

    void onUserRegistered();

    void makeToast(final String message, final int toastLength);

}
