package org.example.fabricjava;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import java.io.File;
import java.io.IOException;

/**
 * @title: FileUtil
 * @author valentinebeats
 * @version 1.0
 * @date 2022/11/27
 */
public class FileUtil {

    /**
     * 读取文件内容
     * @param filePath
     * @return
     */
    public static String readFileContent(String filePath) throws Exception{
        File file = new File(filePath);
        if (!file.exists()) {
            throw new Exception("文件不存在！");
        }

        try {
            StringBuffer sb = new StringBuffer();
            LineIterator iterator = FileUtils.lineIterator(file, "UTf-8");
            while (iterator.hasNext()) {
                String line = iterator.nextLine();
                sb.append(line+"\n");
           }
            return  sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }
}
