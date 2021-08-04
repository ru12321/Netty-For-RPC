package com.mrru;

import com.mrru.serializer.CommonSerializer;

/**
 * 服务器类通用接口
 *
 * @className: RpcServer
 * @author: 茹某
 * @date: 2021/8/1 20:23
 **/
public interface RpcServer
{
    void start();

    void setSerializer(CommonSerializer serializer);

    //向 Nacos 注册服务
    <T> void publishService(T service, Class<T> serviceClass);
}
