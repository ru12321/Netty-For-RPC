package com.mrru.loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;
import java.util.Random;

/**
 * 随机算法
 *
 * @className: RandomLoadBalancer
 * @author: 茹某
 * @date: 2021/8/4 18:16
 **/
public class RandomLoadBalancer implements LoadBalancer
{

    @Override
    public Instance select(List<Instance> instances)
    {
        //[0,5) : 0 1 2 3 4
        return instances.get(new Random().nextInt(instances.size()));
    }
}
