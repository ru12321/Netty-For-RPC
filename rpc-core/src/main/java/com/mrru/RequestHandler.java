package com.mrru;

import com.mrru.entity.RpcRequest;
import com.mrru.entity.RpcResponse;
import com.mrru.enums.ResponseCode;
import com.mrru.registry.ServiceProvider;
import com.mrru.registry.ServiceProviderImpl;
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
        Object result = null;
        //从本地注册中 获得 实现类对象
        Object service = serviceProvider.getServiceProvider(rpcRequest.getInterfaceName());

        try {
            result = invokeTargetMethod(rpcRequest, service);
            logger.info("服务：{} 成功调用方法：{}", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());

        } catch (IllegalAccessException | InvocationTargetException e) {
            logger.error("调用或发送时有错误发生： ", e);
        }
        return result;
    }

    private Object invokeTargetMethod(RpcRequest rpcRequest, Object service) throws InvocationTargetException, IllegalAccessException
    {
        Method method;
        try {
            //根据包装的rpcRequest对象 获得到 实现类的 要调用的方法
            method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());

        } catch (NoSuchMethodException e) {
            return RpcResponse.fail(ResponseCode.METHOD_NOT_FOUND, rpcRequest.getRequestId());
        }
        //通过反射执行实现类对象的 参数为包装的rpcRequest对象中带得参数  的 method方法
        return method.invoke(service, rpcRequest.getParameters());
    }


}
