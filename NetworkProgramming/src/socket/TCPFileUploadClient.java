package socket;

import Utils.StreamUtils;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;


/***
 * 文件上传的客户端
 */

public class TCPFileUploadClient {
    public static void main(String[] args) throws Exception {
        Socket socket = new Socket(InetAddress.getLocalHost(), 8888);

        //创建读取磁盘文件的输入流
        String filePath = "E:\\green.jpeg";
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(filePath));

        //bytes 就是filePath对应的字节数组
        byte[] bytes = StreamUtils.streamToByteArray(bis);

        //通过socket获取到输出流，将bytes数据发给服务端
        BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
        bos.write(bytes);//将对应的字节数组写入到数据通道
        bis.close();
        socket.shutdownOutput();//设置结束标记

        //接收服务器发来的回复消息
        InputStream inputStream = socket.getInputStream();
        //使用StreamToString方法，直接将InputStream 读取到的内容 转成字符串
        String s = StreamUtils.streamToString(inputStream);
        System.out.println(s);


        //关闭流
        inputStream.close();
        bos.close();
        socket.close();


    }
}
