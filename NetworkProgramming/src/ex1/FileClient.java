package ex1;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.sql.SQLOutput;
import java.util.Scanner;

/** FileClient类：客户端
 *  连接服务器，在服务器提供的根目录下进行文件操作
 *  可交互的指令为：
 * ls       服务器返回当前目录文件列表（<file/dir>	name	size）
 * cd <dir>	进入指定目录
 * cd ..    返回上一级目录（已经是根目录则不做变动）
 * get <file>	通过UDP下载指定文件，保存到客户端当前目录下
 * bye	    断开连接，客户端运行完毕
 *
 * @author Jing Yuemng
 * @version 1.0
 * 客户端
 */
public class FileClient {
    private DatagramSocket datagramSocket;
    private Socket socket;
    private String serverAddress;
    private int serverPort;
    private BufferedReader in;
    private BufferedWriter out;

    /**
     * 构造函数
     * @param serverAddress
     * @param serverPort
     */
    public FileClient(String serverAddress,int serverPort){
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    /**
     * 连接服务器
     */
    public void connect(){
        try {
            //建立连接
            socket = new Socket(serverAddress,serverPort);//tcp连接
            datagramSocket = new DatagramSocket();//打开udp

            //打开tcp输入输出流
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            //发送udp的端口号
            out.write(Integer.toString(datagramSocket.getLocalPort()));
            out.newLine();
            out.flush();

            //接收并显示信息
            System.out.println(in.readLine());

        } catch (IOException e) {
            System.err.println("请检查输入的ip地址是否正确，或者服务器是否已经打开！");
            System.exit(1);
        }

    }

    /**
     * 向服务器发送指令
     * @return 返回指令command
     */
    public String sendCommand(){
        String command = null;
        try {
            //从控制台读入指令
            Scanner scanner = new Scanner(System.in);
            command = scanner.nextLine();
            //向服务端发送指令
            out.write(command);
            out.newLine();//结束标志
            out.flush();

        } catch (IOException e) {
            System.err.println("服务器连接断开！");
            System.exit(-1);
        }
        return command;
    }

    /**
     * 与服务器进行交互，处理命令
     */
    public void interaction(){
        while (true){
            String command = sendCommand();//发送指令
            if(command != null) {
                //处理FTP命令
                String[] list = command.split(" ");
                String key = list[0];
                switch (key) {
                    case "ls":
                        ls(command);//调用ls()函数来处理
                        break;
                    case "cd":
                        cd();//调用cd()函数来处理
                        break;
                    case "get":
                        get();//调用get()函数来处理
                        break;
                    case "bye":
                        bye(command);//调用bye()函数来处理
                        break;
                    default:
                        System.out.println("未知命令：" + command);
                        break;
                }
            }

        }
    }


    /**
     * ls指令：接收服务器发送的 当前目录下的所有文件列表
     * @param command 用户输入的指令
     */
    public void ls(String command){
            if ("ls".equalsIgnoreCase(command)) { //检查命令是否有效，不考虑大小写
                try {
                    //接收数据
                    String s1 = in.readLine();
                    String s2 = s1.substring(1,s1.length()-1);

                    //显示数据
                    String[] fileList = s2.split(", ");
                    for (String file : fileList) {
                        System.out.println(file);
                    }
                } catch(IOException e) {
                    e.printStackTrace();
                }
        }
        else {
            System.out.println("命令无效，请输入正确形式：ls ");
        }

    }

    /**
     * cd指令：进入指定目录
     */
    public void cd(){
        try {
            //接收数据
            String s = in.readLine();
            //显示数据
            System.out.println(s);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * get指令：从服务端下载指定文件
     */
    public void get() {
        //接收提示信息
        String message = null;
        try {
            message = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //判断提示信息
        if (message.equals("OK")) {
            FileOutputStream fos = null;
            try {
                //接收文件路径
                String path = in.readLine();
                System.out.println("开始接收文件： " + path);

                //接收文件长度
                String s = in.readLine();
                long fileSize = Long.parseLong(s);

                //接收文件
                byte[] fileData;
                byte[] buffer = new byte[1024];
                int bufferSize = 1024;
                int receiveSize;//每次接收到的文件的实际长度
                try {
                    fos = new FileOutputStream(path, true);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                //循环接收服务端发送的数据
                for (int i = 0; i <= fileSize / bufferSize; i++) {
                    //接收packet
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    datagramSocket.receive(packet);

                    //收到的数据写入到fileData
                    receiveSize = packet.getLength();
                    fileData = new byte[receiveSize];
                    System.arraycopy(packet.getData(), 0, fileData, 0, receiveSize);

                    //fos流将fileData写入文件中
                    fos.write(fileData);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }finally {//关闭流
               if (fos != null){
                   try {
                       fos.close();
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
               }
            }
            System.out.println("文件接收完毕");

        } else {
            System.out.println(message);
        }


    }

    /**
     * bye指令：断开与服务器的连接
     * @param command 用户输入的指令
     */
    public void bye(String command){
        if ("bye".equalsIgnoreCase(command)) { //检查命令是否有效，不考虑大小写
            try {
                in.close();
                out.close();
                socket.close();
                datagramSocket.close();
                System.out.println("客户端退出！");
                System.exit(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            System.out.println("命令无效，请输入正确形式：bye ");
        }
    }

    /**
     * 程序入口
     * @param args
     */
    public static void main(String[] args){
        //输入服务器ip，new一个FileClient对象
        System.out.println("请输入服务器ip地址：");
        Scanner scanner = new Scanner(System.in);
        String ip = scanner.nextLine();
        FileClient client = new FileClient(ip, 2021);

        //连接服务器
        client.connect();

        //与服务器进行交互
        client.interaction();


    }
}
