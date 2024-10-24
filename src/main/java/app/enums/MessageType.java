package app.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.NUMBER)
public enum MessageType { // Dành cho websocket
    ATTENDEE_STATUS,
    ROOM_STATUS,
}
