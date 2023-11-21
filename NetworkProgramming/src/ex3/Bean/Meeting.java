package ex3.Bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author Jing Yumeng
 * @version 1.0
 * @see java.io.Serializable
 */
public class Meeting implements Serializable{

    /**
     * ����ID ����
     */
    private int meetingID;

    /**
     * ����title
     */
    private String title;

    /**
     * ���鿪ʼʱ�������ʱ��
     */
    private Date startTime;
    private Date endTime;

    /**
     * ����������������
     */
    private User launchUser;
    private ArrayList<User> otherUsers;

    /**
     * ���캯��
     *
     * @param meetingID ����ID
     * @param title �������
     * @param startTime ��ʼʱ��
     * @param endTime ����ʱ��
     * @param launchUser ���������
     * @param otherUsers ���������
     */
    public Meeting(int meetingID, String title, Date startTime, Date endTime, User launchUser, ArrayList<User> otherUsers) {
        this.meetingID = meetingID;
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.launchUser = launchUser;
        this.otherUsers = otherUsers;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Meeting)) return false;

        Meeting meeting = (Meeting) o;

        if (getMeetingID() != meeting.getMeetingID()) return false;
        if (!getTitle().equals(meeting.getTitle())) return false;
        if (getStartTime() != null ? !getStartTime().equals(meeting.getStartTime()) : meeting.getStartTime() != null)
            return false;
        if (getEndTime() != null ? !getEndTime().equals(meeting.getEndTime()) : meeting.getEndTime() != null)
            return false;
        if (!getLaunchUser().equals(meeting.getLaunchUser())) return false;
        return getOtherUsers() != null ? getOtherUsers().equals(meeting.getOtherUsers()) : meeting.getOtherUsers() == null;
    }

    @Override
    public String toString() {
        return "Meeting{" +
                "meetingID=" + meetingID +
                ", title='" + title + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", launchUser=" + launchUser.toString() +
                ", " +
                "\n" + "otherUsers=" + otherUsers.toString() +
                '}';
    }

    public int getMeetingID() {
        return meetingID;
    }

    public void setMeetingID(int meetingID) {
        this.meetingID = meetingID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public User getLaunchUser() {
        return launchUser;
    }

    public void setLaunchUser(User launchUser) {
        this.launchUser = launchUser;
    }

    public ArrayList<User> getOtherUsers() {
        return otherUsers;
    }

    public void setOtherUsers(ArrayList<User> otherUsers) {
        this.otherUsers = otherUsers;
    }
}


