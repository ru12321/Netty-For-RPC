package com.mrru.registry;

import java.net.InetSocketAddress;

/**
 * 服务注册中心通用接口
 *
 * @className: ServiceRegistry
 * @author: 茹某
 * @date: 2021/8/4 10:30
 **/
public interface ServiceRegistry
{
    /**
     * 将一个服务注册进注册表
     *
     * @param serviceName       服务名称
     * @param inetSocketAddress 提供服务的地址
     */
    void register(String serviceName, InetSocketAddress inetSocketAddress);

    /**
     * 根据服务名称查找服务实体
     *
     * @param serviceName serviceName 服务名称
     * @return 服务地址
     */
    InetSocketAddress lookupService(String serviceName);


}
