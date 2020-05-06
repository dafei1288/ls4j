# 说明 

## 示例

```java

import cn.gbase.ls4j.core.Ssh;

public class TestSingle {
    public static void main(String[] args) throws Exception {
        Ssh ls = Ssh.getInstance()
                .buildRemoteHostIp("localhost")
                .buildRemoteHostPort(1022)
                .buildRemoteHostUser("root")
                .buildRemoteHostPasswd("dafei1288")
//                .printPwd()
//                .cd("1")
//                .printPwd()
//                .printList()
//                .cd("11")
//                .printPwd()
//                .printList()
                .cd("1/12")
                .upload("E:\\working\\GBase\\8s_远程目录操作\\ls4j\\src\\test\\")
                .cdHome()
                .printPwd()
                .cd("1")
                .cd("11")
                .printLs();
        ;

    }
}


```
Ssh.getInstance()
                
                创建连接必要信息
                
                .buildRemoteHostIp("localhost")
                .buildRemoteHostPort(1022)
                .buildRemoteHostUser("root")
                .buildRemoteHostPasswd("dafei1288")
                
                
                可以设置，但是非必须API，使用 change* 开始
                
                cd操作，可以连续.构建， .cd("1").cd("2").cd("3")   实际相当于 cd 1 & cd 2 & cd 3
                        cdHome 可以重新返回home路径
                
                终结操作 .pwd 现实当前路径
                        .upload(本地路径) ，将本地路径上传到服务器路径
                        .ls 列取当前路径下的所有文件夹
                        
//                .printPwd()
//                .cd("1")
//                .printPwd()
//                .printList()
//                .cd("11")
//                .printPwd()
//                .printList()
                                .cd("1/12")
                                .upload("E:\\working\\GBase\\8s_远程目录操作\\ls4j\\src\\test\\")
                                .cdHome()
                                .printPwd()
                                .cd("1")
                                .cd("11")
                                .printList();