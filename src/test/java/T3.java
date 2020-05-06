import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ClientChannel;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.scp.ScpClient;
import org.apache.sshd.client.scp.ScpClientCreator;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.scp.helpers.DefaultScpFileOpener;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class T3 {
    public static void main(String[] args) throws IOException, InterruptedException
    {
        SshClient client = null;
        ClientSession session = null;
        String result = null;
        String error = null;
        try {
            client = SshClient.setUpDefaultClient();
            client.start();
            session = client.connect("root", "192.168.56.101", 22).verify(10 * 1000).getSession();
            session.addPasswordIdentity("dafei1288");
            if (!session.auth().verify(10 * 1000).isSuccess()) {
                throw new Exception("auth faild");
            }


            ScpClientCreator creator = ScpClientCreator.instance();
            creator.setScpFileOpener(DefaultScpFileOpener.INSTANCE);
            ScpClient sclient = creator.createScpClient(session);

//            OutputStream os = new FileOutputStream("a1pplication.yml");
//            sclient.download("application.yml",os);


//            sclient.upload("E:\\working\\GBase\\newworkspace\\testjsch\\build.gradle","/root/");

//cd /opt/GBase/ &&

//            文件
//            ls -l | grep ^[^d] | awk '{print $9}'

//            文件夹
//            ls -l |grep ^d | awk '{print $9}'
            ClientChannel channel = session.createExecChannel("ls -l |grep ^d | awk '{print $9}'");
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
            result = new String(out.toByteArray(), Charset.forName("GBK"));
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
        System.out.println("--------------------result--------------------");
        System.out.println(result);

        System.out.println("--------------------error--------------------");
        System.out.println(error);
    }
}
