package cn.gbase.ls4j.core.internal;

import cn.gbase.ls4j.core.RemoteHost;
import com.jcraft.jsch.JSchException;

import java.io.IOException;
import java.util.List;

public interface IService {
    public void rmdir(RemoteHost remoteHost, String remotePath, String charset) throws Exception;
    public void mkdir(RemoteHost remoteHost, String remotePath, String charset) throws Exception;
    public List<String> lsFiles(RemoteHost remoteHost , String cli , String charset ) throws Exception;
    public void  uploadSFTP(RemoteHost remoteHost , String localPath, String remotePath) throws Exception;
    public String pwd(RemoteHost remoteHost, String remotePath, String charset) throws Exception;
}
