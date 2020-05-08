package cn.gbase.ls4j.core.internal;

import cn.gbase.ls4j.core.RemoteHost;
import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class JschService  implements  IService{

    public void rmdir(RemoteHost remoteHost, String remotePath, String charset) throws Exception{
        ChannelSftp channelSftp = ChannelSftpSingleton.getInstance().getChannelSftp(remoteHost);

        this.rmdir(remotePath,channelSftp);
    }


    public void rmdir(String remoteDir , ChannelSftp channelSftp) throws Exception{
        try{
            if(isDirectory(remoteDir,channelSftp)){
                Vector<ChannelSftp.LsEntry> dirList = channelSftp.ls(remoteDir);

                for(ChannelSftp.LsEntry entry : dirList){
                    if(!(entry.getFilename().equals(".") || entry.getFilename().equals(".."))){
                        if(entry.getAttrs().isDir()){
                            rmdir(remoteDir + "/" +  entry.getFilename()  ,channelSftp);
                        }else{
                            String filepath = remoteDir + "/" + entry.getFilename();
//                            System.out.println(filepath);
                            channelSftp.rm(filepath);
                        }
                    }
                }
                channelSftp.cd("..");
                channelSftp.rmdir(remoteDir);
            }
        }catch (SftpException e){
            e.printStackTrace();
        }
    }


    private boolean isDirectory(String remoteDirectory , ChannelSftp channelSftp) throws SftpException{
//        System.out.println(remoteDirectory);
        return channelSftp.stat(remoteDirectory).isDir();
    }




    public void mkdir(RemoteHost remoteHost, String remotePath, String charset) throws Exception{
        ChannelSftp channelSftp = ChannelSftpSingleton.getInstance().getChannelSftp(remoteHost);
        channelSftp.mkdir(remotePath);
    }

    public String pwd(RemoteHost remoteHost, String remotePath, String charset) throws Exception{
        ChannelSftp channelSftp = ChannelSftpSingleton.getInstance().getChannelSftp(remoteHost);
        channelSftp.cd(remotePath);
        return channelSftp.pwd();
//        ChannelSftpSingleton.getInstance().closeChannel();
    }

    public List<String> lsFiles(RemoteHost remoteHost, String remotePath, String charset) throws Exception {
        List<ChannelSftp.LsEntry> all = ChannelSftpSingleton.getInstance().getChannelSftp(remoteHost).ls(remotePath);
        return  all.stream().map(it->((ChannelSftp.LsEntry) it).getFilename()).filter(it->(!it.equals(".")&&!it.equals(".."))).collect(Collectors.toList());
    }

    public void  uploadSFTP(RemoteHost remoteHost , String localPath, String remotePath) throws JSchException, IOException {
        try {
            SftpUtil.uploadFilesToServer(remoteHost,localPath, remotePath, new SftpProgressMonitor() {
                @Override
                public void init(int i, String src, String dst, long size) {
//                    System.out.println("正在上传 " + src + " 到 " + dst + "，文件大小：" + (double) (size / 1024) + "kb");
                    log.debug("正在上传 " + src + " 到 " + dst + "，文件大小：" + (double) (size / 1024) + "kb");
                }

                @Override
                public boolean count(long l) {
                    return true;
                }

                @Override
                public void end() {
//                    System.out.println("上传成功");
                    log.debug("upload successfully");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class SftpUtil{
        private SftpUtil() {
        }

        public static void uploadFilesToServer(RemoteHost remoteHost,String srcPath, String dst, SftpProgressMonitor monitor) throws Exception {
            ChannelSftp sftp = upload(remoteHost,srcPath, dst, monitor);
            if (sftp != null) {
                sftp.quit();
                sftp.disconnect();
//                System.out.println(" SFTP disconnect successfully!");
                log.debug("SFTP disconnect successfully!");
            }
            ChannelSftpSingleton.getInstance().closeChannel();
        }


        private static ChannelSftp upload(RemoteHost remoteHost,String path, String dst, SftpProgressMonitor monitor) throws SftpException {
            File file = new File(path);
            if (!file.exists()) {
                return null;
            }
            ChannelSftp chSftp = null;
            try {
                chSftp = ChannelSftpSingleton.getInstance().getChannelSftp(remoteHost);
            } catch (JSchException e) {
                e.printStackTrace();
            }
            if (chSftp == null) {
                return null;
            }
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files == null || files.length <= 0) {
                    return null;
                }
                for (File f : files) {
                    String fp = f.getAbsolutePath();
                    if (f.isDirectory()) {
                        String mkdir = dst + "/" + f.getName();
                        try {
                            chSftp.cd(mkdir);
                        } catch (Exception e) {
                            System.out.println("---> "+mkdir);
                            chSftp.mkdir(mkdir);
                        }
                        upload(remoteHost,fp, mkdir, monitor);
                    } else {
                        chSftp.put(fp, dst, monitor, ChannelSftp.OVERWRITE);
                    }
                }
            } else {
                String fp = file.getAbsolutePath();
                chSftp.put(fp, dst, monitor, ChannelSftp.OVERWRITE);
            }
            return chSftp;
        }
    }


    public static class ChannelSftpSingleton{
        private static ChannelSftpSingleton instance;
        private ChannelSftp channelSftp;
        private Session session;
        private RemoteHost remoteHost;
        private ChannelSftpSingleton() {
        }

        public static ChannelSftpSingleton getInstance() {
            if (instance == null) {
                instance = new ChannelSftpSingleton();
            }
            return instance;
        }

        public ChannelSftp getChannelSftp(RemoteHost remoteHost) throws JSchException {

            if (channelSftp != null && !channelSftp.isClosed()) {
                return channelSftp;
            }
            channelSftp = getChannel(remoteHost);
            return channelSftp;
        }

        /**
         * 断开SFTP Channel、Session连接
         *
         * @throws Exception
         */
        public void closeChannel() throws Exception {
            if (channelSftp != null) {
                channelSftp.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
//            System.out.println("disconnected SFTP successfully!");
            log.debug("disconnected SFTP successfully!");
        }

        /**
         * 获得SFTP Channel
         *
         * @return ChannelSftp Instance
         * @throws JSchException
         */
        private ChannelSftp getChannel(RemoteHost remoteHost) throws JSchException {

            // 创建JSch对象
            JSch jsch = new JSch();
            // 根据用户名，主机ip，端口获取一个Session对象
            session = jsch.getSession(remoteHost.getUser(), remoteHost.getHost(), remoteHost.getPort());
            // 设置密码
            session.setPassword(remoteHost.getPass());
            Properties configTemp = new Properties();
            configTemp.put("StrictHostKeyChecking", "no");
            // 为Session对象设置properties
            session.setConfig(configTemp);
            // 设置timeout时间
            session.setTimeout(60000);
            session.connect();
            // 通过Session建立链接
            // 打开SFTP通道
            Channel channel = session.openChannel("sftp");
            // 建立SFTP通道的连接
            channel.connect();

//            System.out.println("Connected successfully to ftpHost = " + remoteHost.getHost() + ",as ftpUserName = " + remoteHost.getUser() + ", returning: " + channel);
            log.debug("Connected successfully to ftpHost = " + remoteHost.getHost() + ",as ftpUserName = " + remoteHost.getUser() + ", returning: " + channel);
            return (ChannelSftp) channel;
        }
    }


}
