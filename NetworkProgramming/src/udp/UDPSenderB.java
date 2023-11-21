package udp;


import java.io.IOException;
import java.net.*;

/***
 * ���Ͷ�B
 */
public class UDPSenderB {
    public static void main(String[] args) throws IOException {
        //1. ���� DatagramSocket ����׼����8888 �˿ڽ�������
        DatagramSocket socket = new DatagramSocket(8888);

        //2. ����Ҫ���͵����ݣ���װ�� DatagramPacket ������
        byte[] data = "hello������Ի��~".getBytes();
        //˵������װ�� DatagramPacket���� �Ĳ�����data���ֽ����飩��data.length������ip���˿�
        DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName("192.168.56.1"),9999);
        socket.send(packet);

        //3. ���ջظ���Ϣ
        byte[] buf = new byte[1024];
        packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);

        int len = packet.getLength();
        data = packet.getData();
        String s = new String(data, 0, len);
        System.out.println(s);


        //3.�ر���Դ
        socket.close();
        System.out.println("B���˳�...");

    }
}
