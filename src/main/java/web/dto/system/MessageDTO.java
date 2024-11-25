package web.dto.system;

import web.model.enums.MessageType;

public class MessageDTO<T> {
    public MessageType type;
    public String roomId;
    public T data;
    public String ip;
}
