package com.example.rum8.listeners;

import com.example.rum8.dataModels.LinkListSingleLink;

import java.util.ArrayList;

public interface ViewLinkListControllerListener {
    void goToProfileSettings();
    void goToLogin();
    void goToViewLinkList();
    void goToAdvSettings();
    void addNewLink(LinkListSingleLink link);
    void displayLinks(ArrayList<LinkListSingleLink> links);
    ArrayList<LinkListSingleLink> getLinks();
}