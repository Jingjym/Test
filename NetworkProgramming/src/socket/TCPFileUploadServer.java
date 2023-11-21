package socket;


import Utils.StreamUtils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/***
 * �ļ��ϴ��ķ����
 */
public class TCPFileUploadServer {
    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(8888);
        System.out.println("�������ѿ��������ڼ���......");

        //�ȴ�����
        Socket socket = serverSocket.accept();

        //��ȡ�ͻ��˷��͵�����
        //ͨ��socket�õ�������
        BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
        //�õ����͹����ļ����ֽ�����
        byte[] bytes = StreamUtils.streamToByteArray(bis);

        //���õ���bytes���飬д�뵽ָ����·�����͵õ�һ���ļ�
        String destFilePath = "src\\green.jpeg";
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(destFilePath));
        bos.write(bytes);
        bos.close();

        //���ͻ��˻ظ���Ϣ
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        writer.write("�յ�ͼƬ��");
        writer.flush();
        socket.shutdownOutput();

        //�ر�������Դ
        writer.close();
        bis.close();
        socket.close();
        serverSocket.close();


    }
}
