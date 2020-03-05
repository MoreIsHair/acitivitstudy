package com.yy.activiti.controller.business;


import com.yy.activiti.model.entity.SystemUser;
import com.yy.activiti.service.ISystemUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author yy
 * @since 2019-12-20
 */
@RestController
@RequestMapping("/user")
public class SystemUserController {

    @Autowired
    ISystemUserService systemUserService;

    @PostMapping("/add")
    public void add(@RequestBody SystemUser systemUser){
        systemUserService.save(systemUser);
    }


}

