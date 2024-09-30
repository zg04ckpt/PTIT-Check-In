package app.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.NonNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "Rooms")
public class Room {
    @Id
    private String id;
    @NonNull
    @Size(max = 100)
    private String name;
    @NonNull
    @Size(max = 100)
    private String createBy;
    private boolean checkLocation;
    @NonNull
    private double latitude;
    @NonNull
    private double longitude;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean auto;
    @NonNull
    private LocalDateTime createOn;
    @NonNull
    private String code;
    @NonNull
    private String url;

    public Room() {
    }

    public Room(String id, String name, String createBy, boolean checkLocation, double latitude, double longitude, LocalDateTime startTime, LocalDateTime endTime, boolean auto, LocalDateTime createOn, String code, String url) {
        this.id = id;
        this.name = name;
        this.createBy = createBy;
        this.checkLocation = checkLocation;
        this.latitude = latitude;
        this.longitude = longitude;
        this.startTime = startTime;
        this.endTime = endTime;
        this.auto = auto;
        this.createOn = createOn;
        this.code = code;
        this.url = url;
    }
}
