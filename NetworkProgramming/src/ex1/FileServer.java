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

/** FileServer类：服务端
 *  接收客户端的连接，提供根目录供客户端访问操作
 * @author Jing Yuemng
 * @version 1.0
 */

public class FileServer {
    private ServerSocket serverSocket;
    private DatagramSocket datagramSocket;
    private String root;
    private ExecutorService executor;


    /**
     * 构造函数
     * @param root 传递根目录
     */
    public FileServer(String root) {
        this.root = root;
        this.executor = Executors.newFixedThreadPool(5);//创建线程池
    }

    /**
     * 获取根目录root
     * @return
     */
    public String getRoot(){
        return this.root;
    }

    /***
     *启动服务端程序
     */
    public void start(){
        try {
            serverSocket = new ServerSocket(2021);
            datagramSocket = new DatagramSocket(2020);
            System.out.println("服务器启动");
            while (true) {
                //接收连接
                Socket socket = serverSocket.accept();
                //调用线程
                executor.execute(new ClientHandler(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * ClientHandler类
     * 处理客户线程
     */
    class ClientHandler extends Thread {
        private Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                //接收客户端udp端口号
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                int clientPort = Integer.parseInt(in.readLine());

                //回复信息
                String message = socket.getInetAddress().getHostAddress() + ":" + socket.getPort() + ">连接成功";
                sendMessage(socket, message);
                System.out.println(message);

                //接收并处理客户指令
                commandProcess(clientPort);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * 接收客户指令，根据指令调用相应函数处理
         * @param clientPort 客户端udp端口号
         * @throws IOException
         */
        public void commandProcess(int clientPort){
            try {
                //记录当前目录
                String currentDir = getRoot();

                //接收并处理客户指令
                Boolean flag = true;
                while (flag) {
                    //接收客户指令
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String command = in.readLine();
                    if (command != null) {
                        //处理客户指令
                        String[] list = command.split(" ");
                        String key = list[0];
                        switch (key) {
                            case "ls":
                                ls(socket, command, currentDir);//调用ls()函数来处理
                                break;
                            case "cd":
                                currentDir = cd(socket, command, currentDir);//调用cd()函数来处理
                                break;
                            case "get":
                                get(socket, command, clientPort, currentDir);//调用get()函数来处理
                                break;
                            case "bye":
                                flag = !bye(socket, command);//调用bye()函数来处理
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
     * 用TCP向客户端发送交互信息
     * @param socket 客户端socket
     * @param message 发送的信息
     */
    public void sendMessage(Socket socket,String message){
        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            out.write(message);
            out.newLine();//结束标志
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取文件夹的长度
     * @param folder 需要获取长度的文件夹
     * @return
     */
    public static long getFolderSize(File folder){
        long size = 0;

        if (folder != null){
            File[] files = folder.listFiles();//获取文件列表
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
     * 根据路径获取 当前目录下的所有文件列表
     * @param path 路径
     * @return 返回文件列表
     */
    private static List<String> getFiles(String path){
        List<String> fileList = new ArrayList<>();
        File dir = new File(path);
        if (dir.isDirectory()){
            File[] dirFiles = dir.listFiles();//获取文件列表
            if(dirFiles != null){
                //设计显示格式
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
     * ls指令：向客户端发送 当前目录下的所有文件列表
     * @param socket 客户端的socket
     * @param currentDir 当前目录
     */
    public void ls(Socket socket,String command,String currentDir){
        if ("ls".equalsIgnoreCase(command)) { //检查命令是否有效，不考虑大小写
            //获取文件列表
            List<String> fileList = getFiles(currentDir);
            sendMessage(socket,fileList.toString());
        }

    }

    /**
     * cd指令：进入指定目录
     * @param socket 客户端的socket
     * @param command 客户的指令
     * @param currentDir 当前目录
     * @return 返回最新的当前目录
     */
    public String cd(Socket socket,String command,String currentDir){
        //发送给客户端的数据
        String message = "命令无效，请输入正确形式：cd <dir> 或者 cd .. ";
        //分解指令
        String[] str = command.split(" ");
        //获取文件名
        String fileName = str[1];
        for (int i = 2;i < str.length;i++){
            fileName += " ";
            fileName += str[i];
        }

        if (str.length >= 2) {
            //如果是命令cd ..
            if (fileName.equals("..")) {
                //当前目录已经是根目录时，不做变动
                if (currentDir.equals(root)) {
                    message = root + " > OK";
                }

                //当前目录不是根目录时：
                else {
                    //创建一个表示当前目录的父目录的File对象
                    File parentDir = new File(currentDir).getParentFile();
                    //切换路径
                    currentDir = parentDir.getAbsolutePath();
                    message = currentDir + " > OK";
                }

            }
            //如果是命令cd ../..
            else if (fileName.equals("../..")) {
                //当前目录已经是根目录时，不做变动
                if (currentDir.equals(root)) {
                    message = root + " > OK";
                }
                //当前目录不是根目录时：
                else {
                    //创建一个表示当前目录的父目录的File对象
                    File parentDir = new File(currentDir).getParentFile();
                    File grandparentDir = parentDir.getParentFile();

                    //如果父目录为根目录
                    if (parentDir.getAbsolutePath().equals(root)) {
                        message = root + " > OK";
                        currentDir = root;
                    } else {
                        currentDir = grandparentDir.getAbsolutePath();
                        message = currentDir + " > OK";
                    }

                }
            }

            //如果是命令 cd <dir>
            else {
                //判断文件是否存在
                Boolean flag = false;
                File f = new File(currentDir);
                File[] dirFiles = f.listFiles();//获取当前文件列表
                for (File file : dirFiles) {
                    if (file.getName().equals(fileName)) {
                        flag = true;
                        break;
                    }
                }

                String path = currentDir + "\\" + fileName;//获取dir的路径
                File targetFile = new File(path);//创建一个表示dir的File对象

                //当targetFile存在 且 属于文件夹类型
                if (flag && targetFile.exists() && targetFile.isDirectory()) {
                    currentDir = path;//切换路径
                    message = currentDir + " > OK";
                } else {
                    message = "切换失败，请检查路径正确性！";
                }
            }
        }

        //发送数据
        sendMessage(socket,message);
        return currentDir;

    }

    /**
     * get指令：发送客户端指定文件
     * @param socket 客户端的socket
     * @param command 客户端指令
     * @param clientPort 客户端udp端口
     * @param currentDir 当前目录
     */
    public void get(Socket socket,String command,int clientPort,String currentDir) {
        //发送给客户端的提示信息
        String message;
        //分解指令
        String[] str = command.split(" ");
        //获取文件名
        String fileName = str[1];
        for (int i = 2;i < str.length;i++){
            fileName += " ";
            fileName += str[i];
        }

        //检查命令格式
        //格式正确时：
        if (str.length >= 2) {
            File file = new File(currentDir + "\\" + fileName);

            //file存在 且 属于普通文件文件
            if (file.exists() && file.isFile()) {

                    //发送提示信息
                    message = "OK";
                    sendMessage(socket, message);

                    //发送该文件的路径和大小
                    //这里由于服务端与客户端在同一主机，为了能成功看到文件的下载，此处传输的文件路径为文件名
                    String path = fileName;
                    String size = Long.toString(file.length());
                    sendMessage(socket, path);
                    sendMessage(socket, size);

                    //发送文件数据
                    byte[] buffer;//用于承载发送的文件数据
                    int bufferSize = 1024;//buffer的最大长度
                    long fileSize = file.length();//文件长度
                    long unsentSize = fileSize;//还未发送的文件长度
                    FileInputStream fis = null;
                    try {
                        fis = new FileInputStream(file);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                try {
                    //文件以bufferSize为单位，分段发送
                    for (int i = 0; i <= fileSize / bufferSize; i++) {
                        buffer = new byte[(int) Math.min(unsentSize, bufferSize)];//确定buffer实际大小
                        fis.read(buffer);//将文件读入buffer

                        //发送
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, socket.getInetAddress(), clientPort);
                        datagramSocket.send(packet);
                        try {
                            TimeUnit.MICROSECONDS.sleep(1);//限制发送速度
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //更新unsentSize
                        unsentSize -= bufferSize;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {//关闭流
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
                message = "请检查文件是否存在，或是否为普通文件！";
            }
        }
        //格式不正确时：
        else {
            message = "命令无效，请输入正确形式：get <file>";
        }
        //该命令无法执行时，返回相应提示信息
        sendMessage(socket, message);

    }

    /**
     * 断开与客户端的连接
     * @param socket 客户端的socket
     * @return 连接是否断开
     */
    public Boolean bye(Socket socket,String command){
        if ("bye".equalsIgnoreCase(command)) { //检查命令是否有效，不考虑大小写
            try {
                System.out.println(socket.getInetAddress().getHostAddress() + ":" + socket.getPort() + ">连接断开");
                socket.close();//关闭连接
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    /**
     * 程序入口，启动时需要传入参数“根目录”
     * @param args
     */
    public static void main(String[] args){
        //输入root目录参数,检验该目录是否有效，有效时创建一个FileServer对象
        //检查是否传递参数
        if (args.length < 1){
            System.out.println("请传递根目录参数！");
            return;
        }
        //检查目录是否有效
        String rootDirectory = args[0];
        File dir = new File(rootDirectory);
        if (!dir.exists() || !dir.isDirectory()){
            System.out.println("根目录无效，请输入一个存在的目录！");
            return;
        }

        //创建FileServer对象
        FileServer server = new FileServer(rootDirectory);

        //启动服务端
        server.start();


    }

}
