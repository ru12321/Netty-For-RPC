package com.mrru.util;

import com.mrru.entity.RpcRequest;
import com.mrru.entity.RpcResponse;
import com.mrru.enums.ResponseCode;
import com.mrru.enums.RpcError;
import com.mrru.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 检查响应与请求
 *
 * @className: RpcMessageChecker
 * @author: 茹某
 * @date: 2021/8/3 16:57
 **/
public class RpcMessageChecker
{
    private static final Logger logger = LoggerFactory.getLogger(RpcMessageChecker.class);

    public static final String INTERFACE_NAME = "interfaceName";

    private RpcMessageChecker(){}

    public static void check(RpcRequest rpcRequest, RpcResponse rpcResponse){
        if (rpcResponse == null){
            logger.error("调用服务失败,serviceName:{}", rpcRequest.getInterfaceName());
            throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }
        //比较发出去的请求号，和返回的响应请求号 是否相等
        if (!rpcRequest.getRequestId().equals(rpcResponse.getRequestId())) {
            throw new RpcException(RpcError.RESPONSE_NOT_MATCH, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }

        //返回状态码 校验
        if (rpcResponse.getStatusCode() == null || !rpcResponse.getStatusCode().equals(ResponseCode.SUCCESS.getCode())) {
            logger.error("调用服务失败,serviceName:{},RpcResponse:{}", rpcRequest.getInterfaceName(), rpcResponse);
            throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }
    }



}
