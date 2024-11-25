package web.service;

import java.io.ByteArrayInputStream;

public interface IFileService {
    ByteArrayInputStream exportDataToExcelFile(String roomId);
    ByteArrayInputStream exportLogDataToExcelFile(String roomId);
}
