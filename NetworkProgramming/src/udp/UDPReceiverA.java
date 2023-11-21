package udp;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * 接收端A
 */
public class UDPReceiverA {
    public static void main(String[] args) throws IOException {
        //1. 创建一个 DatagramSocket 对象，准备在9999端口接收数据
        DatagramSocket socket = new DatagramSocket(9999);

        //2. 构建一个DatagramPacket 对象，准备接收数据
        byte[] buf = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);

        //3. 调用接收方法，将通过网络传递过来的 DatagramPacket 对象 填充到 packet对象
        // 注意：没有数据包发送过来时，会在此阻塞等待
        System.out.println("接收端A 等待接收数据....");
        socket.receive(packet);

        //4. packet进行拆包，取出数据，并显示
        //实际接收到的数据字节长度
        int len = packet.getLength();
        //接收到数据
        byte[] data = packet.getData();
        String s = new String(data, 0, len);
        System.out.println(s);

        //5. 发送回复消息
        data = "好的，明天见！".getBytes();
        packet = new DatagramPacket(data, data.length, InetAddress.getByName("192.168.56.1"), 8888);
        socket.send(packet);


        //6. 关闭资源
        socket.close();
        System.out.println("A端退出....");

    }
}
