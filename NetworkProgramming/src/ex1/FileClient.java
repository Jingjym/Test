package ex1;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.sql.SQLOutput;
import java.util.Scanner;

/** FileClient�ࣺ�ͻ���
 *  ���ӷ��������ڷ������ṩ�ĸ�Ŀ¼�½����ļ�����
 *  �ɽ�����ָ��Ϊ��
 * ls       ���������ص�ǰĿ¼�ļ��б�<file/dir>	name	size��
 * cd <dir>	����ָ��Ŀ¼
 * cd ..    ������һ��Ŀ¼���Ѿ��Ǹ�Ŀ¼�����䶯��
 * get <file>	ͨ��UDP����ָ���ļ������浽�ͻ��˵�ǰĿ¼��
 * bye	    �Ͽ����ӣ��ͻ����������
 *
 * @author Jing Yuemng
 * @version 1.0
 * �ͻ���
 */
public class FileClient {
    private DatagramSocket datagramSocket;
    private Socket socket;
    private String serverAddress;
    private int serverPort;
    private BufferedReader in;
    private BufferedWriter out;

    /**
     * ���캯��
     * @param serverAddress
     * @param serverPort
     */
    public FileClient(String serverAddress,int serverPort){
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    /**
     * ���ӷ�����
     */
    public void connect(){
        try {
            //��������
            socket = new Socket(serverAddress,serverPort);//tcp����
            datagramSocket = new DatagramSocket();//��udp

            //��tcp���������
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            //����udp�Ķ˿ں�
            out.write(Integer.toString(datagramSocket.getLocalPort()));
            out.newLine();
            out.flush();

            //���ղ���ʾ��Ϣ
            System.out.println(in.readLine());

        } catch (IOException e) {
            System.err.println("���������ip��ַ�Ƿ���ȷ�����߷������Ƿ��Ѿ��򿪣�");
            System.exit(1);
        }

    }

    /**
     * �����������ָ��
     * @return ����ָ��command
     */
    public String sendCommand(){
        String command = null;
        try {
            //�ӿ���̨����ָ��
            Scanner scanner = new Scanner(System.in);
            command = scanner.nextLine();
            //�����˷���ָ��
            out.write(command);
            out.newLine();//������־
            out.flush();

        } catch (IOException e) {
            System.err.println("���������ӶϿ���");
            System.exit(-1);
        }
        return command;
    }

    /**
     * ����������н�������������
     */
    public void interaction(){
        while (true){
            String command = sendCommand();//����ָ��
            if(command != null) {
                //����FTP����
                String[] list = command.split(" ");
                String key = list[0];
                switch (key) {
                    case "ls":
                        ls(command);//����ls()����������
                        break;
                    case "cd":
                        cd();//����cd()����������
                        break;
                    case "get":
                        get();//����get()����������
                        break;
                    case "bye":
                        bye(command);//����bye()����������
                        break;
                    default:
                        System.out.println("δ֪���" + command);
                        break;
                }
            }

        }
    }


    /**
     * lsָ����շ��������͵� ��ǰĿ¼�µ������ļ��б�
     * @param command �û������ָ��
     */
    public void ls(String command){
            if ("ls".equalsIgnoreCase(command)) { //��������Ƿ���Ч�������Ǵ�Сд
                try {
                    //��������
                    String s1 = in.readLine();
                    String s2 = s1.substring(1,s1.length()-1);

                    //��ʾ����
                    String[] fileList = s2.split(", ");
                    for (String file : fileList) {
                        System.out.println(file);
                    }
                } catch(IOException e) {
                    e.printStackTrace();
                }
        }
        else {
            System.out.println("������Ч����������ȷ��ʽ��ls ");
        }

    }

    /**
     * cdָ�����ָ��Ŀ¼
     */
    public void cd(){
        try {
            //��������
            String s = in.readLine();
            //��ʾ����
            System.out.println(s);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * getָ��ӷ��������ָ���ļ�
     */
    public void get() {
        //������ʾ��Ϣ
        String message = null;
        try {
            message = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //�ж���ʾ��Ϣ
        if (message.equals("OK")) {
            FileOutputStream fos = null;
            try {
                //�����ļ�·��
                String path = in.readLine();
                System.out.println("��ʼ�����ļ��� " + path);

                //�����ļ�����
                String s = in.readLine();
                long fileSize = Long.parseLong(s);

                //�����ļ�
                byte[] fileData;
                byte[] buffer = new byte[1024];
                int bufferSize = 1024;
                int receiveSize;//ÿ�ν��յ����ļ���ʵ�ʳ���
                try {
                    fos = new FileOutputStream(path, true);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                //ѭ�����շ���˷��͵�����
                for (int i = 0; i <= fileSize / bufferSize; i++) {
                    //����packet
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    datagramSocket.receive(packet);

                    //�յ�������д�뵽fileData
                    receiveSize = packet.getLength();
                    fileData = new byte[receiveSize];
                    System.arraycopy(packet.getData(), 0, fileData, 0, receiveSize);

                    //fos����fileDataд���ļ���
                    fos.write(fileData);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }finally {//�ر���
               if (fos != null){
                   try {
                       fos.close();
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
               }
            }
            System.out.println("�ļ��������");

        } else {
            System.out.println(message);
        }


    }

    /**
     * byeָ��Ͽ��������������
     * @param command �û������ָ��
     */
    public void bye(String command){
        if ("bye".equalsIgnoreCase(command)) { //��������Ƿ���Ч�������Ǵ�Сд
            try {
                in.close();
                out.close();
                socket.close();
                datagramSocket.close();
                System.out.println("�ͻ����˳���");
                System.exit(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            System.out.println("������Ч����������ȷ��ʽ��bye ");
        }
    }

    /**
     * �������
     * @param args
     */
    public static void main(String[] args){
        //���������ip��newһ��FileClient����
        System.out.println("�����������ip��ַ��");
        Scanner scanner = new Scanner(System.in);
        String ip = scanner.nextLine();
        FileClient client = new FileClient(ip, 2021);

        //���ӷ�����
        client.connect();

        //����������н���
        client.interaction();


    }
}
