package app.services;

import app.dtos.TestDTO;
import app.models.Test;

//định nghĩa tất cả phương thức xử lý liên quan đến đối tượng Test
//xử lý dữ liệu sau khi được kiểm tra hợp lệ ở Controller hoặc sau khi lấy dữ liệu từ Repository
public interface TestService {
    //chỉ định nghĩa tên phương thức, tham số và kiểu trả về
     Test phuongThuc1(TestDTO dto);
}
