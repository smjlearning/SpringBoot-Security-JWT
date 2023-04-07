package com.example.security.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.security.domain.SysUser;
import com.example.security.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 实现UserDetailsService接口
 * 进行校验用户
 * 校验成功返回UserDetails对象
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {


    @Autowired
    private SysUserService sysUserService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //查询数据库

        SysUser user = sysUserService.getOne(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUserName, username)
        );

        if (user == null){
            throw new RuntimeException("用户名或密码错误~");
        }

        //权限信息
        List<String> authorities = new ArrayList<>();
        authorities.add("/test");
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();

        for (String authority : authorities) {
            SimpleAuthority simpleGrantedAuthority = new SimpleAuthority(authority);
            grantedAuthorities.add(simpleGrantedAuthority);
        }
//        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
//
//        for (String authority : authorities) {
//            SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(authority);
//            grantedAuthorities.add(simpleGrantedAuthority);
//        }

        return new UserDetail(username,user.getPassword(),grantedAuthorities);
    }


}
