package com.mrru.exception;

/**
 * 序列化过程中的异常，自己传入对错误的解释msg
 *
 * @className: SerializeException
 * @author: 茹某
 * @date: 2021/8/2 15:33
 **/
public class SerializeException extends RuntimeException
{
    public SerializeException(String msg)
    {
        super(msg);
    }

}
