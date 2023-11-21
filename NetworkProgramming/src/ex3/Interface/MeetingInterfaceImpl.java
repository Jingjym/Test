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
     * 为维护方便，所有返回信息写在此处
     */
    private static final String REGISTER_USER_SUCCESS = "[SERVER INFO] 注册成功！";
    private static final String REGISTER_USER_FAILURE = "[SERVER INFO] 注册失败！";
    private static final String LOGIN_SUCCESS = "[SERVER INFO] 用户已经存在（不需要重新注册！）";

    private static final String INVALID_TIME_FORMAT = "[INFO] 无效时间格式! (yyyy-MM-dd-HH:mm)";
    private static final String INVALID_TIME = "[INFO] 无效时间!";
    private static final String INVALID_USER = "[INFO] 无效用户!";
    private static final String ADD_MEETING_SUCCESS = "[INFO] 添加会议成功!";
    private static final String MEMBERS_ERROR = "[INFO] 其他成员里不能包括操作者本身！";
    private static final String LACK_OF_USERS = "[INFO] 只有一名用户 不能创建会议！";

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
        判断时间格式
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
        判断时间重叠与倒置
        */
        if (isOverlap(startTime, endTime)||isReversed(startTime, endTime)) {
            info = INVALID_TIME;
            return info;
        }

        /*
        判断添加者是否存在
         */
        if (!isUserExist(launchUser)) {
            info = INVALID_USER;
            return info;
        }

        /*
        判断是否有other users 若没有提示缺少用户
         */
        if (otherUserNames.length < 1) {
            info = LACK_OF_USERS;
            return info;
        }

        /*
        判断other users里有没有操作者
         */
        for(String name : otherUserNames){
            if(name.equals(launchUser.getName())){
                info = MEMBERS_ERROR;
                return info;
            }
        }


        /*
        判断参与者是否合理
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
        添加会议
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
        判断时间格式
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
        判断添加者是否存在
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
        判断添加者是否存在
         */
        if (!isUserExist(user)) {
            System.out.println(INVALID_USER + "\n");
            return false;
        }

        /*
        删除会议
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
        判断添加者是否存在
         */
        if (!isUserExist(user)) {
            System.out.println(INVALID_USER + "\n");
            return false;
        }

        /*
        清除会议
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
     * 判断新会议是否与原会议时间重叠
     *
     * @param startTime 新会议开始时间
     * @param endTime   新会议结束时间
     * @return 是否重叠
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
     * 判断时间是否颠倒
     *
     * @param former 开始时间
     * @param latter 结束时间
     * @return 是否颠倒
     */
    private boolean isReversed(Date former, Date latter){
        return former.after(latter);
    }

    /**
     * 判断[st1 et1]与[st2 et2]时间是否重叠
     *
     * @param st1 起始时间1
     * @param et1 结束时间1
     * @param st2 起始时间2
     * @param et2 结束时间2
     * @return 是否重叠
     */
    private boolean isOverlap(Date st1, Date et1, Date st2, Date et2) {
        boolean isOverlap = isBetween(st2, st1, et1) || isBetween(et2, st1, et1) || isBetween(st1, st2, et2) ||
                st1.equals(st2) || et1.equals(et2);//两时间是否相等
        return isOverlap;
    }

    /**
     * 判断date是否在时间段[former latter]内
     *
     * @param date   时间
     * @param former 起始时间
     * @param latter 结束时间
     * @return 是否在时间段中
     */
    private boolean isBetween(Date date, Date former, Date latter) {
        boolean isBetween = (date.after(former) && date.before(latter)) || date.equals(former) || date.equals(latter);
        return isBetween;
    }

    /**
     * 判断一组用户是否都存在
     *
     * @param usernames 用户名列表
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
     * 判断用户是否存在
     *
     * @param username 用户名
     * @return 是否存在
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
     * 判断用户是否存在
     *
     * @param user 用户
     * @return 是否存在
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

        //定义一个合法月份的天数数组，校验天数是否合法
        int [] arr = {31,28,31,30,31,30,31,31,30,31,30,31};
        //由于平年二月28天，闰年二月29天，需额外赋值
        if((year % 4 == 0 && year %100 != 0) || year % 400 == 0){
            arr[1] = 29; //闰年
        }else{
            arr[1] = 28; //平年
        }

        //校验月份是否合法，0<month<13
        if(month >0 && month < 13){
            //判断日期
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

