package exam1;

//import exam.Constant;
//import exam.Handler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** ProvxyServer：代理服务器
 * 监听客户端的request请求，将request转发到目标HTTP服务器，并将响应的文件转发到客户端
 * 只接受客户端HTTP/1.0  GET请求
 * @author Jing Yuemng
 * @version 1.0
 */
public class ProvxyServer {

	/**
	 * 服务器套接字
	 */
	ServerSocket serverSocket;
	
	/**
	 * 服务器默认的端口号8000
	 */
	private static  int ProvxyPort = 8000;
	
	/**
	 * 开启代理服务器
	 */
	private void start() {
		try {
			serverSocket = new ServerSocket(ProvxyPort);
			System.out.println("代理服务器启动");

			final int POOL_SIZE=4;
			ExecutorService executorService; //线程池
			executorService = Executors.newFixedThreadPool(Runtime.getRuntime().
					availableProcessors() * POOL_SIZE);

			/**
			 * 监听端口的请求
			 */
			while(true) {
				Socket socket = serverSocket.accept();
				executorService.execute(new ProvxyThread(socket));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 程序入口
	 */
	public static void main(String[] args) throws IOException {

		//开启代理服务器
		new ProvxyServer().start();
		
	}

	

}
