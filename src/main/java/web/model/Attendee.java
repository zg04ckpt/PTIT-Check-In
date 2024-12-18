package web.model;

import web.model.enums.CheckInStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Attendees")
public class Attendee {
    @Id
    private String id; //chuỗi string 32 kí tự ngẫu nhiên

    @Column(nullable = false)
    private String checkInCode; //mã điểm danh

    @Column(nullable = false)
    private String name; // tên người điểm danh

    @Enumerated(EnumType.ORDINAL)
    private CheckInStatus checkInStatus; // trạng thái

    private double latitude; //Vĩ độ

    private double longitude; //Kinh độ

    private String ip; //IP check-in

    private String userAgent;

    private LocalDateTime attendOn; //time checkin

    @ManyToOne()
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    public Attendee() {
    }

    public Attendee(String id, String checkInCode, String name) {
        this.id = id;
        this.checkInCode = checkInCode;
        this.name = name;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public String getId() {
        return id;
    }

    public String getCheckInCode() {
        return checkInCode;
    }

    public String getName() {
        return name;
    }

    public CheckInStatus getCheckInStatus() {
        return checkInStatus;
    }

    public void setCheckInStatus(CheckInStatus checkInStatus) {
        this.checkInStatus = checkInStatus;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public LocalDateTime getAttendOn() {
        return attendOn;
    }

    public void setAttendOn(LocalDateTime attendOn) {
        this.attendOn = attendOn;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }
}
