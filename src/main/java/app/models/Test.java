package app.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

//class test migration
@Entity
@Table(name = "Tests")
public class Test {
    @Id
    private String id; //khóa chính
    private String prop1; // thuoc tinh 1
    private String prop2;

    public Test() {
    }

    public Test(String id, String prop1, String prop2) {
        this.id = id;
        this.prop1 = prop1;
        this.prop2 = prop2;
    }

    //Getter và Setter khi nao dùng mới tạo
}
