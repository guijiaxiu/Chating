package com.bittch.multi_thread;

/**
 * 多线程版本的客户端
 * 读写分离，读写分别作为一个线程
 */

import javafx.scene.chart.LineChart;

import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.Scanner;

/**
 * 读取服务器发来信息的线程
 */
class ReadFromServer implements Runnable{
    private Socket client;
    // 通过构造方法传入通信的Socket
    public ReadFromServer(Socket client){
        this.client = client;
    }
    @Override
    public void run() {
        // 获取输入流，读取服务器发来的信息
        Scanner readFromServer = null;
        try {
            readFromServer = new Scanner(client.getInputStream());
            readFromServer.useDelimiter("\n");
            //不断读取服务器信息
            while(true){
                if(readFromServer.hasNextLine()){
                    String str = readFromServer.nextLine();
                    System.out.println("服务器发来的信息为："+str);
                }
                if(client.isClosed()){
                    System.out.println("客户端已经关闭");
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            readFromServer.close();
        }
    }
}

/**
 * 向服务器发送信息线程
 */
class SendMsgToServer implements Runnable{
    private Socket client;
    public SendMsgToServer(Socket client){
        this.client = client;
    }
    @Override
    public void run() {
        //获取键盘输入，向服务器发送信息
        Scanner in = new Scanner(System.in);
        PrintStream sendMsgToServer = null;

        try{
            //获取输出流。向服务器发送信息
            sendMsgToServer = new PrintStream(client.getOutputStream(),true,"UTF-8");
            while(true) {
                System.out.println("请输入要发送的信息...");
                if (in.hasNextLine()) {
                    String strToServer = in.nextLine();
                    sendMsgToServer.println(strToServer);
                    if(strToServer.contains("byebye")){
                        System.out.println("关闭客户端");
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            sendMsgToServer.close();
            in.close();
        }
    }
}
public class MutiThreadClient {
    public static void main(String[] args) throws IOException {
        //建立与服务器的连接
        Socket client = new Socket("127.0.0.1",6666);
        //创建读写线程与服务器通信
        Thread readThread = new Thread(new ReadFromServer(client));
        Thread sendThread = new Thread(new SendMsgToServer(client));
        readThread.start();
        sendThread.start();
    }
}
