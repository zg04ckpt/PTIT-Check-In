package app.dtos.system;

import app.enums.MessageType;
import com.google.gson.JsonObject;

public class MessageDTO<T> {
    public MessageType type;
    public String roomId;
    public T data;
    public String ip;
}
