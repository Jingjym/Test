package ex3.Client;

import ex3.Interface.MeetingInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Arrays;

/**
 * RMI 客户端
 *
 * @author Jing Yumeng
 * @version 1.0
 *
 */
public class RMIClient {

    private static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    /**
     * 为维护方便，所有操作结果信息写在此处
     */
    private static final String CREATE_SUCCESS = "Successful!";
    private static final String WRONG_PARAMETER = "[INFO] 参数错误!";
    private static final String SUCCESS = "[INFO] 操作成功！";
    private static final String FAILURE = "[INFO] 操作失败！";
    private static final String TIME_FORMAT = "[INFO] 时间格式：yyyy-MM-dd-HH:mm";

    private static String username = null;
    private static String password = null;

    static MeetingInterface rmi;

    /**
     * 程序入口，处理客户命令
     * @param args
     */
    public static void main(String[] args) {
        /**
         * 创建远程对象
         */
        try {
            if (args.length < 3) {
                System.err.println(WRONG_PARAMETER);
                System.exit(0);
            }
            String host = args[0];
            String port = args[1];
            /*
            通过查找获得远程对象
             */
            rmi = (MeetingInterface) Naming.lookup("//" + host + ":" + port + "/Interface.MeetingInterfaceImpl");

            /**
             * 注册服务
             */
            if (args[2].equals("register")) {
                if (args.length < 5) {
                    System.err.println(WRONG_PARAMETER);
                    System.exit(0);
                }
                boolean flag = rmi.registerUser(args[3], args[4]);
                if (!flag) {
                    System.err.println(FAILURE + " 用户已存在！");
                    System.exit(0);
                } else {
                    username = args[3];
                    password = args[4];
                    System.out.println(CREATE_SUCCESS);
                }
            } else if(args[2].equals("add")){
                if(args.length < 8){
                    System.err.println(WRONG_PARAMETER);
                    System.exit(0);
                }
                username = args[3];
                password = args[4];

                String[] cmds = Arrays.copyOfRange(args, 5, args.length);
                String[] otherUserNames = Arrays.copyOfRange(cmds, 0, cmds.length - 3);
                String info = rmi.addMeeting(username, password, otherUserNames, cmds[cmds.length - 3], cmds[cmds.length - 2], cmds[cmds.length - 1]);
                System.out.println(info);

            } else if(args[2].equals("query")){
                if(args.length < 7){
                    System.err.println(WRONG_PARAMETER);
                    System.exit(0);
                }
                username = args[3];
                password = args[4];

                String[] cmds = Arrays.copyOfRange(args, 5, args.length);
                String info = rmi.queryMeeting(username, password, cmds[0],cmds[1]);
                System.out.println(info);

            } else if(args[2].equals("delete")){
                if(args.length < 6){
                    System.err.println(WRONG_PARAMETER);
                    System.exit(0);
                }
                username = args[3];
                password = args[4];

                String[] cmds = Arrays.copyOfRange(args, 5, args.length);
                boolean flag = rmi.deleteMeeting(username, password, Integer.parseInt(cmds[0]));
                if (flag){
                    System.out.println(SUCCESS);
                }else System.err.println(FAILURE);

            } else if(args[2].equals("clear")){
                if(args.length < 5){
                    System.err.println(WRONG_PARAMETER);
                    System.exit(0);
                }
                username = args[3];
                password = args[4];

                boolean flag = rmi.clearMeeting(username, password);
                if (flag) {
                    System.out.println(SUCCESS);
                }else System.err.println(FAILURE);

            } else {
                System.err.println(WRONG_PARAMETER);
                System.exit(0);
            }

            /**
             * 显示帮助
             */
            helpMenu();

            /**
             * 其他服务
             */
            while (true) {
                System.out.println("Input an operation: ");
                String operation = br.readLine();
                String[] cmds = operation.split(" ");
                service(cmds);
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理命令
     *
     * @param cmds 命令
     */
    private static void service(String[] cmds) throws RemoteException {

        if (cmds[0].equals("add")) {
            doAdd(cmds);
        } else if (cmds[0].equals("delete")) {
            doDelete(cmds);
        } else if (cmds[0].equals("clear")) {
            doClear();
        } else if (cmds[0].equals("query")) {
            doQuery(cmds);
        } else if (cmds[0].equals("help")) {
            helpMenu();
        } else if (cmds[0].equals("quit")) {
            System.exit(0);
        }else System.err.println(WRONG_PARAMETER);
    }

    /**
     * 帮助菜单
     */
    private static void helpMenu() {
        System.out.println(TIME_FORMAT);
        System.out.println("RMI Menu:");
        System.out.println("\t" + "1.add");
        System.out.println("\t\t" + "arguments:<username> <start> <end> <title>");
        System.out.println("\t" + "2.delete");
        System.out.println("\t\t" + "arguments:<meetingid>");
        System.out.println("\t" + "3.clear");
        System.out.println("\t\t" + "arguments:no args");
        System.out.println("\t" + "4.query");
        System.out.println("\t\t" + "arguments:<start> <end>");
        System.out.println("\t" + "5.help");
        System.out.println("\t\t" + "arguments:no args");
        System.out.println("\t" + "6.quit");
        System.out.println("\t\t" + "arguments:no args");
    }

    /**
     * 根据命令添加会议
     *
     * @param cmds 添加命令
     * @throws RemoteException
     */
    private static void doAdd(String[] cmds) throws RemoteException {
        String info;
        if (cmds.length < 4) {
            info = WRONG_PARAMETER;
            System.err.println(info);
        } else {
            String[] otherUserNames = Arrays.copyOfRange(cmds, 1, cmds.length - 3);
            info = rmi.addMeeting(username, password, otherUserNames, cmds[cmds.length - 3], cmds[cmds.length - 2], cmds[cmds.length - 1]);
            System.out.println(info);
        }
    }

    /**
     * 根据命令删除会议
     *
     * @param cmds 删除命令
     * @throws RemoteException
     */
    private static void doDelete(String[] cmds) throws RemoteException {
        if (cmds.length != 2) {
            System.err.println(WRONG_PARAMETER);
        } else {
            boolean flag = rmi.deleteMeeting(username, password, Integer.parseInt(cmds[1]));
            if (flag){
                System.out.println(SUCCESS);
            }else System.err.println(FAILURE);
        }
    }

    /**
     * 根据命令清除会议
     *
     * @throws RemoteException
     */
    private static void doClear() throws RemoteException {
        boolean flag = rmi.clearMeeting(username, password);
        if (flag) {
            System.out.println(SUCCESS);
        }else System.err.println(FAILURE);
    }

    /**
     * 根据命令查询会议
     *
     * @param cmds 查询命令
     * @throws RemoteException
     */
    private static void doQuery(String[] cmds) throws RemoteException {
        if (cmds.length != 3) {
            System.err.println(WRONG_PARAMETER);
        } else {
            String info = rmi.queryMeeting(username, password, cmds[1],cmds[2]);
            System.out.println(info);
        }
    }
}
