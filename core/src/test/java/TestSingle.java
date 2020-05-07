

import cn.gbase.ls4j.core.Ssh;
import cn.gbase.ls4j.core.internal.SshdService;
import cn.gbase.ls4j.core.RemoteHost;

import java.io.IOException;

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
                .cd("ff/gg/hh")
                .changeLockhome(false)
                .mkdir()
                .upload("E:\\working\\GBase\\8s_远程目录操作\\ls4j\\src\\test\\")
                .cdHome()
                .changeLockhome(true)
                .printPwd()
                .cd("1")
                .cd("11")
                .printLs();

        ;

    }
    public static void main1(String[] args) throws IOException, InterruptedException {
        RemoteHost rh = new RemoteHost();
        rh.setHost("localhost");
        rh.setPort(1022);
        rh.setUser("root");
        rh.setPass("dafei1288");


        SshdService ls = new SshdService();

        ls.lsFiles(rh,"ls -l ~/ |grep ^d | awk '{print $9}'","utf-8").stream().forEach(System.out::println);
    }
}
