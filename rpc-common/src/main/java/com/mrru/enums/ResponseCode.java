package com.mrru.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 方法调用的响应状态码
 * @className: ResponseCode
 * @author: 茹某
 * @date: 2021/8/1 9:52
 **/
@Getter
@AllArgsConstructor
public enum ResponseCode
{
    SUCCESS(200,"调用方法成功"),
    FAIL(500,"调用方法失败"),
    METHOD_NOT_FOUND(500, "未找到指定方法"),
    CLASS_NOT_FOUND(500, "未找到指定类");


    private final int code;
    private final String message;
}
