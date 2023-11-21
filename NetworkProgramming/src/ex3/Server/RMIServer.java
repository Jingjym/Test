package ex3.Server;


import ex3.Interface.MeetingInterface;
import ex3.Interface.MeetingInterfaceImpl;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

/**
 * RMI ������
 *
 * @author Jing Yumeng
 * @version 1.0
 */
public class RMIServer {
    /**
     * ������ڣ����� RMI ע����񲢽��ж���ע��
     */
    public static void main(String[] args) {
        try {
            //����һ��rmi url ʹ��8000�˿ں�
            String url = "rmi://localhost:8000/Interface.MeetingInterfaceImpl";

            LocateRegistry.createRegistry(8000);

            // ����Զ�̶����һ������ʵ����������MeetingInterfaceImpl����
            MeetingInterface meetingInterface = new MeetingInterfaceImpl();

            // ��MeetingInterfaceImpl����ע�ᵽRMIע���������
            Naming.rebind(url, meetingInterface);

            // Ҳ���԰�RMIServerʵ��ע�ᵽ��һ̨������RMIע�����Ļ�����
            System.out.println("RMIServer is ready!");
        } catch (Exception e) {
            System.out.println("RMIServer failed: " + e);
        }
    }
}
