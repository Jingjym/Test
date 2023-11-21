package ex3.Interface;

import ex3.Bean.Meeting;
import ex3.Bean.User;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

/**
 * @author Jing Yumeng
 * @version 1.0
 * @see java.rmi.server.UnicastRemoteObject
 * @see MeetingInterface
 */
public class MeetingInterfaceImpl extends UnicastRemoteObject implements MeetingInterface {

    private ArrayList<User> users = new ArrayList<>();
    private ArrayList<Meeting> meetings = new ArrayList<>();

    private static int meetingID = 0;
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm");


    /**
     * Ϊά�����㣬���з�����Ϣд�ڴ˴�
     */
    private static final String REGISTER_USER_SUCCESS = "[SERVER INFO] ע��ɹ���";
    private static final String REGISTER_USER_FAILURE = "[SERVER INFO] ע��ʧ�ܣ�";
    private static final String LOGIN_SUCCESS = "[SERVER INFO] �û��Ѿ����ڣ�����Ҫ����ע�ᣡ��";

    private static final String INVALID_TIME_FORMAT = "[INFO] ��Чʱ���ʽ! (yyyy-MM-dd-HH:mm)";
    private static final String INVALID_TIME = "[INFO] ��Чʱ��!";
    private static final String INVALID_USER = "[INFO] ��Ч�û�!";
    private static final String ADD_MEETING_SUCCESS = "[INFO] ��ӻ���ɹ�!";
    private static final String MEMBERS_ERROR = "[INFO] ������Ա�ﲻ�ܰ��������߱���";
    private static final String LACK_OF_USERS = "[INFO] ֻ��һ���û� ���ܴ������飡";

    public MeetingInterfaceImpl() throws RemoteException {
        super();
    }

    @Override
    public boolean registerUser(String username, String password) throws RemoteException {

        for (User user : users) {
            if (isUserExist(username)) {
                System.out.println(LOGIN_SUCCESS + "\n");
                return false;
            }
        }
        User user = new User(username, password);
        users.add(user);
        System.out.println(user.toString() + "\n" + REGISTER_USER_SUCCESS + "\n");
        return true;
    }

    @Override
    public String addMeeting(String username, String password, String[] otherUserNames, String start, String end, String title) throws RemoteException {

        String info = null;

        ArrayList<User> otherUsers = new ArrayList<>();
        User launchUser = new User(username, password);

        Date startTime = null;
        Date endTime = null;

        if(!isLegalDate(start) || !isLegalDate(end)){
            info = INVALID_TIME;
            return info;
        }

        /*
        �ж�ʱ���ʽ
        */
        try {
            startTime = dateFormat.parse(start);
            endTime = dateFormat.parse(end);
        } catch (ParseException e) {
            info = INVALID_TIME_FORMAT;
            e.printStackTrace();
            return info;
        }


        /*
        �ж�ʱ���ص��뵹��
        */
        if (isOverlap(startTime, endTime)||isReversed(startTime, endTime)) {
            info = INVALID_TIME;
            return info;
        }

        /*
        �ж�������Ƿ����
         */
        if (!isUserExist(launchUser)) {
            info = INVALID_USER;
            return info;
        }

        /*
        �ж��Ƿ���other users ��û����ʾȱ���û�
         */
        if (otherUserNames.length < 1) {
            info = LACK_OF_USERS;
            return info;
        }

        /*
        �ж�other users����û�в�����
         */
        for(String name : otherUserNames){
            if(name.equals(launchUser.getName())){
                info = MEMBERS_ERROR;
                return info;
            }
        }


        /*
        �жϲ������Ƿ����
         */
        if(!isUsersExist(otherUserNames)){
            info = INVALID_USER;
            return info;
        }else {
            for (String temp : otherUserNames){
                for (User user : users){
                    if (user.getName().equals(temp)){
                        otherUsers.add(user);
                    }
                }
            }
        }

        /*
        ��ӻ���
         */
        Meeting meeting = new Meeting(meetingID++, title, startTime, endTime, launchUser, otherUsers);
        meetings.add(meeting);
        info = ADD_MEETING_SUCCESS;

        return info;
    }

    @Override
    public String queryMeeting(String username, String password, String start, String end) throws RemoteException {
        String info = "";

        User user = new User(username,password);

        Date startTime = null;
        Date endTime = null;

        if(!isLegalDate(start) || !isLegalDate(end)){
            info = INVALID_TIME;
            return info;
        }

        /*
        �ж�ʱ���ʽ
        */
        try {
            startTime = dateFormat.parse(start);
            endTime = dateFormat.parse(end);

        } catch (ParseException e) {
            info = INVALID_TIME_FORMAT;
            e.printStackTrace();
            return info;
        }

        /*
        �ж�������Ƿ����
         */
        if (!isUserExist(user)) {
            info = INVALID_USER;
            return info;
        }

        for (Meeting meeting : meetings) {
            if (isBetween(meeting.getStartTime(), startTime, endTime)
                    && isBetween(meeting.getEndTime(), startTime, endTime)) {
                info += meeting.toString();
                info += "\n";
            }
        }

        return info;
    }

