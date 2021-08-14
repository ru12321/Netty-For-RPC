package com.mrru.transport;

import com.mrru.annotation.Service;
import com.mrru.annotation.ServiceScan;
import com.mrru.enums.RpcError;
import com.mrru.exception.RpcException;
import com.mrru.registry.local.ServiceProvider;
import com.mrru.registry.nacos.ServiceRegistry;
import com.mrru.util.ReflectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Set;

/**
 * 服务注册中间调用类
 *
 * @className: AbstractRpcServer
 * @author: 茹某
 * @date: 2021/8/5 16:28
 **/
public abstract class AbstractRpcServer implements RpcServer
{

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    protected String host;
    protected int port;

    protected ServiceRegistry serviceRegistry;
    protected ServiceProvider serviceProvider;

    public void scanServices()
    {
        //通过方法栈，获取到 main方法 所在的类
        String mainClassName = ReflectUtil.getStackTrace();

        Class<?> startClass;
        try {
            startClass = Class.forName(mainClassName);
            //校验main方法的类 如果不是 ServiceScan类
            if (!startClass.isAnnotationPresent(ServiceScan.class)) {
                logger.error("启动类缺少 @ServiceScan 注解");
                throw new RpcException(RpcError.SERVICE_SCAN_PACKAGE_NOT_FOUND);
            }
        } catch (ClassNotFoundException e) {
            logger.error("出现未知错误");
            throw new RpcException(RpcError.UNKNOWN_ERROR);
        }
        //校验main方法的类 如果是ServiceScan类
        //获取到 ServiceScan 注解的值，得到根包名称
        String basePackage = startClass.getAnnotation(ServiceScan.class).value();

        if ("".equals(basePackage)) {
            basePackage = mainClassName.substring(0, mainClassName.lastIndexOf("."));//test-server/com.mrru
        }

        // 获取到根包下的所有的 Class
        Set<Class<?>> classSet = ReflectUtil.getClasses(basePackage);
        //逐个判断是否有 Service 注解
        for (Class<?> clazz : classSet) {
            //如果有的话
            if (clazz.isAnnotationPresent(Service.class)) {
                String serviceName = clazz.getAnnotation(Service.class).name();//""
                Object obj;
                try {
                    //通过反射创建该对象
                    obj = clazz.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    logger.error("创建 " + clazz + " 时有错误发生");
                    continue;
                }
                //走到这 说明 main类所在包下的类 有@Service注解，并且注解是默认值
                if ("".equals(serviceName)) {
                    Class<?>[] interfaces = clazz.getInterfaces();//接口数组 {HelloService}
                    for (Class<?> oneInterface : interfaces) {//接口 HelloService
                        publishService(obj, oneInterface.getCanonicalName());//new HelloService, rpc-api/com.mrru.HelloService
                    }
                }
            }
        }
    }


    @Override
    public <T> void publishService(T service, String serviceName)
    {
        //本地注册
        serviceProvider.addServiceProvider(service, serviceName);
        //nacos注册
        serviceRegistry.register(serviceName, new InetSocketAddress(host, port));
    }
}
