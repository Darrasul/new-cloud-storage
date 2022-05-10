package com.buzas.cloud.application.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TableItem {

    private String name;
    private String size;

    public TableItem(Path path) throws IOException {
        this.name = path.getFileName().toString();
        this.size = String.valueOf(Files.size(path));
    }

    public TableItem(String name, String size) {
        this.name = name;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public String getSize() {
        return size;
    }
}
