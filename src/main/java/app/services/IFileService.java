package app.services;

import java.io.ByteArrayInputStream;

public interface IFileService {
    ByteArrayInputStream exportDataToExcelFile(String roomId);
}
