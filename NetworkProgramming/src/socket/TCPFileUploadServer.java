package socket;


import Utils.StreamUtils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/***
 * 文件上传的服务端
 */
public class TCPFileUploadServer {
    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(8888);
        System.out.println("服务器已开启，正在监听......");

        //等待连接
        Socket socket = serverSocket.accept();

        //读取客户端发送的数据
        //通过socket得到输入流
        BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
        //得到传送过来文件的字节数组
        byte[] bytes = StreamUtils.streamToByteArray(bis);

        //将得到的bytes数组，写入到指定的路径，就得到一个文件
        String destFilePath = "src\\green.jpeg";
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(destFilePath));
        bos.write(bytes);
        bos.close();

        //给客户端回复消息
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        writer.write("收到图片！");
        writer.flush();
        socket.shutdownOutput();

        //关闭其他资源
        writer.close();
        bis.close();
        socket.close();
        serverSocket.close();


    }
}
