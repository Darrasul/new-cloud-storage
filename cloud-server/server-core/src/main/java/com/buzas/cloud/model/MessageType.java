package com.buzas.cloud.model;

public enum MessageType {
    FILE("file"),
    LIST("list"),
    DELETE("delete"),
    DOWNLOAD("download"),
    DELIVER("deliver"),
    ERROR_DOWNLOAD("error_download");

    private final String name;

    MessageType(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
