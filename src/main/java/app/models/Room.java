package app.models;

import app.enums.RoomStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Entity
@Table(name = "Rooms")
public class Room {
    @Id
    private String id;  //chuỗi string 32 kí tự ngẫu nhiên
    @Column(nullable = false)
    private String name; //tên phòng
    @Column(nullable = false)
    private String createBy; // người tạo phòng
    private boolean requireCheckLocation; //có bật kiểm tra vị trí hay không
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
    private boolean enableAutoApproval; // có bật tự động duyệt hay ko
    @Column(nullable = false)
    private LocalDateTime createOn; // tạo phòng lúc nào
    @Column(nullable = false, unique = true)
    private String code; //mã ngẫu nhiên 6 chữ in hoa duy nhất
    @Column(nullable = false, unique = true)
    private String url; //link vào phòng
    private boolean isEnded; // đã đóng phòng hay chưa
    @Enumerated(EnumType.ORDINAL)
    private RoomStatus status; // trạng thái phòng

    @OneToMany(mappedBy = "room", cascade = CascadeType.REMOVE)
    private List<Attendee> attendees;

    public Room() {
    }

    public Room(String id, String name, String createBy, boolean requireCheckLocation, double latitude, double longitude, double acceptRange, boolean enableAutoApproval, LocalDateTime createOn, boolean isEnded) {
        this.id = id;
        this.name = name;
        this.createBy = createBy;
        this.requireCheckLocation = requireCheckLocation;
        this.latitude = latitude;
        this.longitude = longitude;
        this.acceptRange = acceptRange;
        this.enableAutoApproval = enableAutoApproval;
        this.createOn = createOn;
        this.isEnded = isEnded;

        this.attendees = new ArrayList<>();
    }

    public RoomStatus getStatus() {
        return status;
    }

    public void setStatus(RoomStatus status) {
        this.status = status;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public boolean isRequireCheckLocation() {
        return requireCheckLocation;
    }

    public void setRequireCheckLocation(boolean requireCheckLocation) {
        this.requireCheckLocation = requireCheckLocation;
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

    public boolean isEnableAutoApproval() {
        return enableAutoApproval;
    }

    public void setEnableAutoApproval(boolean enableAutoApproval) {
        this.enableAutoApproval = enableAutoApproval;
    }

    public boolean isEnded() {
        return isEnded;
    }

    public void setEnded(boolean ended) {
        isEnded = ended;
    }

    public void generateRandomCode() {
        String src = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        int codeLen = 6;
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        while (codeLen-- > 0) {
            sb.append(src.charAt(random.nextInt(src.length())));
        }
        this.code = sb.toString();
    }
}
