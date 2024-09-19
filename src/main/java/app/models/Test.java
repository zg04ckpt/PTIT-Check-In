package app.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

//class test migration
@Entity
public class Test {
    @Id
    private String id;
    private String prop1;
    private String prop2;

    public Test() {
    }

    public Test(String id, String prop1, String prop2) {
        this.id = id;
        this.prop1 = prop1;
        this.prop2 = prop2;
    }

    public String getId() {
        return id;
    }

    public String getProp1() {
        return prop1;
    }

    public void setProp1(String prop1) {
        this.prop1 = prop1;
    }

    public String getProp2() {
        return prop2;
    }

    public void setProp2(String prop2) {
        this.prop2 = prop2;
    }
}
