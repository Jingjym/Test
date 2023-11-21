package Utils;

import java.io.*;

public class StreamUtils {
    /**
     *���ܣ���������ת����byte[],�����԰��ļ������ݶ��뵽byte[]
     * @param is
     * @return
     * @throws Exception
     */
    public static byte[] streamToByteArray(InputStream is) throws Exception {
        //�������������
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        //�ֽ�����
        byte[] b = new byte[1024];
        int len;
        //ѭ����ȡ���Ѷ�ȡ��������д��bos
        while ((len=is.read(b))!=-1){
            bos.write(b,0,len);
        }
        //��bosת���ֽ�����
        byte[] array = bos.toByteArray();
        bos.close();
        return array;
    }


    /**
     * ���ܣ���InputStreamת����String
     * @param is
     * @return
     * @throws Exception
     */

    public static String streamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line=reader.readLine())!=null){
            builder.append(line+"\r\n");
        }
        return builder.toString();
    }
}
