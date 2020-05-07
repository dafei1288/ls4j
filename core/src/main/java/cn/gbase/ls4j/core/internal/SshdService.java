package cn.gbase.ls4j.core.internal;


import cn.gbase.ls4j.core.RemoteHost;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ClientChannel;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.scp.ScpClient;
import org.apache.sshd.client.scp.ScpClientCreator;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.client.subsystem.sftp.SftpClient;
import org.apache.sshd.client.subsystem.sftp.SftpClientFactory;
import org.apache.sshd.client.subsystem.sftp.SftpRemotePathChannel;
import org.apache.sshd.client.subsystem.sftp.fs.SftpFileSystemProvider;
import org.apache.sshd.common.scp.helpers.DefaultScpFileOpener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.*;
import java.util.logging.Level;

@Slf4j
public class SshdService implements  IService{
    @Override
    public void mkdir(RemoteHost remoteHost, String remotePath, String charset) throws Exception {

    }

    public List<String>  lsFiles(RemoteHost remoteHost , String cli , String charset ) throws IOException, InterruptedException {
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
        log.debug(result);
        log.info("error: "+error);

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
//    public void  uploadSFTP(RemoteHost remoteHost , String localPath, String remotePath) throws IOException, InterruptedException {
//        SshClient client = null;
//        ClientSession session = null;
//        String result = null;
//        String error = null;
//
//            client = SshClient.setUpDefaultClient();
//            client.start();
//            session = client.connect(remoteHost.getUser(), remoteHost.getHost(), remoteHost.getPort()).verify(10 * 1000).getSession();
//            session.addPasswordIdentity(remoteHost.getPass());
//            if (!session.auth().verify(10 * 1000).isSuccess()) {
//                throw new RuntimeException("auth faild");
//            }
//
//            ScpClientCreator creator = ScpClientCreator.instance();
//            creator.setScpFileOpener(DefaultScpFileOpener.INSTANCE);
//            ScpClient sclient = creator.createScpClient(session);
//
//
//            sclient.upload(localPath,remotePath);
//
//        session.close(false);
//        client.stop();
//
//    }
    public void uploadSFTP(RemoteHost remoteHost , String localPath, String remotePath) throws IOException {
//        SshClient client = SshClient.setUpDefaultClient();
//        client.start();
//        ClientSession session = client.connect(remoteHost.getUser(), remoteHost.getHost(), remoteHost.getPort()).verify().getSession();
//        session.addPasswordIdentity(remoteHost.getPass());
//        session.auth().verify();
//
//
////        ScpClientCreator creator = ScpClientCreator.instance();
////        creator.setScpFileOpener(DefaultScpFileOpener.INSTANCE);
////        ScpClient sclient = creator.createScpClient(session);
//
//        SftpClientFactory factory = SftpClientFactory.instance();
//        SftpClient sftp = factory.createSftpClient(session);
//
////        sftp.openRemotePathChannel(remotePath,Arrays.asList(StandardOpenOption.CREATE,StandardOpenOption.WRITE));
//
////        SftpFileSystemProvider provider = new SftpFileSystemProvider(client);
////        URI uri = SftpFileSystemProvider.createFileSystemURI(remoteHost.getHost(), remoteHost.getPort(), remoteHost.getUser(), remoteHost.getPass());
////        try (FileSystem fs = provider.newFileSystem(uri, Collections.<String, Object>emptyMap())) {
////            Path tp = fs.getPath(remotePath);
////            System.out.println(tp);
////
////
////
////            System.out.println(new File(localPath).toPath());
////            Files.copy(new File(localPath).toPath(),tp,StandardCopyOption.REPLACE_EXISTING);
////        }
//
//        List<StandardOpenOption> all = new ArrayList<>();
//        all.add(StandardOpenOption.WRITE);
//        all.add(StandardOpenOption.CREATE);
////        OpenOptio
//        SftpRemotePathChannel sftpRemotePathChannel = sftp.openRemotePathChannel(remotePath,all) ;
//
////        EnumSet.of(SftpClient.OpenMode.Write, SftpClient.OpenMode.Create)
////                sftp.open(remotePath,
////                EnumSet.of(SftpClient.OpenMode.Write, SftpClient.OpenMode.Create));
//        FileInputStream in = new FileInputStream(localPath);
//        int buff_size = 1024 * 1024;
//        ByteBuffer src = ByteBuffer.allocate(buff_size);//[buff_size];
//        int len;
//        int fileOffset = 0;
//        byte[] temp = new byte[buff_size];
//        while ((len = in.read(temp)) != -1) {
//            sftpRemotePathChannel.write(src,fileOffset,len);
////            sftp.write(handle, fileOffset, src, 0, len);
//            fileOffset += len;
//        }
//
//        in.close();
//        sftp.close(handle);
//
//        session.close(false);
//        client.stop();
    }

    @Override
    public String pwd(RemoteHost remoteHost, String remotePath, String charset) throws Exception {
        return null;
    }

}
