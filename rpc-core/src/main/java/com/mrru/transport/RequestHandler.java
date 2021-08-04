package com.mrru.transport;

import com.mrru.entity.RpcRequest;
import com.mrru.entity.RpcResponse;
import com.mrru.enums.ResponseCode;
import com.mrru.registry.local.ServiceProvider;
import com.mrru.registry.local.ServiceProviderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * 服务端 进行过程调用的处理器，动态代理真正的反射invoke调用 在RequestHandlerThread中被调用
 *
 * @className: RequestHandler
 * @author: 茹某
 * @date: 2021/8/1 18:36
 **/
public class RequestHandler
{
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private static final ServiceProvider serviceProvider;

    static{
        serviceProvider = new ServiceProviderImpl();
    }

    public Object handle(RpcRequest rpcRequest)
    {
        //从本地注册中 获得 实现类对象
        Object service = serviceProvider.getServiceProvider(rpcRequest.getInterfaceName());
        //调用实现类的方法，并获得返回结果
        return invokeTargetMethod(rpcRequest, service);
    }

    private Object invokeTargetMethod(RpcRequest rpcRequest, Object service)
    {
        Object result;
        try {
            //根据包装的rpcRequest对象 获得到 实现类的 要调用的方法
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            result = method.invoke(service, rpcRequest.getParameters());
            logger.info("服务:{} 成功调用方法:{}", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            return RpcResponse.fail(ResponseCode.METHOD_NOT_FOUND, rpcRequest.getRequestId());
        }
        //通过反射执行实现类对象的 参数为包装的rpcRequest对象中带得参数  的 method方法
        return result;
    }


}
