package app.services.impl;

import app.dtos.CreateTestDTO;
import app.models.Test;
import app.repositories.TestRepository;
import app.services.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

//Đinh nghĩa chi tiết các phương thuc cua service
@Service
public class TestServiceImpl implements TestService {
    private TestRepository repository;
    //thêm bất kì repository nào

    @Autowired
    public TestServiceImpl(TestRepository repository) {
        this.repository = repository;
    }


    @Override
    public boolean phuongThucTest(String test) {
        try {
            //xử lý
            return true;
        } catch (Exception exception) {
            return false;
        }
    }
}
