package ex3.Server;


import ex3.Interface.MeetingInterface;
import ex3.Interface.MeetingInterfaceImpl;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

/**
 * RMI 服务器
 *
 * @author Jing Yumeng
 * @version 1.0
 */
public class RMIServer {
    /**
     * 程序入口，启动 RMI 注册服务并进行对象注册
     */
    public static void main(String[] args) {
        try {
            //定义一个rmi url 使用8000端口号
            String url = "rmi://localhost:8000/Interface.MeetingInterfaceImpl";

            LocateRegistry.createRegistry(8000);

            // 创建远程对象的一个或多个实例，下面是MeetingInterfaceImpl对象
            MeetingInterface meetingInterface = new MeetingInterfaceImpl();

            // 把MeetingInterfaceImpl对象注册到RMI注册服务器上
            Naming.rebind(url, meetingInterface);

            // 也可以把RMIServer实例注册到另一台启动了RMI注册服务的机器上
            System.out.println("RMIServer is ready!");
        } catch (Exception e) {
            System.out.println("RMIServer failed: " + e);
        }
    }
}
