package com.example.rum8.listeners;

public interface MainControllerListener {

    void goToProfileSettings();

    void goToLogin();

    void goToLinkList();

    void showToast(final String message, final int toastLength);

}
