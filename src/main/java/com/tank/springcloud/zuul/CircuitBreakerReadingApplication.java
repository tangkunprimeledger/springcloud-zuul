package com.tank.springcloud.zuul;

import com.alibaba.fastjson.JSON;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.tank.springcloud.zuul.yaml.YamlEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author tangkun
 * @date 2019-11-10
 */
@EnableCircuitBreaker
@RestController
@SpringBootApplication
@Slf4j
public class CircuitBreakerReadingApplication {

    @Autowired
    private YamlEntity yamlEntity;

    public static void main(String[] args) {
        SpringApplication.run(CircuitBreakerReadingApplication.class, args);

    }

    /**
     * 当toRead抛出异常时，会调用fallbackMethod方法，fallbackMethod方法不支持参数类型，
     * 但返回参数必须兼容command的方法的返回参数
     * @return
     */
    @RequestMapping("to-read")
    @HystrixCommand(fallbackMethod = "defaultYaml")
    public String toRead(){
      log.info("to read is calling");
        error();
      return JSON.toJSONString(yamlEntity);
    }

    public void  error(){
        throw new RuntimeException();
    }
    public String defaultYaml(){
        return "{\"hello\":\"world\"}";
    }



}
