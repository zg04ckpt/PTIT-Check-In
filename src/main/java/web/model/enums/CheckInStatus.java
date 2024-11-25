package web.model.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.NUMBER)
public enum CheckInStatus {
    OUT_OF_ROOM,
    WAIT_APPROVAL,
    ACCEPTED,
    REJECTED
}
