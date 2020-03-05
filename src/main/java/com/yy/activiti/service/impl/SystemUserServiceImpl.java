package com.yy.activiti.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yy.activiti.dao.SystemUserMapper;
import com.yy.activiti.model.entity.SystemUser;
import com.yy.activiti.service.ISystemUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author yy
 * @since 2019-12-20
 */
@Service
public class SystemUserServiceImpl extends ServiceImpl<SystemUserMapper, SystemUser> implements ISystemUserService {

    @Autowired
    SystemUserMapper systemUserMapper;

    @Override
    public SystemUser getByUsername(String username) {
        QueryWrapper<SystemUser> systemUserQueryWrapper = new QueryWrapper<>();
        systemUserQueryWrapper.eq("username", username);
        return systemUserMapper.selectOne(systemUserQueryWrapper);
    }
}
