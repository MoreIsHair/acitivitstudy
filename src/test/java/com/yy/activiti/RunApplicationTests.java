package com.yy.activiti;

import cn.hutool.core.lang.Console;
import com.yy.activiti.service.ISystemUserService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {RunApplicationTests.class})
@ComponentScan(basePackages = {"com.yy"})
@MapperScan(value = {"com.yy.activiti.dao"})
public class RunApplicationTests {


    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    ISystemUserService systemUserService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    public void contextLoads() {
        System.out.println("=========================================================");
        System.out.println(passwordEncoder.encode("123"));
    }

    @Test
    public void deploySelectTest(){
        List<Deployment> deployments = repositoryService.createDeploymentQuery().list();
        deployments.stream().forEach(e-> Console.log(e));
    }

    @Test
    public void  systemUserServiceTest(){
       // SystemUser build = SystemUser.builder().description("123").id(1234).password("123").name("123").sex(false).username("123").build();
      //  systemUserService.save(build);
    }


}
