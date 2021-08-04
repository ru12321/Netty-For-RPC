package com.mrru.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * RPC调用过程中的错误
 * @className: RpcError
 * @author: 茹某
 * @date: 2021/8/1 18:53
 **/
@Getter
@AllArgsConstructor
public enum RpcError
{
    //客户端连接错误
    CLIENT_CONNECT_SERVER_FAILURE("客户端连接服务端失败"),

    //服务注册调用错误
    SERVICE_INVOCATION_FAILURE("服务调用出现失败"),
    SERVICE_CAN_NOT_BE_NULL("注册的服务不得为空"),
    SERVICE_NOT_FOUND("找不到对应的服务"),
    SERVICE_NOT_IMPLEMENT_ANY_INTERFACE("注册的服务未实现接口"),

    //传输协议错误
    UNKNOWN_PROTOCOL("不识别的协议包"),
    UNKNOWN_SERIALIZER("不识别的(反)序列化器"),
    UNKNOWN_PACKAGE_TYPE("不识别的数据包类型"),

    //序列化相关错误
    SERIALIZER_NOT_FOUND("找不到序列化器"),
    RESPONSE_NOT_MATCH("响应与请求号不匹配"),

    //注册相关错误
    FAILED_TO_CONNECT_TO_SERVICE_REGISTRY("连接注册中心失败"),
    REGISTER_SERVICE_FAILED("注册服务失败");
    private final String message;

}
