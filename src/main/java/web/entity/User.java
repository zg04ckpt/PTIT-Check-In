package web.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)//tạo ra id là 1 chuỗi bất kì
    private String id;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private LocalDate dob;

    public String getPassword() {
        return password;
    }

    public String getId() {
        return id;
    }

    public LocalDate getDob() {
        return dob;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

