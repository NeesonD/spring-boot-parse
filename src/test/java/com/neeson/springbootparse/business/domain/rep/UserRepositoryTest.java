package com.neeson.springbootparse.business.domain.rep;

import com.neeson.springbootparse.business.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

@Slf4j
@SpringBootTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void saveTest() {
        User user = new User();
        user.setId(1L);
        User result = userRepository.save(user);
        log.info(result.toString());
        Assert.assertNotNull(user.getId());
    }

    @Test
    public void findOneTest() {
        Optional<User> user = userRepository.findById(1L);
        log.info(user.toString());
        Assert.assertNotNull(user.get());
    }
}
