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

    public void scanServices() {
        //通过方法栈，获取到 main方法 所在的类
        String mainClassName = ReflectUtil.getStackTrace();

        Class<?> startClass;
        try {
            startClass = Class.forName(mainClassName);
            //校验main方法的类 是不是 ServiceScan类
            if(!startClass.isAnnotationPresent(ServiceScan.class)) {
                logger.error("启动类缺少 @ServiceScan 注解");
                throw new RpcException(RpcError.SERVICE_SCAN_PACKAGE_NOT_FOUND);
            }
        } catch (ClassNotFoundException e) {
            logger.error("出现未知错误");
            throw new RpcException(RpcError.UNKNOWN_ERROR);
        }
        //获取到 ServiceScan 注解的值，得到根包名称
        String basePackage = startClass.getAnnotation(ServiceScan.class).value();

        if("".equals(basePackage)) {
            basePackage = mainClassName.substring(0, mainClassName.lastIndexOf("."));
        }

        // 获取到根包下的所有的 Class
        Set<Class<?>> classSet = ReflectUtil.getClasses(basePackage);
        //逐个判断是否有 Service 注解
        for(Class<?> clazz : classSet) {
            //如果有的话
            if(clazz.isAnnotationPresent(Service.class)) {
                String serviceName = clazz.getAnnotation(Service.class).name();
                Object obj;
                try {
                    //通过反射创建该对象
                    obj = clazz.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    logger.error("创建 " + clazz + " 时有错误发生");
                    continue;
                }
                if("".equals(serviceName)) {
                    Class<?>[] interfaces = clazz.getInterfaces();
                    for (Class<?> oneInterface: interfaces){
                        publishService(obj, oneInterface.getCanonicalName());
                    }
                } else {
                    //调用 publishService 注册即可
                    publishService(obj, serviceName);
                }
            }
        }
    }


    @Override
    public <T> void publishService(T service, String serviceName) {
        //本地注册
        serviceProvider.addServiceProvider(service, serviceName);
        //nacos注册
        serviceRegistry.register(serviceName, new InetSocketAddress(host, port));
    }
}
