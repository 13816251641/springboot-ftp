package com.lujieni.ftp.utils;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * @Auther lujieni
 * @Date 2020/6/11
 */
public final class FtpUtil {

    private FtpUtil(){

    }

    public static Boolean uploadFile(String host, int port, String username, String password, String basePath,
                                     String filePath, String fileName, InputStream inputStream) throws IOException {
        //1、创建临时路径
        String tempPath="";
        //2、创建FTPClient对象（对于连接ftp服务器，以及上传和上传都必须要用到一个对象）
        FTPClient ftp = new FTPClient();
        try{
            //3、定义返回的状态码
            int reply;
            //4、连接ftp(当前项目所部署的服务器和ftp服务器之间可以相互通讯，表示连接成功)
            ftp.connect(host,port);
            //5、输入账号和密码进行登录
            ftp.login(username,password);
            //6、接受状态码(如果成功，返回230，如果失败返回503)
            reply = ftp.getReplyCode();
            //7、根据状态码检测ftp的连接，调用isPositiveCompletion(reply)-->如果连接成功返回true，否则返回false
            if( ! FTPReply.isPositiveCompletion(reply)){
                //说明连接失败，需要断开连接
                ftp.disconnect();
                return false;
            }
            //8、changWorkingDirectory(linux上的文件夹):检测所传入的目录是否存在,如果存在返回true,否则返回false
            //basePath+filePath-->/lujieni/docs/
            String fullPath = basePath + filePath;
            /* 路径不存在的话 */
            if(!ftp.changeWorkingDirectory(fullPath)){
                //9、截取filePath:/29-->String[]:"" 29
                String[] dirs = fullPath.split("/");// ["","lujieni","","docs"]
                //10、把basePath(/home/ftp/www)-->tempPath
                for(int i = 0;i < dirs.length;i++){
                    //11、循环数组(第一次循环)
                    if(i==0){
                        tempPath = dirs[0];
                        ftp.changeWorkingDirectory(tempPath);
                    }else{
                        if(StringUtils.isEmpty(dirs[i]))
                            continue;
                        //12、更换临时路径:/lujieni
                        tempPath += "/" + dirs[i];
                        //13、再次检测路径是否存在 返回false:说明路径不存在,true:路径不存在
                        if(!ftp.changeWorkingDirectory(tempPath)){
                            //14、makeDirectory():创建目录  返回Boolean雷类型，成功返回true
                            if(!ftp.makeDirectory(tempPath)){
                                return false;
                            }else {
                                //15、严谨判断（重新检测路径是否真的存在(检测是否创建成功)）
                                ftp.changeWorkingDirectory(tempPath);
                            }
                        }
                    }
                }
            }
            //16.把文件转换为二进制字符流的形式进行上传
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            //17、这才是真正上传方法storeFile(filename,input),返回Boolean类型，上传成功返回true
            if (!ftp.storeFile(fileName, inputStream)) {
                return false;
            }
            // 18.退出ftp
            ftp.logout();
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException(e);
        } finally {
            if (ftp.isConnected()) {
                try {
                    // 19.断开ftp的连接
                    ftp.disconnect();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
        return true;
    }
}
