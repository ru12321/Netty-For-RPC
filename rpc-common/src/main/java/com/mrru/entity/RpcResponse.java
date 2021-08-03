package com.mrru.entity;

import com.mrru.enums.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 提供者执行完成或出错后向消费者返回的结果对象
 *
 * @className: RpcResponse
 * @author: 茹某
 * @date: 2021/8/1 9:39
 **/
/*
    那么服务器调用完这个方法后，需要给客户端返回哪些信息呢？
    如果调用成功的话，显然需要返回值，
    如果调用失败了，就需要失败的信息，
    这里封装成一个RpcResponse对象：
    后续的序列化反序列化也是针对 RpcResponse 类型的对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RpcResponse<T> implements Serializable
{
    //响应状态码
    private Integer statusCode;

    //响应状态补充信息--失败时添加
    private String message;

    //响应数据--成功时添加
    private T data;

    //<T> 表示这是一个泛型方法 在修饰符和返回值之间
    //如果静态方法要使用泛型的话，必须将静态方法也定义成泛型方法 。
    public static <T> RpcResponse<T> success(T data)
    {
        RpcResponse<T> response = new RpcResponse<>();
        response.setStatusCode(ResponseCode.SUCCESS.getCode());
        response.setData(data);
        return response;
    }


    public static <T> RpcResponse<T> fail(ResponseCode code)
    {
        RpcResponse<T> response = new RpcResponse<>();
        response.setStatusCode(code.getCode());
        response.setMessage(code.getMessage());
        return response;
    }

}
