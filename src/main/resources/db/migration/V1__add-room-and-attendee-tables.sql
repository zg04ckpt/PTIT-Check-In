CREATE TABLE rooms (
  id VARCHAR(255) NOT NULL,
   name VARCHAR(255) NOT NULL,
   create_by VARCHAR(255) NOT NULL,
   is_check_location_enabled BIT(1) NOT NULL,
   latitude DOUBLE NULL,
   longitude DOUBLE NULL,
   accept_range DOUBLE NULL,
   start_time datetime NULL,
   end_time datetime NULL,
   is_auto_approval BIT(1) NOT NULL,
   create_on datetime NOT NULL,
   code VARCHAR(255) NOT NULL,
   url VARCHAR(255) NOT NULL,
   is_ended BIT(1) NOT NULL,
   CONSTRAINT pk_rooms PRIMARY KEY (id)
);

ALTER TABLE rooms ADD CONSTRAINT uc_rooms_code UNIQUE (code);

ALTER TABLE rooms ADD CONSTRAINT uc_rooms_url UNIQUE (url);

CREATE TABLE attendees (
  id VARCHAR(255) NOT NULL,
   check_in_code VARCHAR(255) NOT NULL,
   name VARCHAR(255) NOT NULL,
   check_in_status SMALLINT NULL,
   latitude DOUBLE NULL,
   longitude DOUBLE NULL,
   ip VARCHAR(255) NULL,
   attend_on datetime NULL,
   room_id VARCHAR(255) NOT NULL,
   CONSTRAINT pk_attendees PRIMARY KEY (id)
);

ALTER TABLE attendees ADD CONSTRAINT FK_ATTENDEES_ON_ROOM FOREIGN KEY (room_id) REFERENCES rooms (id);