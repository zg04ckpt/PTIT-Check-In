CREATE TABLE rooms (
  id VARCHAR(255) NOT NULL,
   name VARCHAR(100) NULL,
   create_by VARCHAR(100) NULL,
   check_location BIT(1) NOT NULL,
   latitude DOUBLE NOT NULL,
   longitude DOUBLE NOT NULL,
   start_time datetime NULL,
   end_time datetime NULL,
   auto BIT(1) NOT NULL,
   create_on datetime NULL,
   code VARCHAR(255) NULL,
   url VARCHAR(255) NULL,
   CONSTRAINT pk_rooms PRIMARY KEY (id)
);

CREATE TABLE attendees (
  id VARCHAR(255) NOT NULL,
   full_name VARCHAR(100) NULL,
   latitude DOUBLE NOT NULL,
   longitude DOUBLE NOT NULL,
   attend_on datetime NULL,
   room_id VARCHAR(255) NOT NULL,
   CONSTRAINT pk_attendees PRIMARY KEY (id)
);

ALTER TABLE attendees ADD CONSTRAINT FK_ATTENDEES_ON_ROOM FOREIGN KEY (room_id) REFERENCES rooms (id);