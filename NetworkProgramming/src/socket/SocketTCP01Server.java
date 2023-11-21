package socket;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 使用字节流
* 服务端
* */

public class SocketTCP01Server {
    public static void main(String[] args) throws IOException {

//      1.在本机9999端口监听，等待连接
        //细节：本机没有其他服务在监听9999
        //细节：这个serversocket 可以通过accept() 返回多个socket[多个客户端连接的多线程并发]
        ServerSocket serverSocket = new ServerSocket(9999);
        System.out.println("服务端，在9999端口监听，等待连接....");
//      2.没有连接时，程序阻塞，等待连接
        //如果有客户端连接，则会返回Socket对象，程序继续
        Socket socket = serverSocket.accept();

        System.out.println("服务端：socket = " + socket.getClass());
//      3.通过socket.getInputStream() 读取
//         客户端写入到数据通道的数据，并显示
        InputStream inputStream = socket.getInputStream();
        //4. IO读取
        byte[] buf = new byte[1024];
        int readLen = 0; //读取到的字节数组的长度
        while ((readLen = inputStream.read(buf)) != -1){
            //根据读取到的实际长度，显示内容
            System.out.println(new String(buf,0,readLen));
        }
        //5. 关闭流 ,socket, 以及serverSocket
        inputStream.close();
        socket.close();
        serverSocket.close();


    }
}
