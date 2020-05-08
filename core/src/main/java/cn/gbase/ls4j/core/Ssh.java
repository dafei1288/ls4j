/**
 * @author Jack 'dafei1288' Li
 * @date 2020-5-1
 * */

package cn.gbase.ls4j.core;

import cn.gbase.ls4j.core.internal.IService;
import cn.gbase.ls4j.core.internal.JschService;
import cn.gbase.ls4j.core.internal.SshdService;

import java.io.IOException;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class Ssh {
    public static String PATHSPILTER = "/";
    private static Ssh ls;
    private RemoteHost remoteHost;
//    private String home_dir = "~/";
    private IService sshdService;
    private IService jschService;
    public final static String CURRENTPATH = "./";
    public final static String PARENTPATH = "../";
    public final static String BLANK = "";

    private boolean lockhome = true;
    private String home_dir =  "~/";
    private String placeholder = "@~@";
    private String dir = "ls -l "+placeholder+" |grep ^d | awk '{print $9}'";
    private String pwd =  "cd "+placeholder + " && pwd";
    private String mkdir = "mkdir -p "+placeholder;


    private String chartset = "utf-8";

    private Stack<String> dirStack;


    public RemoteHost getRemoteHost() {
        return remoteHost;
    }

//    public SshdService getSshdService() {
//        return sshdService;
//    }

    public boolean isLockhome() {
        return lockhome;
    }

    public String getHome_dir() {
        return home_dir;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public String getDir() {
        return dir;
    }

    public String getPwd() {
        return pwd;
    }

    public String getMkdir() {
        return mkdir;
    }

    public String getChartset() {
        return chartset;
    }

    public Stack<String> getDirStack() {
        return dirStack;
    }

    private Ssh(){
        remoteHost = new RemoteHost();
        sshdService = new SshdService();
        jschService = new JschService();
        dirStack = new Stack<String>();
    };


    /**
     *
     * 返回Ssh类实例，用于完成后续SSH操作 <br />
     *
     * build开头方法为构建必须使用的 <br />
     *
     * change方法为修改环境配置的，非必须配置方法 <br />
     *
     * <b>本类使用链式操作</b>
     *
     * cd操作，可以连续.构建， .cd("1").cd("2").cd("3")   实际相当于 cd 1 & cd 2 & cd 3
     *                         cdHome 可以重新返回home路径  <br />
     *
     * 终结操作 .pwd 现实当前路径
     *                         .upload(本地路径) ，将本地路径上传到服务器路径
     *                         .ls 列取当前路径下的所有文件夹
     *
     * @return
     *          Ssh
     *
     * */
    public static Ssh getInstance(){

        if(ls == null){
            ls = new Ssh();
        }
        ls.cdHome();
        return ls;
    }

    /**
     *
     * 构建远程主机IP
     *
     * @param host String 主机IP
     *
     * @return  Ssh
     *
     * */
    public Ssh buildRemoteHostIp(String host){
        this.remoteHost.setHost(host);
        return this;
    }

    /**
     *
     * 构建远程主机端口
     *
     * @param port int 主机端口
     *
     * @return  Ssh
     *
     * */
    public Ssh buildRemoteHostPort(int port){
        this.remoteHost.setPort(port);
        return this;
    }

    /**
     *
     * 构建远程主机用户名
     *
     * @param user String 远程主机用户名
     *
     * @return  Ssh
     *
     * */
    public Ssh buildRemoteHostUser(String user){
        this.remoteHost.setUser(user);
        return this;
    }


    /**
     *
     * 构建远程主机用户密码
     *
     * @param passwd String 远程主机用户密码
     *
     * @return  Ssh
     *
     * */
    public Ssh buildRemoteHostPasswd(String passwd){
        this.remoteHost.setPass(passwd);
        return this;
    }


    /**
     *
     * 将远程主机IP,端口,用户名,密码 封装到一个实例里，其他设置方法完成一样作用
     *
     * @param rh  RemoteHost 将远程主机IP,端口,用户名,密码 封装到一个实例里
     *
     * @return  Ssh
     *
     * */
    public Ssh buildRemoteHost(RemoteHost rh){
        this.remoteHost = rh;
        return this;
    }


    /**
     *
     * 连接终端使用字符集
     *
     * @param chartset  String 字符集
     *
     * @return  Ssh
     *
     * */
    public Ssh changeCharset(String chartset){
        this.chartset = chartset;
        return this;
    }



    /**
     *
     * 是否将用户锁定在自己的目录空间内
     *
     * @param lockhome  boolean
     *
     * @return  Ssh
     *
     * */
    public Ssh changeLockhome(boolean lockhome){
        this.lockhome = lockhome;
        return this;
    }

    public Ssh changePlaceholder(String placeholder){
        this.placeholder = placeholder;
        return this;
    }



    /**
     *
     * 设置用户的目录空间
     *
     * @param home_dir  String
     *
     * @return  Ssh
     *
     * */
    public Ssh changeHomeDir(String home_dir){
        this.home_dir = home_dir;
        return this;
    }


    /**
     *
     * 同 cd 命令
     *
     * @param dir  String
     *
     * @return  Ssh
     *
     * */
    public Ssh cd(String dir){
        this.dirStack.add(dir);
        return this;
    }

    public  Ssh cdHome(){
        this.dirStack.clear();
        return this;
    }



    /**
     *
     * 将本地路径（可以是文件、文件夹）上传到当前远程路径
     *
     * @param localpath  String  本地路径
     *
     * @return  Ssh
     *
     * */
    public Ssh upload(String localpath) throws Exception {
//        System.out.println(this.pwd());
//        System.out.println(localpath);
        ls.jschService.uploadSFTP(this.remoteHost,localpath,this.pwd());
        return this;
    }


    /**
     *
     * <b>终结操作</b>  获取当前远程路径
     *
     * @return  String 远程路径
     *
     * */
    public String pwd() {
        String path  =this.dirStack.stream().collect(Collectors.joining(PATHSPILTER));
        String res = path;

        if(this.isLockhome()){
            if(path.contains(PARENTPATH)){
                path = path.replace(PARENTPATH,BLANK);
            }

            if(path.contains(CURRENTPATH)){
                path = path.replace(CURRENTPATH,BLANK);
            }

            path = this.home_dir+path;
            try{
                String cli = this.pwd.replace(this.getPlaceholder(),path);
//            System.out.println("===》 "+cli);
                List<String> al = this.sshdService.lsFiles(this.remoteHost,cli,this.chartset);
//              res = this.jschService.pwd(remoteHost,path,this.chartset);

                if(al!=null  && al.size()>0){
                    res = al.get(0);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }



//        System.out.println("res ===》 "+res);
        return res;
    }


    /**
     *
     * <b>终结操作</b>  获取当前远程路径下所有文件夹名
     *
     * @return  List<String> 所有文件夹名
     *
     * */
    public List<String> ls() throws Exception {
        String path  = this.pwd();
//        String cli = this.getDir().replace(this.getPlaceholder(),path);
        return this.ls(path);
    }


    /**
     *
     * <b>终结操作</b>  获取参数路径下，所有文件夹名
     *
     * @param path String 远程路径
     *
     * @return  List<String> 所有文件夹名
     *
     * */
    public List<String> ls(String path) throws Exception {
        return ls.jschService.lsFiles(remoteHost,path,this.chartset);
    }


    public Ssh mkdir() throws Exception{
        return this.mkdir(this.pwd());
    }

    public Ssh mkdir(String path) throws Exception {

        String res = path;

        if(this.isLockhome()){
            if(path.contains(PARENTPATH)){
                path = path.replace(PARENTPATH,BLANK);
            }

            if(path.contains(CURRENTPATH)){
                path = path.replace(CURRENTPATH,BLANK);
            }

            path = this.home_dir+path;

        }

        try{
            String cli = this.mkdir.replace(this.getPlaceholder(),path);
//            System.out.println("===》 "+cli);
            List<String> al = this.sshdService.lsFiles(this.remoteHost,cli,this.chartset);
//              res = this.jschService.pwd(remoteHost,path,this.chartset);

            if(al!=null  && al.size()>0){
                res = al.get(0);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return this;
    }

    public Ssh rmdir(String remotepath) throws Exception {
        this.jschService.rmdir(this.remoteHost,remotepath,this.chartset);
        return this;
    }

    public Ssh printLs(){
        try {
            this.ls().stream().forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }

        return this;
    }

    public Ssh printPwd(){
        System.out.println(this.pwd());
        return this;
    }

}
