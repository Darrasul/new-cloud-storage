package com.buzas.cloud.model;

import java.io.Serializable;

public abstract class AbstractMessage implements Serializable {
    public abstract MessageType getMessageType();
}
