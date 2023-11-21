package ex1;

import java.io.*;
import java.net.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/** FileServer�ࣺ�����
 *  ���տͻ��˵����ӣ��ṩ��Ŀ¼���ͻ��˷��ʲ���
 * @author Jing Yuemng
 * @version 1.0
 */

public class FileServer {
    private ServerSocket serverSocket;
    private DatagramSocket datagramSocket;
    private String root;
    private ExecutorService executor;


    /**
     * ���캯��
     * @param root ���ݸ�Ŀ¼
     */
    public FileServer(String root) {
        this.root = root;
        this.executor = Executors.newFixedThreadPool(5);//�����̳߳�
    }

    /**
     * ��ȡ��Ŀ¼root
     * @return
     */
    public String getRoot(){
        return this.root;
    }

    /***
     *��������˳���
     */
    public void start(){
        try {
            serverSocket = new ServerSocket(2021);
            datagramSocket = new DatagramSocket(2020);
            System.out.println("����������");
            while (true) {
                //��������
                Socket socket = serverSocket.accept();
                //�����߳�
                executor.execute(new ClientHandler(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * ClientHandler��
     * ����ͻ��߳�
     */
    class ClientHandler extends Thread {
        private Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                //���տͻ���udp�˿ں�
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                int clientPort = Integer.parseInt(in.readLine());

                //�ظ���Ϣ
                String message = socket.getInetAddress().getHostAddress() + ":" + socket.getPort() + ">���ӳɹ�";
                sendMessage(socket, message);
                System.out.println(message);

                //���ղ�����ͻ�ָ��
                commandProcess(clientPort);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * ���տͻ�ָ�����ָ�������Ӧ��������
         * @param clientPort �ͻ���udp�˿ں�
         * @throws IOException
         */
        public void commandProcess(int clientPort){
            try {
                //��¼��ǰĿ¼
                String currentDir = getRoot();

                //���ղ�����ͻ�ָ��
                Boolean flag = true;
                while (flag) {
                    //���տͻ�ָ��
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String command = in.readLine();
                    if (command != null) {
                        //����ͻ�ָ��
                        String[] list = command.split(" ");
                        String key = list[0];
                        switch (key) {
                            case "ls":
                                ls(socket, command, currentDir);//����ls()����������
                                break;
                            case "cd":
                                currentDir = cd(socket, command, currentDir);//����cd()����������
                                break;
                            case "get":
                                get(socket, command, clientPort, currentDir);//����get()����������
                                break;
                            case "bye":
                                flag = !bye(socket, command);//����bye()����������
                                break;
                            default:
                                break;
                        }
                    }
                }
            } catch (IOException e) {
                bye(socket,"bye");
            }
        }

    }



    /**
     * ��TCP��ͻ��˷��ͽ�����Ϣ
     * @param socket �ͻ���socket
     * @param message ���͵���Ϣ
     */
    public void sendMessage(Socket socket,String message){
        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            out.write(message);
            out.newLine();//������־
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * ��ȡ�ļ��еĳ���
     * @param folder ��Ҫ��ȡ���ȵ��ļ���
     * @return
     */
    public static long getFolderSize(File folder){
        long size = 0;

        if (folder != null){
            File[] files = folder.listFiles();//��ȡ�ļ��б�
            for(File file:files){
                if (file.isDirectory()){
                    size += getFolderSize(file);
                }
                else size += file.length();
            }
            return size;
        }
        else return size;
    }

    /**
     * ����·����ȡ ��ǰĿ¼�µ������ļ��б�
     * @param path ·��
     * @return �����ļ��б�
     */
    private static List<String> getFiles(String path){
        List<String> fileList = new ArrayList<>();
        File dir = new File(path);
        if (dir.isDirectory()){
            File[] dirFiles = dir.listFiles();//��ȡ�ļ��б�
            if(dirFiles != null){
                //�����ʾ��ʽ
                for(File file:dirFiles){
                    if (file.isDirectory()){
                        fileList.add("<dir>\t" + file.getName() + "\t" + getFolderSize(file));
                    }
                    else {
                        fileList.add("<file>\t" + file.getName() + "\t" + file.length());
                    }

                }
            }
        }
        return fileList;
    }


    /**
     * lsָ���ͻ��˷��� ��ǰĿ¼�µ������ļ��б�
     * @param socket �ͻ��˵�socket
     * @param currentDir ��ǰĿ¼
     */
    public void ls(Socket socket,String command,String currentDir){
        if ("ls".equalsIgnoreCase(command)) { //��������Ƿ���Ч�������Ǵ�Сд
            //��ȡ�ļ��б�
            List<String> fileList = getFiles(currentDir);
            sendMessage(socket,fileList.toString());
        }

    }

    /**
     * cdָ�����ָ��Ŀ¼
     * @param socket �ͻ��˵�socket
     * @param command �ͻ���ָ��
     * @param currentDir ��ǰĿ¼
     * @return �������µĵ�ǰĿ¼
     */
    public String cd(Socket socket,String command,String currentDir){
        //���͸��ͻ��˵�����
        String message = "������Ч����������ȷ��ʽ��cd <dir> ���� cd .. ";
        //�ֽ�ָ��
        String[] str = command.split(" ");
        //��ȡ�ļ���
        String fileName = str[1];
        for (int i = 2;i < str.length;i++){
            fileName += " ";
            fileName += str[i];
        }

        if (str.length >= 2) {
            //���������cd ..
            if (fileName.equals("..")) {
                //��ǰĿ¼�Ѿ��Ǹ�Ŀ¼ʱ�������䶯
                if (currentDir.equals(root)) {
                    message = root + " > OK";
                }

                //��ǰĿ¼���Ǹ�Ŀ¼ʱ��
                else {
                    //����һ����ʾ��ǰĿ¼�ĸ�Ŀ¼��File����
                    File parentDir = new File(currentDir).getParentFile();
                    //�л�·��
                    currentDir = parentDir.getAbsolutePath();
                    message = currentDir + " > OK";
                }

            }
            //���������cd ../..
            else if (fileName.equals("../..")) {
                //��ǰĿ¼�Ѿ��Ǹ�Ŀ¼ʱ�������䶯
                if (currentDir.equals(root)) {
                    message = root + " > OK";
                }
                //��ǰĿ¼���Ǹ�Ŀ¼ʱ��
                else {
                    //����һ����ʾ��ǰĿ¼�ĸ�Ŀ¼��File����
                    File parentDir = new File(currentDir).getParentFile();
                    File grandparentDir = parentDir.getParentFile();

                    //�����Ŀ¼Ϊ��Ŀ¼
                    if (parentDir.getAbsolutePath().equals(root)) {
                        message = root + " > OK";
                        currentDir = root;
                    } else {
                        currentDir = grandparentDir.getAbsolutePath();
                        message = currentDir + " > OK";
                    }

                }
            }

            //��������� cd <dir>
            else {
                //�ж��ļ��Ƿ����
                Boolean flag = false;
                File f = new File(currentDir);
                File[] dirFiles = f.listFiles();//��ȡ��ǰ�ļ��б�
                for (File file : dirFiles) {
                    if (file.getName().equals(fileName)) {
                        flag = true;
                        break;
                    }
                }

                String path = currentDir + "\\" + fileName;//��ȡdir��·��
                File targetFile = new File(path);//����һ����ʾdir��File����

                //��targetFile���� �� �����ļ�������
                if (flag && targetFile.exists() && targetFile.isDirectory()) {
                    currentDir = path;//�л�·��
                    message = currentDir + " > OK";
                } else {
                    message = "�л�ʧ�ܣ�����·����ȷ�ԣ�";
                }
            }
        }

        //��������
        sendMessage(socket,message);
        return currentDir;

    }

    /**
     * getָ����Ϳͻ���ָ���ļ�
     * @param socket �ͻ��˵�socket
     * @param command �ͻ���ָ��
     * @param clientPort �ͻ���udp�˿�
     * @param currentDir ��ǰĿ¼
     */
    public void get(Socket socket,String command,int clientPort,String currentDir) {
        //���͸��ͻ��˵���ʾ��Ϣ
        String message;
        //�ֽ�ָ��
        String[] str = command.split(" ");
        //��ȡ�ļ���
        String fileName = str[1];
        for (int i = 2;i < str.length;i++){
            fileName += " ";
            fileName += str[i];
        }

        //��������ʽ
        //��ʽ��ȷʱ��
        if (str.length >= 2) {
            File file = new File(currentDir + "\\" + fileName);

            //file���� �� ������ͨ�ļ��ļ�
            if (file.exists() && file.isFile()) {

                    //������ʾ��Ϣ
                    message = "OK";
                    sendMessage(socket, message);

                    //���͸��ļ���·���ʹ�С
                    //�������ڷ������ͻ�����ͬһ������Ϊ���ܳɹ������ļ������أ��˴�������ļ�·��Ϊ�ļ���
                    String path = fileName;
                    String size = Long.toString(file.length());
                    sendMessage(socket, path);
                    sendMessage(socket, size);

                    //�����ļ�����
                    byte[] buffer;//���ڳ��ط��͵��ļ�����
                    int bufferSize = 1024;//buffer����󳤶�
                    long fileSize = file.length();//�ļ�����
                    long unsentSize = fileSize;//��δ���͵��ļ�����
                    FileInputStream fis = null;
                    try {
                        fis = new FileInputStream(file);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                try {
                    //�ļ���bufferSizeΪ��λ���ֶη���
                    for (int i = 0; i <= fileSize / bufferSize; i++) {
                        buffer = new byte[(int) Math.min(unsentSize, bufferSize)];//ȷ��bufferʵ�ʴ�С
                        fis.read(buffer);//���ļ�����buffer

                        //����
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, socket.getInetAddress(), clientPort);
                        datagramSocket.send(packet);
                        try {
                            TimeUnit.MICROSECONDS.sleep(1);//���Ʒ����ٶ�
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //����unsentSize
                        unsentSize -= bufferSize;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {//�ر���
                    if (fis != null ){
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return;
            } else {
                message = "�����ļ��Ƿ���ڣ����Ƿ�Ϊ��ͨ�ļ���";
            }
        }
        //��ʽ����ȷʱ��
        else {
            message = "������Ч����������ȷ��ʽ��get <file>";
        }
        //�������޷�ִ��ʱ��������Ӧ��ʾ��Ϣ
        sendMessage(socket, message);

    }

    /**
     * �Ͽ���ͻ��˵�����
     * @param socket �ͻ��˵�socket
     * @return �����Ƿ�Ͽ�
     */
    public Boolean bye(Socket socket,String command){
        if ("bye".equalsIgnoreCase(command)) { //��������Ƿ���Ч�������Ǵ�Сд
            try {
                System.out.println(socket.getInetAddress().getHostAddress() + ":" + socket.getPort() + ">���ӶϿ�");
                socket.close();//�ر�����
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    /**
     * ������ڣ�����ʱ��Ҫ�����������Ŀ¼��
     * @param args
     */
    public static void main(String[] args){
        //����rootĿ¼����,�����Ŀ¼�Ƿ���Ч����Чʱ����һ��FileServer����
        //����Ƿ񴫵ݲ���
        if (args.length < 1){
            System.out.println("�봫�ݸ�Ŀ¼������");
            return;
        }
        //���Ŀ¼�Ƿ���Ч
        String rootDirectory = args[0];
        File dir = new File(rootDirectory);
        if (!dir.exists() || !dir.isDirectory()){
            System.out.println("��Ŀ¼��Ч��������һ�����ڵ�Ŀ¼��");
            return;
        }

        //����FileServer����
        FileServer server = new FileServer(rootDirectory);

        //���������
        server.start();


    }

}
