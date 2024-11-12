package app.services.impl;

import app.enums.CheckInStatus;
import app.models.Attendee;
import app.repositories.IRoomRepository;
import app.services.IFileService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class FileService implements IFileService {
    private final IRoomRepository roomRepository;

    public FileService(IRoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Override
    public ByteArrayInputStream exportDataToExcelFile(String roomId) {
        try(
                Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()
        ) {
            List<Attendee> data = roomRepository.getReferenceById(roomId).getAttendees();

            Sheet sheet = workbook.createSheet("Kết quả");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("STT");
            header.createCell(1).setCellValue("Mã điểm danh");
            header.createCell(2).setCellValue("Họ và tên");
            header.createCell(3).setCellValue("Vắng");

            for(int i = 1; i <= data.size(); i++) {
                Row row = sheet.createRow(i);
                Attendee attendee = data.get(i - 1);
                row.createCell(0).setCellValue(i);
                row.createCell(1).setCellValue(attendee.getCheckInCode());
                row.createCell(2).setCellValue(attendee.getName());
                if(attendee.getCheckInStatus() != CheckInStatus.ACCEPTED) {
                    row.createCell(3).setCellValue("v");
                } else {
                    row.createCell(3).setCellValue("");
                }
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            return null;
        }
    }
}
