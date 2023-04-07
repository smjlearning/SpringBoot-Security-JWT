package com.example.security.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.security.domain.SysUser;

/**
 * <p>
 * 用户信息表 Mapper 接口
 * </p>
 */
public interface SysUserMapper extends BaseMapper<SysUser> {

    void updateUser(String name);
}
