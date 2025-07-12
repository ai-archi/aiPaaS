package com.aixone.directory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.aixone.permission.annotation.EnablePermission;

@SpringBootApplication
@EnablePermission
public class DirectoryServeApplication {

    public static void main(String[] args) {
        SpringApplication.run(DirectoryServeApplication.class, args);
    }

} 