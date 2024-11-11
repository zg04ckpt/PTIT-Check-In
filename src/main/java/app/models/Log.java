package app.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Logs")
public class Log {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    private String ip;
    @Column(nullable = false)
    private LocalDateTime time;
    @Column(nullable = false)
    private String description;
    private String attendeeId;
    private String roomId;

    public Log() {
    }

    public Log(String ip, String description, String attendeeId, String roomId) {
        this.ip = ip;
        this.description = description;
        this.time = LocalDateTime.now();
        this.attendeeId = attendeeId;
        this.roomId = roomId;
    }
}
