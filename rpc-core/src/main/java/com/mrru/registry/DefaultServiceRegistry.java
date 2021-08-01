package com.mrru.registry;

import com.mrru.enums.RpcError;
import com.mrru.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @className: DefaultServiceRegistry
 * @author: 茹某
 * @date: 2021/8/1 15:02
 **/

//DefaultServiceRegistry类型对象， 通过serviceMap保存 接口类名称<-->其实现类对象，通过后面getService调用得到接口名称对应的实现类对象

public class DefaultServiceRegistry implements ServiceRegistry
{
    private static final Logger logger = LoggerFactory.getLogger(DefaultServiceRegistry.class);

    //保存的是key-value ，接口名称-实现类对象
    private final Map<String,Object> serviceMap = new ConcurrentHashMap<>();
    //保存的是 实现类名称
    private final Set<String> registeredService = ConcurrentHashMap.newKeySet();


    //注册服务信息  注册实现类, 比如参数service = HelloServiceImpl类型的helloService对象
    @Override
    public synchronized <T> void register(T service)
    {
        String serviceName = service.getClass().getCanonicalName();//获取该类的规范名称

        //使用一个 Set 来保存当前有哪些对象已经被注册
        if (registeredService.contains(serviceName)) return;
        registeredService.add(serviceName);

        //将服务名与提供服务的对象的对应关系保存在一个 ConcurrentHashMap 中
        Class<?>[] interfaces = service.getClass().getInterfaces();//获取该类所实现的所有接口
        if (interfaces.length == 0){
            throw new RpcException(RpcError.SERVICE_NOT_IMPLEMENT_ANY_INTERFACE);
        }

        //在注册服务时，默认采用这个对象实现的接口的完整类名作为服务名，例如某个对象 A 实现了接口 X 和 Y，
        // 那么将 A 注册进去后，会有两个服务名 X 和 Y 对应于 A 对象。
        // 这种处理方式也就说明了某个接口只能有一个对象提供服务。
        for (Class<?> i : interfaces)
        {
            serviceMap.put(i.getCanonicalName(), service);        //保存的是key-value ，接口名称-实现类对象
        }
        logger.info("向接口： {} 注册服务： {}", interfaces, serviceName);
    }

    //获取服务信息  通过接口名称 得到其保存的实现类，一个接口只能对应一个实现类对象
    @Override
    public Object getService(String serviceName)
    {
        Object service = serviceMap.get(serviceName);

        if (service == null){
            throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        }

        return service;
    }





















}
