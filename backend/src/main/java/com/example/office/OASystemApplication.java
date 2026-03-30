package com.example.office;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.office.mapper")
@org.springframework.cache.annotation.EnableCaching
public class OASystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(OASystemApplication.class, args);
        System.out.println("---------------------------------------------------------");
        System.out.println("--                                                     --");
        System.out.println("--                 ☆在线办公系统启动成功☆                 --");
        System.out.println("--                                                     --");
        System.out.println("---------------------------------------------------------");
    }
}
