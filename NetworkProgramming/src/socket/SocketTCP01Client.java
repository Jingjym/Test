package socket;


import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * ʹ���ֽ���
 * �ͻ���
 * */
public class SocketTCP01Client {
    public static void main(String[] args) throws IOException {
        //1.���ӷ�������ip���˿ڣ�
        //��������ӱ�����9999�˿ڣ�������ӳɹ�������Socket
        Socket socket = new Socket(InetAddress.getLocalHost(),9999);
        System.out.println("�ͻ��� socket = " + socket.getClass());
        //2.�����Ϻ�����Socket��ͨ��scocket.getOutputStream()
        //�õ� �� socket��������������������
        OutputStream outputStream = socket.getOutputStream();
        //3.ͨ���������д�����ݵ� ����ͨ��
        outputStream.write("hello,server".getBytes());
        //4. �ر��������socket������ر�
        outputStream.close();
        socket.close();
        System.out.println("�ͻ����˳�....");
    }
}
