package com.pdc.spring.util;

import java.io.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 流操作工具类
 * @author pdc
 */
public final class StreamUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(StreamUtil.class);

    /**
     * 从输入流中获取字符串形式的信息
     */
    public static String getStringInfo(InputStream is) {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = null;
        InputStreamReader isReader = new InputStreamReader(is);
        try {
            reader = new BufferedReader(isReader);
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            LOGGER.error("getStringInfo() failure", e);
            throw new RuntimeException(e);
        } finally {
            try {
                reader.close();
                isReader.close();
            } catch (IOException e) {
                LOGGER.error("close Reader failure", e);
            }
        }
        return sb.toString();
    }

    /**
     * 将输入流复制到输出流
     */
    public static void copyStream(InputStream inputStream, OutputStream outputStream) {
        try {
            int length;
            byte[] buffer = new byte[4 * 1024];
            while ((length = inputStream.read(buffer, 0, buffer.length)) != -1) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.flush();
        } catch (IOException e) {
            LOGGER.error("copy stream failure", e);
            throw new RuntimeException(e);
        } finally {
            try {
                inputStream.close();
                outputStream.close();
            } catch (Exception e) {
                LOGGER.error("close stream failure", e);
            }
        }
    }
}
