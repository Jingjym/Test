package socket;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/***
 * ʹ���ַ���
 * �����
 */

public class SocketTCP03Server {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(9999);
        System.out.println("���������ڼ���....");

        Socket socket = serverSocket.accept();
        System.out.println("�������յ����ӣ�" + socket.toString());

        InputStream inputStream = socket.getInputStream();
        //�ֽ���ת�ַ�����ת������
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String s = bufferedReader.readLine();
        System.out.println(s);

        OutputStream outputStream = socket.getOutputStream();
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
        bufferedWriter.write("hello client!(�ַ���)");
        bufferedWriter.newLine();
        bufferedWriter.flush();

        bufferedWriter.close();
        bufferedReader.close();
        socket.close();
        serverSocket.close();

    }
}
