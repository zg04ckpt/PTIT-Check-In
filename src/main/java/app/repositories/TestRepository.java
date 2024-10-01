package app.repositories;

import app.models.Test;
import org.springframework.data.jpa.repository.JpaRepository;

//định nghĩa các truy vấn tới CSDL để lấy hoặc thay đổi dữ liệu
public interface TestRepository extends JpaRepository<Test, String > {
    /* nếu cần truy vấn phức tạp thì thêm, ví dụ truy vấn sử dụng cú pháp tên tham số:
        @Query("SELECT u FROM User u WHERE u.prop1 = :prop1 AND u.prop2 = :prop2")
        List<User> findByProp1AndProp2(@Param("prop1") String prop1, @Param("prop2") boolean prop2);
        (Luu ý User sẽ là tên class trong Model chứ không phải ten bảng trong DB)
    */

    /* 1 kiểu truy vấn mà ko cần @Query là viết tên phương thức theo định dạng
        VD: tìm kiếm theo giá trị của thuộc tính bất kì của Test:
            Test findBy + <tên_thuộc_tính_viết_hoa_chữ_cái_đầu>(Kiểu_của_thuộc_tính giá_trị_cần_tìm);
    */
    Test findByProp1(String prop1);
}
