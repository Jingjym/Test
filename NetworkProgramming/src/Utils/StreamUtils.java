package Utils;

import java.io.*;

public class StreamUtils {
    /**
     *功能：将输入流转换成byte[],即可以把文件的内容读入到byte[]
     * @param is
     * @return
     * @throws Exception
     */
    public static byte[] streamToByteArray(InputStream is) throws Exception {
        //创建输出流对象
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        //字节数组
        byte[] b = new byte[1024];
        int len;
        //循环读取，把读取到的数据写入bos
        while ((len=is.read(b))!=-1){
            bos.write(b,0,len);
        }
        //将bos转成字节数组
        byte[] array = bos.toByteArray();
        bos.close();
        return array;
    }


    /**
     * 功能：将InputStream转换成String
     * @param is
     * @return
     * @throws Exception
     */

    public static String streamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line=reader.readLine())!=null){
            builder.append(line+"\r\n");
        }
        return builder.toString();
    }
}
