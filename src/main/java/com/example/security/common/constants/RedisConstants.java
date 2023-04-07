package com.example.security.common.constants;

/**
 * Redis常量
 */
public class RedisConstants {

    /**
     * redis中根名称
     */
    public static final String REDIS_DATABASE = "TES";

    /**
     * 用户信息过期时间
     */
    public static final Long REDIS_EXPIRE = 360L;

    /**
     * 用户信息的key
     */
    public static final String REDIS_KEY_USER = "user";

    /**
     * 用户的权限信息
     */
    public static final String REDIS_KEY_PERMISSION = "permission";

    /**
     * 用户权限过期时间
     */
    public static final Long REDIS_KEY_EXPIRATION = 30 * 60L;

}
