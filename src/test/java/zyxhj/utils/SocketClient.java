package zyxhj.utils;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
 
public class SocketClient {
    public static String _pattern = "yyyy-MM-dd HH:mm:ss SSS";
    public static SimpleDateFormat format = new SimpleDateFormat(_pattern);
    // 设置超时间
    public static int _sec = 10000;
 
    public static void main(String[] args) {
        System.out.println("----------Client----------");
 
        Socket socket = null;
        try {
            // 与服务端建立连接
            socket = new Socket("127.0.0.1", 8001);
            socket.setSoTimeout(_sec * 1000);
 
            System.out.println(format.format(new Date()));
            System.out.println("建立了链接\n");
 
            // 往服务写数据
            Writer writer = new OutputStreamWriter(socket.getOutputStream());
                         
            System.out.println(format.format(new Date()));
            System.out.println("我在写啦\n");
//            Thread.sleep(10000);
            Scanner scan = new Scanner(System.in);
            
            writer.write(scan.next());
             
//            System.out.println(format.format(new Date()));
//            System.out.println("写完啦 你收下\n");
             
            writer.flush();
             
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
 
             
            System.out.println(format.format(new Date()) + "\n" + _sec + "秒的时间 告诉我你收到了吗\n");
 
            System.out.println(format.format(new Date()) + "\nServer:" + br.readLine());
             
        } catch (SocketTimeoutException e) {
            System.out.println(format.format(new Date()) + "\n" + _sec + "秒没收到回复 我下啦\n\n\n\n\n");
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
