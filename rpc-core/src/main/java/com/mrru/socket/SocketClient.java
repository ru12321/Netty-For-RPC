package com.mrru.socket;

import com.mrru.RpcClient;
import com.mrru.entity.RpcRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @className: RpcClient
 * @author: 茹某
 * @date: 2021/8/1 10:10
 **/
public class SocketClient implements RpcClient
{
    private static final Logger logger = LoggerFactory.getLogger(SocketClient.class);

    private final String host;

    private final int port;

    public SocketClient(String host,int port){
        this.host= host;
        this.port=port;
    }

    /*
    直接使用Java的序列化方式，传输对象 ObjectOutputStream 和 ObjectInputStream
    通过Socket传输。创建一个Socket，获取ObjectOutputStream对象，
    然后把需要发送的对象传进去即可，接收时获取ObjectInputStream对象，readObject()方法就可以获得一个返回的对象。
     */
    public Object sendRequest(RpcRequest rpcRequest){
        try(Socket socket = new Socket(host, port))
        {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream.writeObject(rpcRequest);
            objectOutputStream.flush();

            return objectInputStream.readObject();

        } catch (IOException | ClassNotFoundException e)
        {
            logger.error("调用时有错误发生",e);
            return null;
        }
    }




}
