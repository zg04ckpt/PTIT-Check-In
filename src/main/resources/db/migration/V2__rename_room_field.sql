ALTER TABLE rooms CHANGE is_check_location_enabled require_check_location BIT(1);
ALTER TABLE rooms CHANGE is_auto_approval enable_auto_approval BIT(1);