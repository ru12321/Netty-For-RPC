package com.mrru.registry.nacos;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.mrru.util.NacosUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * nacos发现实现类
 *
 * @className: NacosServiceDiscovery
 * @author: 茹某
 * @date: 2021/8/4 14:52
 **/
public class NacosServiceDiscovery implements ServiceDiscovery
{
    private static final Logger logger = LoggerFactory.getLogger(NacosServiceDiscovery.class);

    @Override
    public InetSocketAddress lookupService(String serviceName)
    {
        try {
            //NacosUtil工具类 去获得instance
            List<Instance> instances = NacosUtil.getAllInstance(serviceName);
            Instance instance = instances.get(0);
            return new InetSocketAddress(instance.getIp(), instance.getPort());
        } catch (NacosException e) {
            logger.error("获取服务时有错误发生:", e);
        }
        return null;
    }
}
