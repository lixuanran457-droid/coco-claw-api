package com.cococlown.cococlawservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@MapperScan("com.cococlown.cococlawservice.mapper")
@EnableCaching
public class CocoClawApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(CocoClawApiApplication.class, args);
        System.out.println("===============================================");
        System.out.println("  COCO-CLAW API 服务已启动");
        System.out.println("  访问地址: http://localhost:8080");
        System.out.println("  Swagger文档: http://localhost:8080/swagger-ui.html");
        System.out.println("===============================================");
    }
}
