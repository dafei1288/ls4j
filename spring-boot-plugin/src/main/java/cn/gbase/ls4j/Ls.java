package cn.gbase.ls4j;

import cn.gbase.ls4j.domain.RemoteHost;
import cn.gbase.ls4j.service.LsService;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class Ls {
    private static Ls ls;
    private RemoteHost remoteHost;
//    private String home_dir = "~/";
    private LsService lsService;

    public final static String CURRENTPATH = "./";
    public final static String PARENTPATH = "../";
    public final static String BLANK = "";

    private boolean lockhome = true;
    private String home_dir =  "~/";
    private String placeholder = "@~@";
    private String dir = "ls -l "+placeholder+" |grep ^d | awk '{print $9}'";
    private String pwd =  "pwd "+placeholder;
    private String mkdir = "mkdir -p "+placeholder;


    private String chartset = "utf-8";

    private Stack<String> dirStack;


    public RemoteHost getRemoteHost() {
        return remoteHost;
    }

    public LsService getLsService() {
        return lsService;
    }

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

    private Ls(){
        remoteHost = new RemoteHost();
        lsService = new LsService();
        dirStack = new Stack<String>();
    };

    public static Ls getInstance(){
        if(ls == null){
            ls = new Ls();
        }
        return ls;
    }

    public Ls buildRemoteHostIp(String host){
        this.remoteHost.setHost(host);
        return this;
    }

    public Ls buildRemoteHostPort(int port){
        this.remoteHost.setPort(port);
        return this;
    }

    public Ls buildRemoteHostUser(String user){
        this.remoteHost.setUser(user);
        return this;
    }

    public Ls buildRemoteHostPasswd(String passwd){
        this.remoteHost.setPass(passwd);
        return this;
    }

    public Ls buildRemoteHost(RemoteHost rh){
        this.remoteHost = rh;
        return this;
    }

    public Ls changeCharset(String chartset){
        this.chartset = chartset;
        return this;
    }

    public Ls changeLockhome(boolean lockhome){
        this.lockhome = lockhome;
        return this;
    }

    public Ls changePlaceholder(String placeholder){
        this.placeholder = placeholder;
        return this;
    }

    public Ls cd(String dir){
        this.dirStack.add(dir);
        return this;
    }



    public String pwd(){
        String path  =this.dirStack.stream().collect(Collectors.joining("/"));
        if(this.isLockhome()){
            if(path.contains(PARENTPATH)){
                path = path.replace(PARENTPATH,BLANK);
            }

            if(path.contains(CURRENTPATH)){
                path = path.replace(CURRENTPATH,BLANK);
            }

            path = this.home_dir+path;
        }
        return path;
    }

    public List<String> ls() throws IOException, InterruptedException {
        String path  = this.pwd();
        String cli = this.getDir().replace(this.getPlaceholder(),path);
        return ls.lsService.lsFiles(remoteHost,cli,this.chartset);
    }

    public List<String> ls(String path) throws IOException, InterruptedException {
        return ls.lsService.lsFiles(remoteHost,path,this.chartset);
    }

    public Ls printList(){
        try {
            this.ls().stream().forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return this;
    }

    public Ls printPwd(){
        System.out.println(this.pwd());
        return this;
    }

}
