INSERT INTO rooms (id, name, create_by, require_check_location, latitude, longitude, accept_range, start_time, end_time, enable_auto_approval, create_on, code, url, is_ended)
VALUES ('6f7ffd60-959a-4953-9444-6bf0919dbe4a', 'Dữ liệu mẫu', 'Dữ liệu mẫu', false, 0.0, 0.0, 0.0, NULL, NULL, false, CURRENT_TIMESTAMP, 'HS12JK', 'test-url', false);

INSERT INTO attendees (id, check_in_code, name, check_in_status, latitude, longitude, ip, attend_on, room_id)
VALUES
('0825e6e4-a667-4d2c-b9b6-3099bea171d5', 'B22DCCN001', 'Hoàng Cao Nguyên', 1, 0.0, 0.0, NULL, NULL, '6f7ffd60-959a-4953-9444-6bf0919dbe4a'),
('4bfa3481-1333-414c-864a-4c4bf0f38df0', 'B22DCCN002', 'Nguyễn Tiến Hưng', 1, 0.0, 0.0, NULL, NULL, '6f7ffd60-959a-4953-9444-6bf0919dbe4a'),
('fb779f94-d7ac-4119-a8d9-b2a833252dde', 'B22DCCN003', 'Vũ Thành Nam', 1, 0.0, 0.0, NULL, NULL, '6f7ffd60-959a-4953-9444-6bf0919dbe4a'),
('fcf5abc7-c008-41bc-9a20-eb9abbffcd10', 'B22DCCN004', 'Bùi Tiến Thịnh', 0, 0.0, 0.0, NULL, NULL, '6f7ffd60-959a-4953-9444-6bf0919dbe4a');
