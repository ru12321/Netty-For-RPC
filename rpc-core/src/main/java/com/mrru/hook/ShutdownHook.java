package com.mrru.hook;

import com.mrru.factory.ThreadPoolFactory;
import com.mrru.util.NacosUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 注销所有nacos实例的钩子函数
 *
 * @className: ShutdownHook
 * @author: 茹某
 * @date: 2021/8/4 16:01
 **/
public class ShutdownHook
{

    private static final Logger logger = LoggerFactory.getLogger(ShutdownHook.class);

    private static final ShutdownHook shutdownHook = new ShutdownHook();

    public static ShutdownHook getShutdownHook()
    {
        return shutdownHook;
    }

    public void addClearAllHook()
    {
        logger.info("关闭后将自动注销所有服务");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            //注销所有nacos实例
            NacosUtil.clearRegistry();
            //关闭线程池
            ThreadPoolFactory.shutDownAll();
        }));
    }

}