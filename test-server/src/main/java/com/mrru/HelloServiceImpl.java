package com.mrru;

import com.mrru.annotation.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 真正的接口的实现类
 *
 * @className: HelloServiceImpl
 * @author: 茹某
 * @date: 2021/8/1 9:10
 **/
@Service
public class HelloServiceImpl implements HelloService
{
    private static final Logger logger = LoggerFactory.getLogger(HelloServiceImpl.class);

    @Override
    public String hello(HelloObject object)
    {
        logger.info("接收到消息: {}", object.getMessage());
        return "真正的实现类，返回值id = " + object.getId();
    }
}
