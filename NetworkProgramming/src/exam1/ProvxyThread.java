package exam1;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

/**
 * ProvxyThread
 * 处理客户线程,针对每个客户实现GET请求处理
 * @author Jing Yuemng
 * @version 1.0
 */
public class ProvxyThread extends Thread{

	/**
	 * 该线程处理的客户端的socket
	 */
	Socket socket;
	
	/**
	 * 定义对客户端的输入输出流
	 */
	private BufferedInputStream istream;
	private BufferedOutputStream ostream;
	
	/**
	 * 定义缓冲区
	 */
	static private int buffer_size = 8192;
	private byte[] buffer;
	
	/**
	 * 定义回车换行符号
	 */
	static private String CRLF = "\r\n"; 
	
	/**
	 * 构造函数，初始化客户端的socket以及对socket的输入输出流，缓冲区等信息
	 * @param socket 客户端的socket
	 */
	public ProvxyThread(Socket socket) {
		this.socket = socket;
		try {
			this.istream = new BufferedInputStream(socket.getInputStream());
			this.ostream = new BufferedOutputStream(socket.getOutputStream());
			this.buffer = new byte[buffer_size];
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	/**
	 * 线程入口
	 * 接收客户端的request请求，将request转发到目标HTTP服务器
	 * 并将响应的文件转发到客户端
	 */
	public void run() {
			String header = null;
			//读取请求信息
			try {
				if(this.istream.read(buffer) != -1) {
					header = new String(buffer);
					header = new String(header.getBytes("iso-8859-1"));
					System.out.println(header);
				}
				
				String[] str = header.split(CRLF);
				String[] str1 = str[0].split(" ");
				if(str1.length == 3) {
					if(!str1[0].equals("GET") || !str1[2].equals("HTTP/1.0")) {//对不支持的请求返回错误代码
						String msg = "Content-Length: 0" + CRLF;
						msg = "HTTP/1.0" + " 501 Not Implemented" + CRLF + CRLF;
						this.ostream.write(msg.getBytes());
					}else {
						String urlstring = str1[1];
						
						/**
						 * 从目的服务器读取所需文件信息
						 */
						URL url = new URL(urlstring);
						URLConnection conn = url.openConnection();
						//若服务器不存在会抛出ConnectException异常
						BufferedInputStream in = new BufferedInputStream(conn.getInputStream());
						String msg = "";
						/**
						 * 判断文件是否存在，不存在返回的长度为0
						 */
						if(conn.getHeaderField("Content-Length").equals("0")){
							msg += "HTTP/1.0 404 Not Found" + CRLF + CRLF;
						}else {
							String name = urlstring;
							msg = "HTTP/1.0" + " 200 OK" + CRLF;
							//获取时间
							msg += "Date: " + new Date() + CRLF;
							
							//判断文件类型
					    	if(name.endsWith("txt") || name.endsWith("html") || name.endsWith("htm")) {
					    		msg += "Content-Type: text/html"+CRLF;
					    	}else if(name.endsWith("jpg") || name.endsWith("jpeg")) {
					    		msg += "Content-Type: image/jpeg"+CRLF;
					    	}else {
					    		msg += "Content-Type: application/octet-stream"+CRLF;;
					    	}
							msg += "Content-Length: " + Long.parseLong(conn.getHeaderField("Content-Length")) + CRLF;
							msg += CRLF;
						}
						System.out.println(msg);
						this.ostream.write(msg.getBytes());//发送请求头部
						this.ostream.flush();
						
						buffer = new byte[buffer_size];
						while(in.read(buffer) != -1) {
							this.ostream.write(buffer);//发送文件
						}
						this.ostream.flush();
						socket.shutdownOutput();
						in.close();
					}
				}else {
					//不合法的请求
					String msg = "HTTP/1.0 400 Bad Request" + CRLF;
					msg += "Date: " + new Date();
					this.ostream.write(msg.getBytes());
					this.ostream.flush();
				}
			} catch (UnsupportedEncodingException e) {
				//此处做404 not found处理
				String msg = "Content-Length: 0" + CRLF;
				msg = "HTTP/1.0" + " 404 Not Found" + CRLF + CRLF;
				
				try {
					this.ostream.write(msg.getBytes());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} catch (IOException e) {
				//此处做404 not found处理
				String msg = "Content-Length: 0" + CRLF;
				msg = "HTTP/1.0" + " 404 Not Found" + CRLF + CRLF;
				
				try {
					this.ostream.write(msg.getBytes());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
	}

}
