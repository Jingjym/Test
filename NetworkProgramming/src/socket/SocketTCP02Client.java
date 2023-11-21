package socket;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * 使用字节流
 * 客户端
 * */
public class SocketTCP02Client {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket(InetAddress.getLocalHost(),9999);
        System.out.println("客户端获取连接：" + socket.toString());

        OutputStream outputStream = socket.getOutputStream();
        outputStream.write("hello server!".getBytes());
        //设置一个结束标志，告诉对方输出完毕
        socket.shutdownOutput();

        InputStream inputStream = socket.getInputStream();
        byte[] buf = new byte[1024];
        int readLen = 0;
        while ((readLen = inputStream.read(buf)) != -1){
            System.out.println(new String(buf,0,readLen));
        }

        socket.close();
        outputStream.close();
        inputStream.close();
    }
}
