package exam1;

//import exam.Constant;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.net.Socket;
import java.nio.file.Files;

/**
 * Class <em>HttpClient</em> is a class representing a simple HTTP client.
 *
 * @author wben
 */

public class HttpClient {

    /**
     * default HTTP port is port 8000
     */
    private static int port = 8000;

    /**
     * Allow a maximum buffer size of 8192 bytes
     */
    private static int buffer_size = 8192;

    /**
     * Response is stored in a byte array.
     */
    private byte[] buffer;

    /**
     * My socket to the world.
     */
    Socket socket = null;

    /**
     * Default port is 8000.
     */
    private static final int PORT = 8000;

    /**
     * Output stream to the socket.
     */
    BufferedOutputStream ostream = null;

    /**
     * Input stream from the socket.
     */
    BufferedInputStream istream = null;

    /**
     * StringBuffer storing the header
     */
    private StringBuffer header = null;

    /**
     * StringBuffer storing the response.
     */
    private StringBuffer response = null;

    /**
     * String to represent the Carriage Return and Line Feed character sequence.
     */
    static private String CRLF = "\r\n";

    String myMail = "JingYumeng@qq.com";

    /**
     * HttpClient constructor;
     */
    public HttpClient() {
        buffer = new byte[buffer_size];
        header = new StringBuffer();
        response = new StringBuffer();
    }

    /**
     * <em>connect</em> connects to the input host on the default http port --
     * port 80. This function opens the socket and creates the input and output
     * streams used for communication.
     */
    public void connect(String host) throws Exception {

        /**
         * Open my socket to the specified host at the default port.
         */
        socket = new Socket(host, PORT);

        /**
         * Create the output stream.
         */
        ostream = new BufferedOutputStream(socket.getOutputStream());

        /**
         * Create the input stream.
         */
        istream = new BufferedInputStream(socket.getInputStream());
    }

    /**
     * <em>processGetRequest</em> process the input GET request.
     */
    public void processGetRequest(String request, String host) throws Exception {
        /**
         * Send the request to the server.
         */
        request += CRLF;

        request += "Host: " + host + CRLF;
        request += "From: " + myMail + CRLF;
        request += "Connection: Close" + CRLF;
        request += "User-Agent: HTTPTool/1.0" + CRLF + CRLF;
        buffer = request.getBytes();
        ostream.write(buffer, 0, request.length());
        ostream.flush();
        /**
         * waiting for the response.
         */
        processResponse();
    }


    /**
     * <em>processPutRequest</em> process the input PUT request.
     * @param request
     * @param address
     * @throws Exception
     */
    public void processPutRequest(String request,String address) throws Exception {
        //��ȡ�ļ���
        String[] str = request.split(" ");
        String dir = "E:\\AllAssignmentCode\\javaIdea\\NetworkProgramming\\root";
        String fileName = dir + str[1];
        for (int i = 2;i < str.length-1;i++){
            fileName += " ";
            fileName += str[i];
        }

        File file = new File(fileName);

        //�ж��ļ��Ƿ����
        int flag = 0;
        File parentDir = file.getParentFile();//��Ŀ¼
        File[] dirFiles = parentDir.listFiles();//��ȡ��Ŀ¼�ļ��б�
        for (File files : dirFiles) {
            if (files.getName().equals(file.getName())) {
                flag = 1;
                break;
            }
        }
        //���ļ������ڻ�������ͨ�ļ�ʱ�����ش�����Ϣ
        if (flag == 0 || file.isDirectory()){
            System.out.println("Not Found!");
            System.exit(0);
        }

        //�ļ����ڣ���ȡ�ļ�����
        byte[] files;
        files = Files.readAllBytes(file.toPath());

        //���� HTTP PUT ����ͷ��
        String resHeader = "PUT " + fileName + " HTTP/1.1"+ CRLF
                + "Host: " + address + CRLF
                + "Connection: close" + CRLF
                + "Content-Type: text/html; charset=UTF-8" + CRLF
                + "Content-Length: " + files.length + CRLF + CRLF;

        //��������ͷ��
        ostream.write(resHeader.getBytes(),0,resHeader.getBytes().length);
        //�����ļ�����
        ostream.write(files);
        ostream.flush();
        socket.shutdownOutput();//������־

        //�ȴ���Ӧ
        processResponse();

    }

    /**
     * <em>processResponse</em> process the server response.
     *
     */
    public void processResponse() throws Exception {
        int last = 0, c = 0;
        /**
         * Process the header and add it to the header StringBuffer.
         */
        boolean inHeader = true; // loop control
        while (inHeader && ((c = istream.read()) != -1)) {
            switch (c) {
                case '\r':
                    break;
                case '\n':
                    if (c == last) {
                        inHeader = false;
                        break;
                    }
                    last = c;
                    header.append("\n");
                    break;
                default:
                    last = c;
                    header.append((char) c);
            }
        }

        /**
         * Read the contents and add it to the response StringBuffer.
         */
        while (istream.read(buffer) != -1) {
            response.append(new String(buffer,"iso-8859-1"));
        }
    }

    /**
     * Get the response header.
     */
    public String getHeader() {
        return header.toString();
    }

    /**
     * Get the server's response.
     */
    public String getResponse() {
        return response.toString();
    }

    /**
     * Close all open connections -- sockets and streams.
     */
    public void close() throws Exception {
        socket.close();
        istream.close();
        ostream.close();
    }
}
