package com.mrru.loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

/**
 * 轮转算法
 *
 * @className: RoundRobinLoadBalancer
 * @author: 茹某
 * @date: 2021/8/4 18:18
 **/
public class RoundRobinLoadBalancer implements LoadBalancer
{
    private int index = 0;

    @Override
    public Instance select(List<Instance> instances)
    {
        if (index >= instances.size()) {
            index %= instances.size();
        }
        return instances.get(index++);
    }
}
