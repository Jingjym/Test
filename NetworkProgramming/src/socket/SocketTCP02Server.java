package socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * ʹ���ֽ���
 * �����
 * */
public class SocketTCP02Server {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(9999);
        System.out.println("���������ڼ���.....");

        Socket socket = serverSocket.accept();
        System.out.println("������յ����ӣ�" + socket.toString());

        InputStream inputStream = socket.getInputStream();
        byte[] buf = new byte[1024];
        int readLen = 0;
        while((readLen = inputStream.read(buf)) != -1){
            System.out.println(new String(buf,0,readLen));
        }

        OutputStream outputStream = socket.getOutputStream();
        outputStream.write("hello client!".getBytes());
        //����һ��������־�����߶Է�������
        socket.shutdownOutput();

        socket.close();
        inputStream.close();
        outputStream.close();
        serverSocket.close();


        }
    }

