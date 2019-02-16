package io.armcha.ribble.presentation.navigationview;

public enum NavigationId {

    HOME("HOME"),
    ORDER("ORDER"),
    FAVORITE("FAVORITE"),
    NOTIFICATION("NOTIFICATION"),
    SETTINGS("SETTINGS"),
    FOODS("FOODS"),
    LOGOUT("LOGOUT");

    private String value = "";

    NavigationId(String name) {
        value = name;
    }

    public String getValue() {
        return value;
    }
}