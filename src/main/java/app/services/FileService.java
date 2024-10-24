package app.services;

import app.models.Attendee;

import java.io.ByteArrayInputStream;
import java.util.List;

public interface FileService {
    ByteArrayInputStream exportDataToExcelFile(String roomId);
}
