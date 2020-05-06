package cn.gbase.ls4j.core;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class RemoteHost {

    @Setter
    @Getter
    private String user;

    @Setter
    @Getter
    private String pass;


    @Setter
    @Getter
    private String host;

    @Setter
    @Getter
    private int port;

}
