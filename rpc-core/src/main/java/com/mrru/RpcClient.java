package com.mrru;

import com.mrru.entity.RpcRequest;

/**
 * @className: RpcClient
 * @author: 茹某
 * @date: 2021/8/1 20:23
 **/
public interface RpcClient
{
    Object sendRequest(RpcRequest rpcRequest);
}
