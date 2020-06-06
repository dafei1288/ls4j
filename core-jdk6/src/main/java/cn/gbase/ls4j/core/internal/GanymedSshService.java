package cn.gbase.ls4j.core.internal;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import cn.gbase.ls4j.core.RemoteHost;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * 适应JDK6改造
 * */
public class GanymedSshService implements IService {
    @Override
    public void rmdir(RemoteHost remoteHost, String remotePath, String charset) throws Exception {
        System.out.println("method : rmdir ==> " + remotePath);
    }

    @Override
    public void mkdir(RemoteHost remoteHost, String remotePath, String charset) throws Exception {
        System.out.println("method : mkdir ==> " + remotePath);
        Connection conn = new Connection(remoteHost.getHost(),remoteHost.getPort());
        conn.connect();

        List<String> datas = new ArrayList<String>();

        boolean isAuthenticated = conn.authenticateWithPassword(remoteHost.getUser(), remoteHost.getPass());
        if(!isAuthenticated){
            throw new Exception("open faild");
        }else{
            Session sess = conn.openSession();
            String ncli = "mkdirs "+ remotePath ;
            System.out.println("cli ==> " + ncli);

            sess.execCommand(ncli);

            InputStream stdout = new StreamGobbler(sess.getStdout());

            BufferedReader br = new BufferedReader(new InputStreamReader(stdout));

            while (true)
            {
                String line = br.readLine();
                datas.add(line);
                if (line == null) {
                    break;
                }
                System.out.println(line);
            }
            System.out.println("ExitCode: " + sess.getExitStatus());
            sess.close();
            conn.close();
        }

    }

    @Override
    public List<String> lsFiles(RemoteHost remoteHost, String cli, String charset) throws Exception {
        Connection conn = new Connection(remoteHost.getHost(),remoteHost.getPort());
        conn.connect();

        List<String> datas = new ArrayList<String>();

        boolean isAuthenticated = conn.authenticateWithPassword(remoteHost.getUser(), remoteHost.getPass());
        if(!isAuthenticated){
            throw new Exception("open faild");
        }else{
            Session sess = conn.openSession();
//            String ncli = "ls -l "+ cli +" |grep ^d | awk '{print $9}'";
            System.out.println("cli ==> " + cli);

            sess.execCommand(cli);

            InputStream stdout = new StreamGobbler(sess.getStdout());

            BufferedReader br = new BufferedReader(new InputStreamReader(stdout));

            while (true)
            {
                String line = br.readLine();
                datas.add(line);
                if (line == null) {
                    break;
                }
                System.out.println(line);
            }
            System.out.println("ExitCode: " + sess.getExitStatus());
            sess.close();
            conn.close();
        }

        return datas;
    }

    @Override
    public void uploadSFTP(RemoteHost remoteHost, String localPath, String remotePath) throws Exception {

    }

    @Override
    public String pwd(RemoteHost remoteHost, String remotePath, String charset) throws Exception {
        System.out.println("method : pwd ==> " + remotePath);
        Connection conn = new Connection(remoteHost.getHost(),remoteHost.getPort());
        conn.connect();

        List<String> datas = new ArrayList<String>();

        boolean isAuthenticated = conn.authenticateWithPassword(remoteHost.getUser(), remoteHost.getPass());
        if(!isAuthenticated){
            throw new Exception("open faild");
        }else{
            Session sess = conn.openSession();
//            String ncli = "cd "+ remotePath +" && pwd ";
            System.out.println("pwd ==> " + remotePath);

            sess.execCommand(remotePath);

            InputStream stdout = new StreamGobbler(sess.getStdout());

            BufferedReader br = new BufferedReader(new InputStreamReader(stdout));

            while (true)
            {
                String line = br.readLine();
                datas.add(line);
                if (line == null) {
                    break;
                }
                System.out.println(line);
            }
            System.out.println("ExitCode: " + sess.getExitStatus());
            sess.close();
            conn.close();
        }
        return datas.size()>0?datas.get(0):"";
    }
}
