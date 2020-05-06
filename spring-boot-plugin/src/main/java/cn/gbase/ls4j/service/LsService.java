package cn.gbase.ls4j.service;

import cn.gbase.ls4j.domain.RemoteHost;
import lombok.extern.java.Log;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ClientChannel;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.scp.ScpClient;
import org.apache.sshd.client.scp.ScpClientCreator;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.scp.helpers.DefaultScpFileOpener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

@Component
@Service
@Log
public class LsService {
    public List<String>  lsFiles(RemoteHost remoteHost ,  String cli , String charset ) throws IOException, InterruptedException {
        SshClient client = null;
        ClientSession session = null;
        String result = null;
        String error = null;
        try {
            client = SshClient.setUpDefaultClient();
            client.start();
            session = client.connect(remoteHost.getUser(), remoteHost.getHost(), remoteHost.getPort()).verify(10 * 1000).getSession();
            session.addPasswordIdentity(remoteHost.getPass());
            if (!session.auth().verify(10 * 1000).isSuccess()) {
                throw new Exception("auth faild");
            }
            
//            ScpClientCreator creator = ScpClientCreator.instance();
//            creator.setScpFileOpener(DefaultScpFileOpener.INSTANCE);
//            ScpClient sclient = creator.createScpClient(session);



            ClientChannel channel = session.createExecChannel(cli);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ByteArrayOutputStream err = new ByteArrayOutputStream();
            channel.setOut(out);
            channel.setErr(err);

            if (!channel.open().verify(10 * 1000).isOpened()) {
                throw new Exception("open faild");
            }
            List<ClientChannelEvent> list = new ArrayList<ClientChannelEvent>();
            list.add(ClientChannelEvent.CLOSED);
            channel.waitFor(list, 10 * 1000);
            channel.close();
            result = new String(out.toByteArray(), Charset.forName(charset));
            error = err.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (client != null) {
                try {
                    client.stop();
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        log.log(Level.INFO,result);
        log.log(Level.INFO,"error: "+error);

        if(error != null && error.length() > 0 ){
            throw new RuntimeException(error);
        }

        String[] res = result.split("\n");
        List<String> temp = new ArrayList<>();
        if(res !=null){
            temp = Arrays.asList(res);
        }
        return temp;
    }
}
