package app.repositories;

import app.models.Test;
import org.springframework.data.jpa.repository.JpaRepository;

//định nghĩa các truy vấn tới CSDL để lấy hoặc thay đổi dữ liệu
public interface TestRepository extends JpaRepository<Test, String > {
    /*
        viết các phương thức truy cập database ngoài các pt sau:
            + save(Entity e): Lưu / tạo mới đối tượng e
            + saveAll(List<Entity> es): Lưu / tạo nhiều
            + getById(String id): Tim và lấy về đối tượng bằng Id
            + getAll(): Lấy tất cả
            + deleteById(String id): Xóa đôí tượng bằng id
            + count(): trả về số dòng ~ số lượng entity
            + ... (tóm lại cái nào có sẵn trong repository. thì ko cần định nghĩa nữa)
        cách định nghĩa phương thức mới: <search GPT với key: các cách tự định nghĩa phương thức trong JPA>
    */

    Test findByProp1(String prop1);
}
