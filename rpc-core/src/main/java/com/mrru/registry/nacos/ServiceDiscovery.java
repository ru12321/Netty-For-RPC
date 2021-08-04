package com.mrru.registry.nacos;

import java.net.InetSocketAddress;

/**
 * 服务发现接口
 *
 * @className: ServiceDiscovery
 * @author: 茹某
 * @date: 2021/8/4 14:49
 **/
public interface ServiceDiscovery
{
    /**
     * 根据服务名称查找服务实体
     *
     * @param serviceName 服务名称
     * @return 服务实体
     */
    InetSocketAddress lookupService(String serviceName);
}
