package com.mrru.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 消费者向提供者发送的请求对象 封装真正要发送的数据
 *
 * @className: RpcRequest  传输协议
 * @author: 茹某
 * @date: 2021/8/1 9:33
 **/
/*
    服务端需要这些信息，才能唯一确定服务端需要调用的接口的方法
    客户端调用时，还需要传递参数的实际值，那么服务端知道以上四个条件，
    就可以找到这个方法并且调用了。我们把这四个条件写到一个对象里，到时候传输时传输这个对象就行了
    后续的序列化反序列化也是针对 RpcRequest 类型的对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RpcRequest implements Serializable
{
    //请求号  UUID字符串形式
    private String requestId;

    //待调用接口名称
    private String interfaceName;

    //待调用方法名称
    private String methodName;

    //调用方法的参数
    private Object[] parameters;

    //调用方法的参数类型
    private Class<?>[] paramTypes;


}
