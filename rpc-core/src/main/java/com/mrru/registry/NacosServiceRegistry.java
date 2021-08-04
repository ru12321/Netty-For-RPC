package com.mrru.registry;

import com.mrru.enums.RpcError;
import com.mrru.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.InetSocketAddress;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import java.net.InetSocketAddress;
import java.util.List;

/**Nacos服务注册中心
 * @className: NacosServiceRegistry
 * @author: 茹某
 * @date: 2021/8/4 10:32
 **/
public class NacosServiceRegistry implements ServiceRegistry
{
    private static final Logger logger = LoggerFactory.getLogger(NacosServiceRegistry.class);

    private static final String SERVER_ADDR = "127.0.0.1:8848";
    private static final NamingService namingService;

    //类加载时自动连接nacos
    static {
        try {
            //过 NamingFactory 创建 NamingService 连接 Nacos
            namingService = NamingFactory.createNamingService(SERVER_ADDR);
        } catch (NacosException e) {
            logger.error("连接到Nacos时有错误发生: ", e);
            throw new RpcException(RpcError.FAILED_TO_CONNECT_TO_SERVICE_REGISTRY);
        }
    }

    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress)
    {
        try {
            //向 Nacos 注册服务
            namingService.registerInstance(serviceName, inetSocketAddress.getHostName(), inetSocketAddress.getPort());
        } catch (NacosException e) {
            logger.error("注册服务时有错误发生:", e);
            throw new RpcException(RpcError.REGISTER_SERVICE_FAILED);
        }
    }

    @Override
    public InetSocketAddress lookupService(String serviceName)
    {
        try {
            //获得提供某个服务的所有提供者的列表
            List<Instance> instances = namingService.getAllInstances(serviceName);
            //获取到某个服务的所有提供者列表后，需要选择一个，这里就涉及了负载均衡策略
            Instance instance = instances.get(0);
            return new InetSocketAddress(instance.getIp(), instance.getPort());
        } catch (NacosException e) {
            logger.error("获取服务时有错误发生:", e);
        }
        return null;
    }
}
