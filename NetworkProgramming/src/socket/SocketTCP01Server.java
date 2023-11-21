package socket;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * ʹ���ֽ���
* �����
* */

public class SocketTCP01Server {
    public static void main(String[] args) throws IOException {

//      1.�ڱ���9999�˿ڼ������ȴ�����
        //ϸ�ڣ�����û�����������ڼ���9999
        //ϸ�ڣ����serversocket ����ͨ��accept() ���ض��socket[����ͻ������ӵĶ��̲߳���]
        ServerSocket serverSocket = new ServerSocket(9999);
        System.out.println("����ˣ���9999�˿ڼ������ȴ�����....");
//      2.û������ʱ�������������ȴ�����
        //����пͻ������ӣ���᷵��Socket���󣬳������
        Socket socket = serverSocket.accept();

        System.out.println("����ˣ�socket = " + socket.getClass());
//      3.ͨ��socket.getInputStream() ��ȡ
//         �ͻ���д�뵽����ͨ�������ݣ�����ʾ
        InputStream inputStream = socket.getInputStream();
        //4. IO��ȡ
        byte[] buf = new byte[1024];
        int readLen = 0; //��ȡ�����ֽ�����ĳ���
        while ((readLen = inputStream.read(buf)) != -1){
            //���ݶ�ȡ����ʵ�ʳ��ȣ���ʾ����
            System.out.println(new String(buf,0,readLen));
        }
        //5. �ر��� ,socket, �Լ�serverSocket
        inputStream.close();
        socket.close();
        serverSocket.close();


    }
}
