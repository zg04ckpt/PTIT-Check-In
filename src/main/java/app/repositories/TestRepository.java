package app.repositories;

import app.models.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

//định nghĩa các truy vấn tới CSDL để lấy hoặc thay đổi dữ liệu
public interface TestRepository extends JpaRepository<Test, String > {
    /*
        Một số có sẵn:
            + save(Entity e): Lưu / tạo mới đối tượng e
            + saveAll(List<Entity> es): Lưu / tạo nhiều
            + getReferenceById(String id): Tim và lấy về đối tượng bằng Id
            + getAll(): Lấy tất cả
            + deleteById(String id): Xóa đôí tượng bằng id
            + count(): trả về số dòng ~ số lượng entity
    */

    //2 kiểu viết repo:

    //Kieu 1 là viết tên phương thức theo mẫu
    Test findByProp1(String prop1);
    Test findByProp1AndProp2(String prop1, String prop2);
    Test existsByProp1(String prop1);

    //Kiểu 2 tự định nghĩa Query
    //Lưu ý: Test là lên class, prop1 là trường của class
    @Query("SELECT t FROM Test t WHERE t.prop1 = :prop1 AND t.prop2 = :prop2")
    Test findByCustomProp1AndProp2(@Param("prop1") String prop1, @Param("prop2") String prop2);
}
