package com.mrru.loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

/**
 * @className: LoadBalancer
 * @author: 茹某
 * @date: 2021/8/4 18:13
 **/
public interface LoadBalancer
{

    //从 Nacos 获取到的是所有提供这个服务的服务端信息列表，需要从中选择一个
    Instance select(List<Instance> instances);
}
