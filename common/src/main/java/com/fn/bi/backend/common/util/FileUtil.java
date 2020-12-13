package com.fn.bi.backend.common.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

@Slf4j
public class FileUtil {
    public static void copyFileNIO(String oldPath, String newPath) throws IOException {
        mkdir(newPath);
        try (FileChannel inputChannel = new FileInputStream(new File(oldPath)).getChannel();
             FileChannel outputChannel = new FileOutputStream(new File(newPath)).getChannel()) {
            while (true) try (FileLock fileLock = inputChannel.tryLock(0, Long.MAX_VALUE, true)) {
                outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
                break;
            } catch (Exception e) {
                log.info("当前文件正在被另一个线程访问");
            }
        }
    }


    public static void mkdir(String strPath) {
        File file = new File(strPath).getParentFile();
        if ((!file.exists()) && file.mkdirs()) log.info("目录不存在，已创建");
    }

}
