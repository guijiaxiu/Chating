package com.bittch.single_thread;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class SingleThreadServer {
    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = null;
        Scanner readFromClient = null;
        PrintStream sendMsgToClient = null;
        try {
            //1. 建立服务端的基站
            serverSocket = new ServerSocket(6666);
            System.out.println("等待客户端连接....");
            //2. 一直阻塞直到有客户端连接
            Socket client = serverSocket.accept();
            System.out.println("有新的客户端连接，端口号为："+client.getPort());
            System.out.println(client.getLocalPort());
            //获取此连接的输入与输出流
            //3. 输入使用Scanner,输出使用打印流
             readFromClient =
                    new Scanner(client.getInputStream());
            sendMsgToClient =
                    new PrintStream(client.getOutputStream(),true,"UTF-8");
            //进行数据的输入、输出
            if(readFromClient.hasNext()){
                System.out.println("客户端说："+readFromClient.nextLine());
            }
            sendMsgToClient.println("Hi I am Server!");
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            //4. 基站关闭
            serverSocket.close();
            //关闭包装流
            readFromClient.close();
            sendMsgToClient.close();
        }
    }
}
