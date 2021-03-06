package com.mrru.transport;

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
    int DEFAULT_SERIALIZER = CommonSerializer.KRYO_SERIALIZER;

    void start();

    //向 Nacos 注册服务
    <T> void publishService(T service, String serviceName);
}
