package com.example.security.filter;

import com.example.security.security.IgnoreWhiteProperties;
import com.example.security.security.UserDetail;
import com.example.security.utils.JwtTokenUtil;
import io.jsonwebtoken.Claims;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author: smj
 * @Date: 2021/7/29 16:46
 * @Description:
 * 在HttpSecurity过滤之后，再执行的过滤器
 */
public class AuthenticationTokenFilter extends BasicAuthenticationFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationTokenFilter.class);

    private JwtTokenUtil jwtTokenUtil;

    private RedisTemplate redisTemplate;

    private IgnoreWhiteProperties ignoreWhiteProperties;

    public AuthenticationTokenFilter(AuthenticationManager authenticationManager,
                                     IgnoreWhiteProperties ignoreWhiteProperties,
                                     JwtTokenUtil jwtTokenUtil,
                                     RedisTemplate redisTemplate) {
        super(authenticationManager);
        this.ignoreWhiteProperties = ignoreWhiteProperties;
        this.jwtTokenUtil = jwtTokenUtil;
        this.redisTemplate = redisTemplate;
    }

    @SneakyThrows
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        System.out.println(request.getServletPath()+" ==============");
        System.out.println(request.getRequestURI());
        System.out.println(request.getRequestURL());

        //白名单请求直接放行
        List<String> whites = ignoreWhiteProperties.getWhites();
        if (whites != null && whites.contains(request.getRequestURI())){
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(null,null,null)
            );
            chain.doFilter(request, response);
            return;
        }

        String token = request.getHeader("token");

        //判断token是否过期
        boolean tokenExpired = jwtTokenUtil.isTokenExpired(token);
        if (tokenExpired){
            throw new RuntimeException("已过期");
        }

        //解析token
        Claims claims = jwtTokenUtil.getClaimsFromToken(token);
        String username = (String) claims.get("username");

        if (username == null){
            throw new RuntimeException("错误");
        }

        UserDetail loginUser = (UserDetail) redisTemplate.opsForValue().get("login:" + username);

        if (loginUser == null){
            //登陆过期，重新登陆
        }else {
            redisTemplate.expire("login:" + username,30, TimeUnit.SECONDS);
        }

        /**
         * 判断请求权限
         * 三个参数的构造，内部会设置已认证，否则会通过ProviderManager去认证
         * 两个参数的需要传入用户名和密码，像登陆流程一样
         */
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(loginUser.getUsername(), loginUser.getPassword(), loginUser.getAuthorities());

        //authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        chain.doFilter(request, response);
    }

}
