package app.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.NUMBER)
public enum RoomStatus {
    PENDING,
    OPENING,
    CLOSED
}
