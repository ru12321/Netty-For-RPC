package com.mrru.registry.nacos;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.mrru.enums.RpcError;
import com.mrru.exception.RpcException;
import com.mrru.loadbalancer.LoadBalancer;
import com.mrru.loadbalancer.RandomLoadBalancer;
import com.mrru.util.NacosUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * nacos服务发现实现类
 *
 * @className: NacosServiceDiscovery
 * @author: 茹某
 * @date: 2021/8/4 14:52
 **/
public class NacosServiceDiscovery implements ServiceDiscovery
{
    private static final Logger logger = LoggerFactory.getLogger(NacosServiceDiscovery.class);

    private final LoadBalancer loadBalancer;

    public NacosServiceDiscovery(LoadBalancer loadBalancer) {
        if(loadBalancer == null)
            this.loadBalancer = new RandomLoadBalancer();
        else
            this.loadBalancer = loadBalancer;
    }

    @Override
    public InetSocketAddress lookupService(String serviceName)
    {
        try {
            //NacosUtil工具类 去获得instance Instance是Nacos的类，表示一个具体的实例
            List<Instance> instances = NacosUtil.getAllInstance(serviceName);

            //校验一下 Nacos中没有服务
            if(instances.size() == 0) {
                logger.error("找不到对应的服务: " + serviceName);
                throw new RpcException(RpcError.SERVICE_NOT_FOUND);
            }

            //通过自定义的负载均衡策略选择哪个服务
            Instance instance = loadBalancer.select(instances);
            return new InetSocketAddress(instance.getIp(), instance.getPort());
        } catch (NacosException e) {
            logger.error("获取服务时有错误发生:", e);
        }
        return null;
    }
}
