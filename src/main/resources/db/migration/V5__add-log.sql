CREATE TABLE logs (
  id INT AUTO_INCREMENT NOT NULL,
   ip VARCHAR(255) NOT NULL,
   time datetime NOT NULL,
   `desc` VARCHAR(255) NOT NULL,
   attendee_id VARCHAR(32) NULL,
   room_id VARCHAR(32) NULL,
   CONSTRAINT pk_logs PRIMARY KEY (id)
);

ALTER TABLE logs ADD CONSTRAINT FK_LOGS_ON_ATTENDEE FOREIGN KEY (attendee_id) REFERENCES attendees (id);

ALTER TABLE logs ADD CONSTRAINT FK_LOGS_ON_ROOM FOREIGN KEY (room_id) REFERENCES rooms (id);