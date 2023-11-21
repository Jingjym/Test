package ex2;

import java.io.*;
import java.net.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** HttpServer�������
 * ����Http�ͻ��˵����ӣ�֧��GET��PUT�����ܹ�����������
 * @author Jing Yuemng
 * @version 1.0
 */
public class HttpServer {

    private static final int PORT = 8080;//�������˿�
    private static Path rootDirectory;//��Ŀ¼
    private static ServerSocket serverSocket;

    /**
     * ��������������������
     * @param root
     */
    private static void start(String root){
        try {
            rootDirectory = Paths.get(root);
            serverSocket = new ServerSocket(PORT);
            System.out.println("����������");
            //�̳߳�
            final int POOL_SIZE=4;
            ExecutorService executor = Executors.newFixedThreadPool(POOL_SIZE);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                //�����߳�
                executor.execute(new Handler(clientSocket, rootDirectory));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * main
     * �������
     * ���ݸ�Ŀ¼����������������
     * @param args
     */
    public static void main(String[] args) {
        //����rootĿ¼����,�����Ŀ¼�Ƿ���Ч����Чʱ����������
        //����Ƿ񴫵ݲ���
        if (args.length < 1){
            System.out.println("�봫�ݸ�Ŀ¼������");
            return;
        }
        //���Ŀ¼�Ƿ���Ч
        String rootDir = args[0];
        File dir = new File(rootDir);
        if (!dir.exists() || !dir.isDirectory()){
            System.out.println("��Ŀ¼��Ч��������һ�����ڵ�Ŀ¼��");
            return;
        }
        //����������
        start(args[0]);
    }

}