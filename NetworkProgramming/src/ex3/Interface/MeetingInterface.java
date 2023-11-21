package ex3.Interface;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author Jing Yumeng
 * @version 1.0
 * @see java.rmi.Remote
 */
public interface MeetingInterface extends Remote {

    /**
     * ע���û�
     *
     * @param username �û���
     * @param password ����
     * @return �Ƿ�ע��ɹ�
     * @throws RemoteException
     */
    public boolean registerUser(String username, String password) throws RemoteException;

    /**
     * ��ӻ���
     *
     * @param username �û���
     * @param password ����
     * @param otherUsers �������û����б�
     * @param start ��ʼʱ��
     * @param end ����ʱ��
     * @param title �������
     * @return ��ӻ�����Ϣ
     * @throws RemoteException
     */
    public String addMeeting(String username, String password, String[] otherUsers,
                             String start, String end, String title) throws RemoteException;

    /**
     * ��ѯ����
     *
     * @param username �û���
     * @param password ����
     * @param start ��ʼʱ��
     * @param end ����ʱ��
     * @return ������Ϣ
     * @throws RemoteException
     */
    public String queryMeeting(String username, String password, String start, String end) throws RemoteException;

    /**
     * ɾ������
     *
     * @param username �û���
     * @param password ����
     * @param meetingID ����ID
     * @return �Ƿ�ɾ���ɹ�
     * @throws RemoteException
     */
    public boolean deleteMeeting(String username, String password, int meetingID) throws RemoteException;

    /**
     * �������
     *
     * @param username �û���
     * @param password ����
     * @return �Ƿ�����ɹ�
     * @throws RemoteException
     */
    public boolean clearMeeting(String username, String password) throws RemoteException;
}
