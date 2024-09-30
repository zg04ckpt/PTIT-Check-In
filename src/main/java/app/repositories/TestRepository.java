package app.repositories;

import app.models.Test;
import org.springframework.data.jpa.repository.JpaRepository;

//định nghĩa các truy vấn tới CSDL để lấy hoặc thay đổi dữ liệu
public interface TestRepository extends JpaRepository<Test, String > {
    //nếu cần truy vấn phức tạp thì thêm, con nếu chỉ cần thêm sửa xóa, lấy bằng Id thì để nguyên

    //ví dụ truy vấn tự viết
    // Truy vấn sử dụng cú pháp tên tham số
    //@Query("SELECT u FROM User u WHERE u.prop1 = :prop1 AND u.prop2 = :prop2")
    //List<User> findByProp1AndProp2(@Param("prop1") String prop1, @Param("prop2") boolean prop2);
}
