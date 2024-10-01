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

    @Autowired
    public TestServiceImpl(TestRepository repository) {
        this.repository = repository;
    }

    @Override
    public Test phuongThuc1(CreateTestDTO dto) {
        try{
            //định nghĩa hành động của phương thức này
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean createNewTest(CreateTestDTO dto) {
        try {
            if(repository.findByProp1(dto.prop1) != null) {
                return false;
            }
            Test test = new Test(
                    UUID.randomUUID().toString(),
                    dto.prop1,
                    dto.prop2
            );
            repository.save(test);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
