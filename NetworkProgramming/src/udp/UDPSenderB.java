package udp;


import java.io.IOException;
import java.net.*;

/***
 * 发送端B
 */
public class UDPSenderB {
    public static void main(String[] args) throws IOException {
        //1. 创建 DatagramSocket 对象，准备在8888 端口接收数据
        DatagramSocket socket = new DatagramSocket(8888);

        //2. 将需要发送的数据，封装到 DatagramPacket 对象中
        byte[] data = "hello，明天吃火锅~".getBytes();
        //说明：封装的 DatagramPacket对象 的参数：data（字节数组），data.length，主机ip，端口
        DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName("192.168.56.1"),9999);
        socket.send(packet);

        //3. 接收回复消息
        byte[] buf = new byte[1024];
        packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);

        int len = packet.getLength();
        data = packet.getData();
        String s = new String(data, 0, len);
        System.out.println(s);


        //3.关闭资源
        socket.close();
        System.out.println("B端退出...");

    }
}
