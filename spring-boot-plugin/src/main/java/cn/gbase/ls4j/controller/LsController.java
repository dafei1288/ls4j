package cn.gbase.ls4j.controller;

import cn.gbase.ls4j.Config;
import cn.gbase.ls4j.domain.MkdirTo;
import cn.gbase.ls4j.domain.RemoteHost;
import cn.gbase.ls4j.domain.ResponseTo;
import cn.gbase.ls4j.service.LsService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Log
@RestController
@RequestMapping(value = "/api/v1", produces="application/json;charset=utf-8") // method= RequestMethod.GET,produces="text/plain;charset=utf-8"
public class LsController {



    @Setter
    @Getter
    @Autowired
    private Config config;

    @Autowired
    private LsService lsService;

    @RequestMapping(value = "/ls",method = RequestMethod.GET )
    private String ls(@RequestParam(value = "uid" , required = true , defaultValue = "") String uid , @RequestParam(value = "next" , required = false , defaultValue = "") String next){
        ResponseTo responseTo = ResponseTo.build();
        RemoteHost rh = new RemoteHost();

        rh.setHost(config.getHostById(uid));
        rh.setPort(config.getPortById(uid));
        rh.setUser(config.getUserById(uid));
        rh.setPass(config.getPassById(uid));

//        String cli = config.getDir();

//        System.out.println(cli);

        List<String> res = new ArrayList<>();
        try {

            next = next == null?"":next;

            String cli = config.processDirCli(next);

            res = lsService.lsFiles(rh , cli ,config.getChartset());

            if (res == null){
                throw  new RuntimeException("none dirs");
            }
            res = res.stream().filter(it->(it!=null && !"".equals(it))).collect(Collectors.toList());
            if(res.size() == 0){
                throw  new RuntimeException("none dirs");
            }

            responseTo.OK().data(res);
        }catch (RuntimeException e){
//            e.printStackTrace();
            responseTo.OK().data(null);
        }catch (IOException e) {
            e.printStackTrace();
            responseTo.error().errorcode(e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
            responseTo.error().errorcode(e.getMessage());
        }

        return responseTo.parseResponseString();
    }



    @RequestMapping(value = "/pwd",method = RequestMethod.GET )
    private String pwd(@RequestParam(value = "uid" , required = true , defaultValue = "") String uid , @RequestParam(value = "next" , required = false , defaultValue = "") String next){
        ResponseTo responseTo = ResponseTo.build();
        RemoteHost rh = new RemoteHost();

        rh.setHost(config.getHostById(uid));
        rh.setPort(config.getPortById(uid));
        rh.setUser(config.getUserById(uid));
        rh.setPass(config.getPassById(uid));

//        String cli = config.getDir();

//        System.out.println(cli);

        List<String> res = new ArrayList<>();
        try {

            next = next == null?"":next;


            String cli = config.processPwdCli(next);
            res.add(cli);
//            res = lsService.lsFiles(rh , cli ,config.getChartset());
            responseTo.OK().data(cli);
        } catch (Exception e) {
            e.printStackTrace();
            responseTo.error().errorcode(e.getMessage());
        }

        return responseTo.parseResponseString();
    }


    @RequestMapping(value = "/mkdir",method = RequestMethod.POST )
    //@RequestParam(value = "uid" , required = true , defaultValue = "") String uid , @RequestParam(value = "dir" , required = false , defaultValue = "") String dir
    private String mkdir(@RequestBody MkdirTo mkdirTo){
        ResponseTo responseTo = ResponseTo.build();
        RemoteHost rh = new RemoteHost();
        System.out.println(mkdirTo);

        String uid = mkdirTo.getUid();
        String dir = mkdirTo.getDir();


        rh.setHost(config.getHostById(uid));
        rh.setPort(config.getPortById(uid));
        rh.setUser(config.getUserById(uid));
        rh.setPass(config.getPassById(uid));

//        String cli = config.getDir();

//        System.out.println(cli);

        List<String> res = new ArrayList<>();
        try {

            dir = dir == null?"":dir;


            String cli = config.processMkdirCli(dir);
//            res.add(cli);
            System.out.println("mkdir : ==> "+cli);
            res = lsService.lsFiles(rh , cli ,config.getChartset());
            responseTo.OK().data(dir);
        } catch (Exception e) {
            e.printStackTrace();
            responseTo.error().errorcode(e.getMessage());
        }

        return responseTo.parseResponseString();
    }



    @RequestMapping(value = "/",method = RequestMethod.GET )
    private String root(){
        //RemoteHost rh = environment.getProperty("ls4j."+uid, RemoteHost.class);
        return config.toString();
    }

}
