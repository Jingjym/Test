package socket;


import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * 使用字符流
 * 客户端
 * */
public class SocketTCP03Client {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket(InetAddress.getLocalHost(),9999);
        System.out.println("客户端获取连接" + socket.toString());

        OutputStream outputStream = socket.getOutputStream();
        //字节流转字符流再转缓冲流
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
        bufferedWriter.write("hello server!(字符流)");
        //插入一个换行符，表示写入的内容结束。注意：要求对方使用readLine()！！！
        bufferedWriter.newLine();
        //需要手动刷新将数据写入数据通道
        bufferedWriter.flush();

        //表示输出结束，两种方法二选一即可
        //socket.shutdownOutput();

        InputStream inputStream = socket.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String s = bufferedReader.readLine();
        System.out.println(s);

        //直接关闭inputStream的外层流bufferedReader即可
        //后打开的流先关闭
        bufferedReader.close();
        bufferedWriter.close();
        socket.close();

    }
}
