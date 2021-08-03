package com.mrru;

import com.mrru.entity.RpcRequest;
import com.mrru.serializer.CommonSerializer;

/**
 * 客户端类通用接口
 *
 * @className: RpcClient
 * @author: 茹某
 * @date: 2021/8/1 20:23
 **/
public interface RpcClient
{
    Object sendRequest(RpcRequest rpcRequest);

    void setSerializer(CommonSerializer serializer);
}
