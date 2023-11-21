package socket;


import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * ʹ���ַ���
 * �ͻ���
 * */
public class SocketTCP03Client {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket(InetAddress.getLocalHost(),9999);
        System.out.println("�ͻ��˻�ȡ����" + socket.toString());

        OutputStream outputStream = socket.getOutputStream();
        //�ֽ���ת�ַ�����ת������
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
        bufferedWriter.write("hello server!(�ַ���)");
        //����һ�����з�����ʾд������ݽ�����ע�⣺Ҫ��Է�ʹ��readLine()������
        bufferedWriter.newLine();
        //��Ҫ�ֶ�ˢ�½�����д������ͨ��
        bufferedWriter.flush();

        //��ʾ������������ַ�����ѡһ����
        //socket.shutdownOutput();

        InputStream inputStream = socket.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String s = bufferedReader.readLine();
        System.out.println(s);

        //ֱ�ӹر�inputStream�������bufferedReader����
        //��򿪵����ȹر�
        bufferedReader.close();
        bufferedWriter.close();
        socket.close();

    }
}
