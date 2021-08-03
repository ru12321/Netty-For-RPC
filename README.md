# Netty-For-RPC

---

实践项目-应用Netty框架完成RPC通信

## 关于对象序列化过程
假设客户端测试 使用的是 new HessianSerializer()

假设服务端测试 使用的是 new KryoSerializer()

仅仅意味着客户端和服务端**编码**时所采用的序列化器，对于**解码**，会根据传输协议传过来的序列化器的Code，创建对应的反序列化器进行解码！

