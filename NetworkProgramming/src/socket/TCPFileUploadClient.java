package socket;

import Utils.StreamUtils;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;


/***
 * �ļ��ϴ��Ŀͻ���
 */

public class TCPFileUploadClient {
    public static void main(String[] args) throws Exception {
        Socket socket = new Socket(InetAddress.getLocalHost(), 8888);

        //������ȡ�����ļ���������
        String filePath = "E:\\green.jpeg";
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(filePath));

        //bytes ����filePath��Ӧ���ֽ�����
        byte[] bytes = StreamUtils.streamToByteArray(bis);

        //ͨ��socket��ȡ�����������bytes���ݷ��������
        BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
        bos.write(bytes);//����Ӧ���ֽ�����д�뵽����ͨ��
        bis.close();
        socket.shutdownOutput();//���ý������

        //���շ����������Ļظ���Ϣ
        InputStream inputStream = socket.getInputStream();
        //ʹ��StreamToString������ֱ�ӽ�InputStream ��ȡ�������� ת���ַ���
        String s = StreamUtils.streamToString(inputStream);
        System.out.println(s);


        //�ر���
        inputStream.close();
        bos.close();
        socket.close();


    }
}
