package com.mrru.exception;

import com.mrru.enums.RpcError;

/**
 * @className: RpcException
 * @author: 茹某
 * @date: 2021/8/1 18:58
 **/
public class RpcException extends RuntimeException
{
    public RpcException(RpcError error, String detail) {
        super(error.getMessage() + ": " + detail);
    }
    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }
    public RpcException(RpcError error) {
        super(error.getMessage());
    }

}
