package udp;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * ���ն�A
 */
public class UDPReceiverA {
    public static void main(String[] args) throws IOException {
        //1. ����һ�� DatagramSocket ����׼����9999�˿ڽ�������
        DatagramSocket socket = new DatagramSocket(9999);

        //2. ����һ��DatagramPacket ����׼����������
        byte[] buf = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);

        //3. ���ý��շ�������ͨ�����紫�ݹ����� DatagramPacket ���� ��䵽 packet����
        // ע�⣺û�����ݰ����͹���ʱ�����ڴ������ȴ�
        System.out.println("���ն�A �ȴ���������....");
        socket.receive(packet);

        //4. packet���в����ȡ�����ݣ�����ʾ
        //ʵ�ʽ��յ��������ֽڳ���
        int len = packet.getLength();
        //���յ�����
        byte[] data = packet.getData();
        String s = new String(data, 0, len);
        System.out.println(s);

        //5. ���ͻظ���Ϣ
        data = "�õģ��������".getBytes();
        packet = new DatagramPacket(data, data.length, InetAddress.getByName("192.168.56.1"), 8888);
        socket.send(packet);


        //6. �ر���Դ
        socket.close();
        System.out.println("A���˳�....");

    }
}
