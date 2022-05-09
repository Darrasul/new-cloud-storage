package com.buzas.cloud.application.model;

public enum MessageType {
    FILE("file"),
    LIST("list"),
    DELETE("delete"),
    DOWNLOAD("download"),
    DELIVER("deliver"),
    ERROR_DOWNLOAD("error_download"),
    REFRESH("refresh"),
    FILE_INFO("file_info"),
    INFO_DELIVER("info_deliver"),
    DIRECTORY_REQUEST("directory_request"),
    DIRECTORY_ANSWER("directory_answer");

    private final String name;

    MessageType(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
