package com.mrru.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @className: RpcError
 * @author: 茹某
 * @date: 2021/8/1 18:53
 **/
@Getter
@AllArgsConstructor
public enum RpcError
{
    SERVICE_INVOCATION_FAILURE("服务调用出现失败"),
    SERVICE_CAN_NOT_BE_NULL("注册的服务不得为空"),
    SERVICE_NOT_FOUND("找不到对应的服务"),
    SERVICE_NOT_IMPLEMENT_ANY_INTERFACE("注册的服务未实现接口");

    private final String message;

}
