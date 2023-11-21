package ex2;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Handler
 * ����ͻ��߳�,���ÿ���ͻ�ʵ��GET��PUT������
 * @author Jing Yuemng
 * @version 1.0
 */
public class Handler extends Thread {
    private Socket clientSocket;//�ͻ��˿�
    private Path rootDirectory;//��Ŀ¼

    static private String CRLF = "\r\n";

    private BufferedInputStream bi;
    private BufferedOutputStream bo;

    /**
     * ���캯��
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
     * �߳����
     * �����û���ͬ������ò�ͬ��������
     */
    public void run() {
        try {
            //���տͻ�������ͷ�����������header��
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

            //�ָ�������Ϣ
            String[] line = header.split("\n");
            String[] requests = line[0].split(" ");

            //�����ʽ���󣬷���400
            if (requests.length < 3){
                header = "HTTP/1.1" + " 400 Bad Request" + CRLF + CRLF;
                bo.write(header.getBytes(), 0, header.length());
                bo.flush();
                clientSocket.shutdownOutput();//������־
            }
            else {
                String method = requests[0];//���󷽷�

                String fileName = requests[1];
                for (int i = 2;i < requests.length-1;i++){
                    fileName += " ";
                    fileName += requests[i];
                }
                String path = rootDirectory + fileName;//����·��

                String version = requests[requests.length-1];//HTTP�汾

                if (method.equalsIgnoreCase("GET")) {
                    get(path, version);
                } else if (method.equalsIgnoreCase("PUT")) {
                    try {
                        put(path, version);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {//����ǲ�֧�ֵķ���
                    header = version + " 501 Not Implemented" + CRLF + CRLF;
                    byte[] buffer = header.getBytes();
                    bo.write(buffer, 0, header.length());
                    bo.flush();
                }
            }
        }catch (IOException e){
            System.out.println("�����жϣ�");
        }
    }

    /**
     * ����GET����
     * @param path
     * @param version
     * @throws IOException
     */
    private void get(String path,String version) throws IOException {
        byte[] buffer;
        StringBuffer header = new StringBuffer();
        File targetFile = new File(path);
        int flag = 0;

        //�ж��ļ��Ƿ����
        File parentDir = targetFile.getParentFile();//��Ŀ¼
        File[] dirFiles = parentDir.listFiles();//��ȡ��Ŀ¼�ļ��б�
        for (File file : dirFiles) {
            if (file.getName().equals(targetFile.getName())) {
                flag = 1;
                break;
            }
        }

        //���ļ�������ʱ�����ش�����Ϣ
        if(flag == 0 || targetFile.isDirectory()) {
            header.append(version + " 404 Not Found" + CRLF + CRLF);
            buffer = header.toString().getBytes();
            bo.write(buffer, 0, header.toString().length());
            bo.flush();
            clientSocket.shutdownOutput();//������־
        }

        //���ļ�����ʱ������ͷ�����ļ�
        else {
            Long len = targetFile.length();//�ļ�����
            byte[] files;
            String type = Files.probeContentType(targetFile.toPath());//�ļ�����
            //����ͷ����Ϣ
            header.append(version + " 200 OK" + CRLF);
            header.append("Server: HttpServer/1.0" + CRLF);
            header.append("Content-type: " + type + CRLF);
            header.append("Content-length: " + len + CRLF + CRLF);
            //����ͷ����Ϣ
            buffer = header.toString().getBytes();
            bo.write(buffer, 0, header.toString().length());
            bo.flush();

            //�����ļ�
            files = Files.readAllBytes(targetFile.toPath());
            bo.write(files, 0, files.length);
            bo.flush();
            clientSocket.shutdownOutput();//������־
        }
    }

    /**
     * ����PUT����
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

        //�����ļ�
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

        //�����ļ�
        String txt = response.toString();
        fos.write(txt.getBytes("iso-8859-1"));
        fos.flush();
        fos.close();


        int len = (int) file.length();//�ļ�����
        String type = Files.probeContentType(file.toPath());//�ļ�����

        //�����ͼƬ���ͣ���Ƕ�뵽htmlҳ����
        if (type.equals("image/jpeg") || type.equals("image/jpg")) {
            String fileName = file.getName();
            //��ȡͼƬ������
            String[] split = fileName.split("\\.");
            String photo = split[0];
            //����htmlҳ��
            String content = "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "    <meta charset=\"utf-8\">\n" +
                    "    <title>�ֲ�ʽ��ϰ2����ҳ��</title>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "<center>\n" +
                    "    <h1>�ֲ�ʽ��ϰ2����ͼƬҳ��</h1>\n" +
                    "    <img alt=\"" + photo + "\"" + " src=\""+ fileName + "\" width=\"480\" height=\"480\">\n" +
                    "</center>\n" +
                    "</body>\n" +
                    "</html>\n";
            //д�ļ�
            file = new File(rootDirectory + "/" + photo+ ".html");
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8));
            writer.write(content);
            writer.close();

            //�����ļ����ͺͳ���
            type = "text/html";
            len = (int)file.length();
        }

        //���췵�ص�ͷ����Ϣ
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

        //���ͷ�����Ϣ
        buffer = header.toString().getBytes();
        bo.write(buffer, 0, header.toString().length());
        bo.flush();
        clientSocket.shutdownOutput();//������־
    }

}