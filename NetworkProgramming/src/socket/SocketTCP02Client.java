package socket;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * ʹ���ֽ���
 * �ͻ���
 * */
public class SocketTCP02Client {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket(InetAddress.getLocalHost(),9999);
        System.out.println("�ͻ��˻�ȡ���ӣ�" + socket.toString());

        OutputStream outputStream = socket.getOutputStream();
        outputStream.write("hello server!".getBytes());
        //����һ��������־�����߶Է�������
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
