package com.mrru.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @className: PackageType
 * @author: 茹某
 * @date: 2021/8/2 8:31
 **/
@Getter
@AllArgsConstructor
public enum PackageType
{
    REQUEST_PACK(0),
    RESPONSE_PACK(1);

    private final int code;
}
