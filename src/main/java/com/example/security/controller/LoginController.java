package com.example.security.controller;

import com.example.security.common.constants.RedisConstants;
import com.example.security.common.domain.Result;
import com.example.security.domain.SysUser;
import com.example.security.utils.JwtTokenUtil;
import io.jsonwebtoken.Claims;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
public class LoginController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

//    @Autowired
//    private Producer producer;

    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping("/login")
    public Result<?> login(@RequestBody SysUser sysUser){

        //进行认证
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(sysUser.getUserName(), sysUser.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);

        if (authenticate == null){
            return Result.fail("500","用户名或密码错误");
        }

        //会返回UserDetails类型
        UserDetails loginUser = (UserDetails) authenticate.getPrincipal();

        //保存信息到redis
        redisTemplate.opsForValue().set("login:" + loginUser.getUsername(),loginUser,30, TimeUnit.MINUTES);

        //生成JWT
        String token = jwtTokenUtil.generateToken(loginUser);

        return Result.success(token);
    }

    /**
     * 注册
     * @param sysUser
     * @return
     */
    @PostMapping("/register")
    public Result<?> register(@RequestBody SysUser sysUser){

        //加密密码
        String password = sysUser.getPassword();
        if (StringUtils.isEmpty(password)){
            password = "123456";
        }

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        sysUser.setPassword(bCryptPasswordEncoder.encode(password));

        return Result.success();
    }

    public static void main(String[] args) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        System.out.println(bCryptPasswordEncoder.encode("123456"));
    }

    /**
     * 登出
     * @param userAccount
     * @return
     */
    @ApiOperation("退出")
    @PostMapping("/logout")
    public Result<?> logout(@RequestHeader("token") String token, @RequestAttribute("userAccount") String userAccount){

        Claims claimsFromToken = JwtTokenUtil.getClaimsFromToken(token);
        Long createdTime = (Long) claimsFromToken.get("created");

        //删除redis中用户缓存信息
        String key = RedisConstants.REDIS_KEY_USER + ":" + RedisConstants.REDIS_KEY_PERMISSION + ":" + userAccount + "-" + createdTime;
        redisTemplate.delete(key);

        return Result.success();
    }

//    @ApiOperation(value = "获取验证码", notes = "")
//    @RequestMapping(path = "/imageCode", method = RequestMethod.GET)
//    public void getImageCode(HttpServletResponse response) {
//        // 生成验证码
//        String text = producer.createText();
//        System.out.println(text);
//        BufferedImage image = producer.createImage(text);
//        String BASE_CHECK_CODES = "qwertyuiplkjhgfdsazxcvbnmQWERTYUPLKJHGFDSAZXCVBNM1234567890";
//
//        // 验证码的归属
//        // 13位时间戳
//        long now = System.currentTimeMillis();
//        //获取7位随机数
//        StringBuilder builder = new StringBuilder();
//        for (int i = 0; i < 7; i++) {
//            int index = (int)(Math.random() * BASE_CHECK_CODES.length());
//            builder.append(BASE_CHECK_CODES.charAt(index));
//        }
//        String kaptchaOwner = now + "" + builder.toString();
//
//        // 向客户端颁发临时凭证，用于获取服务器对应的验证码
//        Cookie cookie = new Cookie("kaptchaOwner", kaptchaOwner);
//        cookie.setMaxAge(60);
//        //cookie.setPath("contextPath");
//        response.addCookie(cookie);
//
//        // 将验证码存入Redis
//        //String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
//        // 60秒后失效
//        //redisTemplate.opsForValue().set(redisKey, text, Constants.IMAGE_CODE_ALIVE_SECONDS, TimeUnit.SECONDS);
//
//        // 将图片输出给浏览器
//        response.setContentType("image/png");
//        try {
//            OutputStream os = response.getOutputStream();
//            // 向浏览器输出图片
//            ImageIO.write(image, "png", os);
//        } catch (IOException e) {
//            log.error("/imageCode occur Exception: {}", e.getMessage(), e);
//        }
//    }

}
