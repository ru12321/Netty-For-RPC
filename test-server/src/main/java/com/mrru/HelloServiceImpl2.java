package com.mrru;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 真正的接口的实现类
 *
 * @className: HelloServiceImpl
 * @author: 茹某
 * @date: 2021/8/1 9:10
 **/
public class HelloServiceImpl2 implements HelloService
{
    private static final Logger logger = LoggerFactory.getLogger(HelloServiceImpl2.class);

    @Override
    public String hello(HelloObject object)
    {
        logger.info("接收到消息: {}", object.getMessage());
        return "本次处理通过Netty，这是调用的返回值,id = " + object.getId();
    }
}
