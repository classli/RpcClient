# RpcClient

### 简易 rpc demo Android客户端实例

运行方式

Android studio 打开工程直接运行安装apk，输入服务端地址即可连接测试。

客户端需要依赖服务端提供的api jar包，进行远程接口调用。

其中的protostuff-runtime jar包，由于序列化和反序列化过程中服务端和客户端字段顺序的不同，会导致服务端deCode失败。可以改写RuntimeSchema下的fill方法，通过treeMap将类中的各个字段进行排序，使得服务端和客户端字段顺序相同。然后客户端和服务端都采用这个改写后的protostuff-runtime jar包进行序列化即可。

另一种方案可以通过标注的方式，对服务端提供的java类中的各个字段进行标记处理，如标明该字段的顺序及类型，序列化和反序列化时按照标注的内容进行处理即可。

[服务端demo地址](https://github.com/classli/RpcServer)