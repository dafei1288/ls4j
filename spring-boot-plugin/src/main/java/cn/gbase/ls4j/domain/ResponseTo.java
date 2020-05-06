package cn.gbase.ls4j.domain;


import com.google.common.collect.Maps;
import com.google.gson.Gson;

import java.util.Map;

public class ResponseTo {
    public static final String STATE = "state";
    public static final String OK = "ok";
    public static final String ERROR = "error";
    public static final String ERRORCODE = "errorcode";
    public static final String DATA = "data";

    public static final String LOG = "log";

    private Map<String, Object> mess = Maps.newHashMap();
//    private Map<String, Object> data = Maps.newHashMap();

    private ResponseTo(){}



    public static ResponseTo build(){
        ResponseTo rt = new ResponseTo();
        rt.mess.put(STATE,OK);
        return rt;
    }

    public ResponseTo OK(){
        this.mess.put(STATE,OK);
        return this;
    }
    public ResponseTo error(){
        this.mess.put(STATE,ERROR);
        return this;
    }
    public ResponseTo errorcode(String code){
        this.mess.put(ERRORCODE,code);
        return this;
    }

    public ResponseTo log(String log){
        this.mess.put(LOG,log);
        return this;
    }

    public ResponseTo data(Object o){
        this.mess.put(DATA,o);
        return this;
    }

    public String parseResponseString(){
        return new Gson().toJson(this.mess);
    }

}
