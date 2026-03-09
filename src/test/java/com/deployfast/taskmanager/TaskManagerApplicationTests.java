package com.deployfast.taskmanager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class TaskManagerApplicationTests {

    @Test
    void contextLoads() {
        // Smoke test: ensures the Spring Context is correctly configured
    }

}
