package com.lujieni.ftp;

import com.lujieni.ftp.config.FtpProperties;
import com.lujieni.ftp.utils.FtpUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedInputStream;
import java.io.InputStream;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FtpApplicationTests {

    @Autowired
    private FtpProperties ftpProperties;

    /**
     * 上传文件至ftp服务器
     */
    @Test
    public void contextLoads() {
      try(InputStream is = new BufferedInputStream(this.getClass().getResourceAsStream("/excel/c.xlsx"))){
          FtpUtil.uploadFile(ftpProperties.getHost(),ftpProperties.getPort(),
                             ftpProperties.getUsername(),ftpProperties.getPassword(),
                             ftpProperties.getBasePath(),"/haha/","bbb.xlsx",is);
      }catch (Exception e){
          System.out.println(e);
      }
    }

}
