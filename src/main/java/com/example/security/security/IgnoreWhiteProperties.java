package com.example.security.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "ignore")
public class IgnoreWhiteProperties {

    private List<String> whites = new ArrayList<>();

}
