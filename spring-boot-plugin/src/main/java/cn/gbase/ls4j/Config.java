package cn.gbase.ls4j;

import lombok.Getter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.stream.Collectors;


@ToString
@Configuration
public class Config {
    @Autowired
    @Getter
    private Environment environment;


    public final static String LS4J = "ls4j";

    public final static String HOST = "host";
    public final static String PORT = "port";
    public final static String USER = "user";
    public final static String PASS = "pass";

    public final static String CURRENTPATH = "./";
    public final static String PARENTPATH = "../";
    public final static String BLANK = "";

    public final static String DOT = ".";

    @Value("${ls4j.cli.dir}")
    @Getter
    private String dir;

    @Value("${ls4j.cli.pwd}")
    @Getter
    private String pwd;


    @Value("${ls4j.cli.mkdir}")
    @Getter
    private String mkdir;

    @Value("${ls4j.cli.chartset}")
    @Getter
    private String chartset;

    @Value("${ls4j.cli.lockhome}")
    @Getter
    private boolean lockhome;

    @Value("${ls4j.cli.homepath}")
    @Getter
    private String homepath;

    @Value("${ls4j.cli.placeholder}")
    @Getter
    private String placeholder;


    public String getHostById(String id){
        return environment.getProperty(this.joinString(LS4J,id,HOST));
    }

    public int getPortById(String id){
        return Integer.parseInt(environment.getProperty(this.joinString(LS4J,id,PORT)));
    }

    public String getUserById(String id){
        return environment.getProperty(this.joinString(LS4J,id,USER));
    }

    public String getPassById(String id){
        return environment.getProperty(this.joinString(LS4J,id,PASS));
    }

//        rh.setHost(config.getEnvironment().getProperty("ls4j."+uid+".host"));
//        rh.setPort(Integer.parseInt(config.getEnvironment().getProperty("ls4j."+uid+".port")));
//        rh.setUser(config.getEnvironment().getProperty("ls4j."+uid+".user"));
//        rh.setPass(config.getEnvironment().getProperty("ls4j."+uid+".pass"));


    private String joinString(String...args){
        String res = Arrays.stream(args).collect(Collectors.joining(DOT));
//        System.out.println(res);
        return res;
    }


    public String processDir(String next){
        System.out.println(next);
        String dir = next;
        if(this.isLockhome()){
            if(next.contains(PARENTPATH)){
                next = next.replace(PARENTPATH,BLANK);
            }

            if(next.contains(CURRENTPATH)){
                next = next.replace(CURRENTPATH,BLANK);
            }

            dir = this.getHomepath()+next;
        }
        System.out.println(dir);
        return dir;
    }


    public String processDirCli(String next){
        String  dir = this.processDir(next);
        String cli = this.getDir().replace(this.getPlaceholder(),dir);
        System.out.println(cli);
        return cli;
    }

    public String processPwdCli(String next){
        String  dir = this.processDir(next);
        String cli = this.getPwd().replace(this.getPlaceholder(),dir);
        System.out.println(cli);
        return dir;
    }

    public String processMkdirCli(String next){
        String  dir = this.processDir(next);
        String cli = this.getMkdir().replace(this.getPlaceholder(),dir);
        System.out.println(cli);
        return cli;
    }
    public String getString(String key){
        return environment.getProperty(key);
    }

}
