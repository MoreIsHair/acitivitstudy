package com.yy.activiti.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yy.activiti.model.entity.SystemUser;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author yy
 * @since 2019-12-20
 */
public interface ISystemUserService extends IService<SystemUser> {

    /**
     * 通过用户名查找用户
     * @param username
     * @return
     */
    SystemUser getByUsername(String username);

}
