package ex2;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Handler
 * 处理客户线程,针对每个客户实现GET和PUT请求处理
 * @author Jing Yuemng
 * @version 1.0
 */
public class Handler extends Thread {
    private Socket clientSocket;//客户端口
    private Path rootDirectory;//根目录

    static private String CRLF = "\r\n";

    private BufferedInputStream bi;
    private BufferedOutputStream bo;

    /**
     * 构造函数
     * @param clientSocket
     * @param rootDirectory
     */
    public Handler(Socket clientSocket,Path rootDirectory) throws IOException {
        this.clientSocket = clientSocket;
        this.rootDirectory = rootDirectory;
        bi = new  BufferedInputStream(clientSocket.getInputStream());
        bo = new BufferedOutputStream(clientSocket.getOutputStream());
    }

    /**
     * 线程入口
     * 根据用户不同请求调用不同方法处理
     */
    public void run() {
        try {
            //接收客户端请求头部，将其放在header中
            int c = 0;
            int last = 0;
            String header = "";
            boolean inHeader = true; // loop control

            while (inHeader && ((c = bi.read()) != -1)) {
                switch (c) {
                    case '\r':
                        break;
                    case '\n':
                        if (c == last) {
                            inHeader = false;
                            break;
                        }
                        last = c;
                        header += "\n";
                        break;
                    default:
                        last = c;
                        header += (char) c;
                }
            }

            //分割请求信息
            String[] line = header.split("\n");
            String[] requests = line[0].split(" ");

            //请求格式错误，返回400
            if (requests.length < 3){
                header = "HTTP/1.1" + " 400 Bad Request" + CRLF + CRLF;
                bo.write(header.getBytes(), 0, header.length());
                bo.flush();
                clientSocket.shutdownOutput();//结束标志
            }
            else {
                String method = requests[0];//请求方法

                String fileName = requests[1];
                for (int i = 2;i < requests.length-1;i++){
                    fileName += " ";
                    fileName += requests[i];
                }
                String path = rootDirectory + fileName;//请求路径

                String version = requests[requests.length-1];//HTTP版本

                if (method.equalsIgnoreCase("GET")) {
                    get(path, version);
                } else if (method.equalsIgnoreCase("PUT")) {
                    try {
                        put(path, version);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {//如果是不支持的方法
                    header = version + " 501 Not Implemented" + CRLF + CRLF;
                    byte[] buffer = header.getBytes();
                    bo.write(buffer, 0, header.length());
                    bo.flush();
                }
            }
        }catch (IOException e){
            System.out.println("连接中断！");
        }
    }

    /**
     * 处理GET请求
     * @param path
     * @param version
     * @throws IOException
     */
    private void get(String path,String version) throws IOException {
        byte[] buffer;
        StringBuffer header = new StringBuffer();
        File targetFile = new File(path);
        int flag = 0;

        //判断文件是否存在
        File parentDir = targetFile.getParentFile();//父目录
        File[] dirFiles = parentDir.listFiles();//获取父目录文件列表
        for (File file : dirFiles) {
            if (file.getName().equals(targetFile.getName())) {
                flag = 1;
                break;
            }
        }

        //当文件不存在时，返回错误信息
        if(flag == 0 || targetFile.isDirectory()) {
            header.append(version + " 404 Not Found" + CRLF + CRLF);
            buffer = header.toString().getBytes();
            bo.write(buffer, 0, header.toString().length());
            bo.flush();
            clientSocket.shutdownOutput();//结束标志
        }

        //当文件存在时，发送头部和文件
        else {
            Long len = targetFile.length();//文件长度
            byte[] files;
            String type = Files.probeContentType(targetFile.toPath());//文件类型
            //构造头部信息
            header.append(version + " 200 OK" + CRLF);
            header.append("Server: HttpServer/1.0" + CRLF);
            header.append("Content-type: " + type + CRLF);
            header.append("Content-length: " + len + CRLF + CRLF);
            //发送头部信息
            buffer = header.toString().getBytes();
            bo.write(buffer, 0, header.toString().length());
            bo.flush();

            //发送文件
            files = Files.readAllBytes(targetFile.toPath());
            bo.write(files, 0, files.length);
            bo.flush();
            clientSocket.shutdownOutput();//结束标志
        }
    }

    /**
     * 处理PUT请求
     * @param path
     * @param version
     * @throws Exception
     */
    private void put(String path,String version) throws Exception {
        StringBuffer response = new StringBuffer();
        StringBuffer header = new StringBuffer();
        boolean update = false;

        String[] s = path.split("/");
        path = rootDirectory + "\\" + s[s.length-1];

        File file = new File(path);
        if (file.exists()) update = true;
        FileOutputStream fos = new FileOutputStream(path);
        byte[] buffer = new byte[1024];

        //接收文件
        int d = 0,tem = 0,i = 0;
        while ((d = bi.read(buffer)) != -1) {
            if(i == 0) {
                tem = d;
                i = 1;
            }
            response.append(new String(buffer,"iso-8859-1"));
            if(tem != d) {
                break;
            }
        }

        //保存文件
        String txt = response.toString();
        fos.write(txt.getBytes("iso-8859-1"));
        fos.flush();
        fos.close();


        int len = (int) file.length();//文件长度
        String type = Files.probeContentType(file.toPath());//文件类型

        //如果是图片类型，则嵌入到html页面中
        if (type.equals("image/jpeg") || type.equals("image/jpg")) {
            String fileName = file.getName();
            //获取图片的名称
            String[] split = fileName.split("\\.");
            String photo = split[0];
            //构造html页面
            String content = "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "    <meta charset=\"utf-8\">\n" +
                    "    <title>分布式练习2测试页面</title>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "<center>\n" +
                    "    <h1>分布式练习2测试图片页面</h1>\n" +
                    "    <img alt=\"" + photo + "\"" + " src=\""+ fileName + "\" width=\"480\" height=\"480\">\n" +
                    "</center>\n" +
                    "</body>\n" +
                    "</html>\n";
            //写文件
            file = new File(rootDirectory + "/" + photo+ ".html");
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8));
            writer.write(content);
            writer.close();

            //更新文件类型和长度
            type = "text/html";
            len = (int)file.length();
        }

        //构造返回的头部信息
        if (update){
            header.append(version+ " 205 Reset Content\n");
            header.append("Server: HttpServer/1.1\n");
            header.append("Content-type: " + type +"\n");
            header.append("Content-length: " + len);
            header.append(CRLF);
            header.append(CRLF);
        }
        else{
            header.append(version+ " 201 Created\n");
            header.append("Server: HttpServer/1.0\n");
            header.append("Content-type: " + type +"\n");
            header.append("Content-length: " + len);
            header.append(CRLF);
            header.append(CRLF);
        }

        //发送返回信息
        buffer = header.toString().getBytes();
        bo.write(buffer, 0, header.toString().length());
        bo.flush();
        clientSocket.shutdownOutput();//结束标志
    }

}