package ex2;

import java.io.*;
import java.net.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** HttpServer：服务端
 * 接收Http客户端的连接，支持GET和PUT请求，能够处理并发请求
 * @author Jing Yuemng
 * @version 1.0
 */
public class HttpServer {

    private static final int PORT = 8080;//服务器端口
    private static Path rootDirectory;//根目录
    private static ServerSocket serverSocket;

    /**
     * 启动服务器，监听连接
     * @param root
     */
    private static void start(String root){
        try {
            rootDirectory = Paths.get(root);
            serverSocket = new ServerSocket(PORT);
            System.out.println("服务器启动");
            //线程池
            final int POOL_SIZE=4;
            ExecutorService executor = Executors.newFixedThreadPool(POOL_SIZE);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                //调用线程
                executor.execute(new Handler(clientSocket, rootDirectory));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * main
     * 程序入口
     * 传递根目录参数，启动服务器
     * @param args
     */
    public static void main(String[] args) {
        //输入root目录参数,检验该目录是否有效，有效时启动服务器
        //检查是否传递参数
        if (args.length < 1){
            System.out.println("请传递根目录参数！");
            return;
        }
        //检查目录是否有效
        String rootDir = args[0];
        File dir = new File(rootDir);
        if (!dir.exists() || !dir.isDirectory()){
            System.out.println("根目录无效，请输入一个存在的目录！");
            return;
        }
        //启动服务器
        start(args[0]);
    }

}