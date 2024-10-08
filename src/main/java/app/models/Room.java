package app.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Rooms")
public class Room {
    @Id
    private String id;  //chuỗi string 32 kí tự ngẫu nhiên
    @Column(nullable = false)
    private String name; //tên phòng
    @Column(nullable = false)
    private String createBy; // người tạo phòng
    private boolean isCheckLocationEnabled; //có bật kiểm tra vị trí hay không
    @Column(nullable = true)
    private double latitude; // vĩ độ mốc (phòng)
    @Column(nullable = true)
    private double longitude; // kinh độ mốc (phòng)
    @Column(nullable = true)
    private double acceptRange; // phạm vi hop lệ
    @Column(nullable = true)
    private LocalDateTime startTime; // thời gian mở phòng (null -> mở lúc tạo)
    @Column(nullable = true)
    private LocalDateTime endTime; // thời gian đóng phòng (null ->đóng bằng tay)
    private boolean isAutoApproval; // có bật tự động duyệt hay ko
    @Column(nullable = false)
    private LocalDateTime createOn; // tạo phòng lúc nào
    @Column(nullable = false, unique = true)
    private String code; //mã ngẫu nhiên 6 chữ in hoa duy nhất
    @Column(nullable = false, unique = true)
    private String url; //link vào phòng
    private boolean isEnded; // đã đóng phòng hay chưa

    @OneToMany(mappedBy = "room", cascade = CascadeType.REMOVE)
    private List<Attendee> attendees;

    public Room() {
    }

    public Room(String id, String name, String createBy, boolean isCheckLocationEnabled, double latitude, double longitude, double acceptRange, LocalDateTime startTime, LocalDateTime endTime, boolean isAutoApproval, LocalDateTime createOn, String url, boolean isEnded) {
        this.id = id;
        this.name = name;
        this.createBy = createBy;
        this.isCheckLocationEnabled = isCheckLocationEnabled;
        this.latitude = latitude;
        this.longitude = longitude;
        this.acceptRange = acceptRange;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isAutoApproval = isAutoApproval;
        this.createOn = createOn;
        this.url = url;
        this.isEnded = isEnded;

        this.attendees = new ArrayList<>();
    }

    public void addAttendee(Attendee attendee) {
        this.attendees.add(attendee);
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getCreateOn() {
        return createOn;
    }

    public String getCode() {
        return code;
    }

    public String getUrl() {
        return url;
    }

    public List<Attendee> getAttendees() {
        return attendees;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public boolean isCheckLocationEnabled() {
        return isCheckLocationEnabled;
    }

    public void setCheckLocationEnabled(boolean checkLocationEnabled) {
        isCheckLocationEnabled = checkLocationEnabled;
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

    public double getAcceptRange() {
        return acceptRange;
    }

    public void setAcceptRange(double acceptRange) {
        this.acceptRange = acceptRange;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public boolean isAutoApproval() {
        return isAutoApproval;
    }

    public void setAutoApproval(boolean autoApproval) {
        isAutoApproval = autoApproval;
    }

    public boolean isEnded() {
        return isEnded;
    }

    public void setEnded(boolean ended) {
        isEnded = ended;
    }
}