    @Override
    public boolean deleteMeeting(String username, String password, int meetingID) throws RemoteException {

        User user = new User(username,password);

        /*
        �ж�������Ƿ����
         */
        if (!isUserExist(user)) {
            System.out.println(INVALID_USER + "\n");
            return false;
        }

        /*
        ɾ������
         */
        for (Meeting meeting : meetings) {
            if (meeting.getMeetingID() == meetingID
                    && meeting.getLaunchUser().equals(user)){
                meetings.remove(meeting);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean clearMeeting(String username, String password) throws RemoteException {

        User user = new User(username,password);

        /*
        �ж�������Ƿ����
         */
        if (!isUserExist(user)) {
            System.out.println(INVALID_USER + "\n");
            return false;
        }

        /*
        �������
         */
        Iterator<Meeting> meetingIterator = meetings.iterator();
        while (meetingIterator.hasNext()) {
            if (meetingIterator.next().getLaunchUser().equals(user)) {
                meetingIterator.remove();
            }
        }
        return true;
    }

    /**
     * �ж��»����Ƿ���ԭ����ʱ���ص�
     *
     * @param startTime �»��鿪ʼʱ��
     * @param endTime   �»������ʱ��
     * @return �Ƿ��ص�
     */
    private boolean isOverlap(Date startTime, Date endTime) {
        boolean isOverlap = false;
        for (Meeting meeting : meetings) {
            if (isOverlap(meeting.getStartTime(), meeting.getEndTime(), startTime, endTime)) {
                isOverlap = true;
                return isOverlap;
            }
        }
        return isOverlap;
    }

    /**
     * �ж�ʱ���Ƿ�ߵ�
     *
     * @param former ��ʼʱ��
     * @param latter ����ʱ��
     * @return �Ƿ�ߵ�
     */
    private boolean isReversed(Date former, Date latter){
        return former.after(latter);
    }

    /**
     * �ж�[st1 et1]��[st2 et2]ʱ���Ƿ��ص�
     *
     * @param st1 ��ʼʱ��1
     * @param et1 ����ʱ��1
     * @param st2 ��ʼʱ��2
     * @param et2 ����ʱ��2
     * @return �Ƿ��ص�
     */
    private boolean isOverlap(Date st1, Date et1, Date st2, Date et2) {
        boolean isOverlap = isBetween(st2, st1, et1) || isBetween(et2, st1, et1) || isBetween(st1, st2, et2) ||
                st1.equals(st2) || et1.equals(et2);//��ʱ���Ƿ����
        return isOverlap;
    }

    /**
     * �ж�date�Ƿ���ʱ���[former latter]��
     *
     * @param date   ʱ��
     * @param former ��ʼʱ��
     * @param latter ����ʱ��
     * @return �Ƿ���ʱ�����
     */
    private boolean isBetween(Date date, Date former, Date latter) {
        boolean isBetween = (date.after(former) && date.before(latter)) || date.equals(former) || date.equals(latter);
        return isBetween;
    }

    /**
     * �ж�һ���û��Ƿ񶼴���
     *
     * @param usernames �û����б�
     * @return
     */
    private boolean isUsersExist(String[] usernames) {
        boolean isAllExist = true;
        for (String username : usernames) {
            if (!isUserExist(username)) {
                isAllExist = false;
                break;
            }
        }
        return isAllExist;
    }

    /**
     * �ж��û��Ƿ����
     *
     * @param username �û���
     * @return �Ƿ����
     */
    private boolean isUserExist(String username) {
        boolean isExist = false;
        for (User user : users) {
            if (user.getName().equals(username)) {
                isExist = true;
                break;
            }
        }
        return isExist;
    }

    /**
     * �ж��û��Ƿ����
     *
     * @param user �û�
     * @return �Ƿ����
     */
    private boolean isUserExist(User user) {
        boolean isExist = false;
        for (User temp : users) {
            if (temp.equals(user)) {
                isExist = true;
                break;
            }
        }
        return isExist;
    }

    private boolean isLegalDate(String date){
        String[] theDate = date.split("-");
//        System.out.println(theDate[0]);

        int year = Integer.parseInt(theDate[0]);
        int month = Integer.parseInt(theDate[1]);
        int day = Integer.parseInt(theDate[2]);

        //����һ���Ϸ��·ݵ��������飬У�������Ƿ�Ϸ�
        int [] arr = {31,28,31,30,31,30,31,31,30,31,30,31};
        //����ƽ�����28�죬�������29�죬����⸳ֵ
        if((year % 4 == 0 && year %100 != 0) || year % 400 == 0){
            arr[1] = 29; //����
        }else{
            arr[1] = 28; //ƽ��
        }

        //У���·��Ƿ�Ϸ���0<month<13
        if(month >0 && month < 13){
            //�ж�����
            if(day > arr[month-1] || day <= 0){
                return false;
            }
        }

        String[] hourANDmin = theDate[3].split(":");
        int hour = Integer.parseInt(hourANDmin[0]);
        int min = Integer.parseInt(hourANDmin[1]);
//        System.out.println("hour and min : " + hour + " " + min);

        if(hour >=0 && hour <= 23){
            if(min < 0 || min >= 60){
                return false;
            }
        }

        return true;
    }
}

