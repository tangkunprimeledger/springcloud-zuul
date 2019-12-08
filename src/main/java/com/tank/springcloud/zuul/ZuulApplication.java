package com.tank.springcloud.zuul;

import com.alibaba.fastjson.JSON;
import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.tank.productclient.client.ProductClient;
import com.tank.springcloud.zuul.yaml.YamlEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@RestController
@EnableEurekaClient
//启用zuul
//@EnableZuulProxy
@EnableCircuitBreaker
@DefaultProperties(defaultFallback = "defaultShow")
@ComponentScan(basePackages = "com.tank")
@SpringBootApplication public class ZuulApplication {

    @Autowired
    private YamlEntity yamlEntity;

    @Autowired
    private ProductClient productClient;

    public static void main(String[] args) {
        SpringApplication.run(ZuulApplication.class, args);

    }

    /**
     * 更多HystrixProperty设置参考 HystrixCommandProperties
     * circuitBreaker.enabled:熔断开关
     * circuitBreaker.requestVolumeThreshold
     * circuitBreaker.sleepWindowInMilliseconds
     * circuitBreaker.errorThresholdPercentage
     * @return
     *
     */
    @GetMapping("/showYaml")
    //@HystrixCommand(fallbackMethod = "defaultShow")
    @HystrixCommand
    public String showYaml(@RequestParam int num){

        if(num % 2 == 0){
        log.info("str:{}", yamlEntity.getSpecialStr());
        log.info("show yamlEntity:{}", yamlEntity);
        return JSON.toJSONString(yamlEntity);
        }else {
            log.error("失败");
            throw new RuntimeException("熔断");
        }
    }

    public String defaultShow(){
        log.info("default show.........");
        return "{\"default\":\"hello\"}";
    }

    @ConfigurationProperties("zuul")
    @RefreshScope
    public ZuulProperties zuulProperties(){
        return new ZuulProperties();
    }

    @RequestMapping(method = RequestMethod.GET,value = "/sayHelloWithFeignHystrix")
    public String sayHelloWithFeignHystrix(){
        log.info("get is called:{}");
        return productClient.getMsg();
    }
}
