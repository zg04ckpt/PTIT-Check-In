package app.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.NonNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "Attendees")
public class Attendee {
    @Id
    private String id;
    @NonNull
    @Size(max = 100)
    private String fullName;
    @NonNull
    private double latitude;
    @NonNull
    private double longitude;
    @NonNull
    private LocalDateTime attendOn;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;
}
