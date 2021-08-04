package com.mrru.registry.nacos;

import com.mrru.enums.RpcError;
import com.mrru.exception.RpcException;
import com.mrru.util.NacosUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;

/**
 * Nacos注册实现类
 *
 * @className: NacosServiceRegistry
 * @author: 茹某
 * @date: 2021/8/4 10:32
 **/
public class NacosServiceRegistry implements ServiceRegistry
{
    private static final Logger logger = LoggerFactory.getLogger(NacosServiceRegistry.class);

    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress)
    {
        try {
            //NacosUtil工具类向 Nacos 注册服务
            NacosUtil.registerService(serviceName, inetSocketAddress);
        } catch (NacosException e) {
            logger.error("注册服务时有错误发生:", e);
            throw new RpcException(RpcError.REGISTER_SERVICE_FAILED);
        }
    }
}
