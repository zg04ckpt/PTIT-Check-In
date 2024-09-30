package app.services.impl;

import app.dtos.TestDTO;
import app.models.Test;
import app.repositories.TestRepository;
import app.services.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestServiceImpl implements TestService {
    private TestRepository repository;

    @Autowired
    public TestServiceImpl(TestRepository repository) {
        this.repository = repository;
    }

    @Override
    public Test phuongThuc1(TestDTO dto) {
        //định nghĩa hành động của phương thức này
        return null;
    }
}
