package com.mrru.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 字节流中标识所使用的序列化和反序列化器
 */
@AllArgsConstructor
@Getter
public enum SerializerCode {
    KRYO(0),
    JSON(1),
    HESSIAN(2),
    PROTOBUF(3);

    private final int code;

}