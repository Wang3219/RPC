# 自定义RPC框架
## 💡 项目简介

仓库: [https://github.com/Wang3219/RPC](https://github.com/Wang3219/RPC)

项目完成了自定义RPC框架

## 🚀 功能介绍

- **服务注册**

- **服务发现**

- **负载均衡**

- **心跳机制**

- **动态代理**

- **序列化**

- **压缩**

## 技术栈
+ Jdk (1.8)
+ Netty (4.1.42.Final)
+ ZooKeeper (3.5.8)
+ Curator (4.2.0)
+ Kryo (4.0.2)

## 运行
安装并启动ZooKeeper后，修改CuratorUtils中DEFAULT_ZOOKEEPER_ADDRESS为ZooKeeper安装ip地址。
先启动rpc-server中ServerMain，然后启动rpc-client中ClientMain即可