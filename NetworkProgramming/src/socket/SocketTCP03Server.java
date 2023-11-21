package socket;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/***
 * 使用字符流
 * 服务端
 */

public class SocketTCP03Server {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(9999);
        System.out.println("服务器正在监听....");

        Socket socket = serverSocket.accept();
        System.out.println("服务器收到连接：" + socket.toString());

        InputStream inputStream = socket.getInputStream();
        //字节流转字符流再转缓冲流
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String s = bufferedReader.readLine();
        System.out.println(s);

        OutputStream outputStream = socket.getOutputStream();
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
        bufferedWriter.write("hello client!(字符流)");
        bufferedWriter.newLine();
        bufferedWriter.flush();

        bufferedWriter.close();
        bufferedReader.close();
        socket.close();
        serverSocket.close();

    }
}
