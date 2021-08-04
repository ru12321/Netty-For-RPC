package com.mrru.registry.nacos;

import java.net.InetSocketAddress;

/**
 * 服务注册接口
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

}
