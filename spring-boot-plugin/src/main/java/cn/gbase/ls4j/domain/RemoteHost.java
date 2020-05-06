package cn.gbase.ls4j.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;

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
