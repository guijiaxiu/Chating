package com.bittch.single_thread;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class SingleThreadClient {
    public static void main(String[] args) throws IOException {
       Socket client = null;
        Scanner readFromServer = null;
        PrintStream writeMsgToServer = null;
       try{
           //尝试与服务器建立连接
           client = new Socket("127.0.0.1",6666);
           //获取此链接的输入、输出流
           readFromServer = new Scanner(client.getInputStream());
           writeMsgToServer = new PrintStream(client.getOutputStream());
           //进行数据的输入、输出
           writeMsgToServer.println("Hi I am Client");
           if(readFromServer.hasNextInt()){
               System.out.println("服务器发来的消息为："+readFromServer.nextLine());
           }
       }catch(IOException e){
           e.printStackTrace();
       }finally {
           client.close();
           readFromServer.close();
           writeMsgToServer.close();
       }
    }
}
