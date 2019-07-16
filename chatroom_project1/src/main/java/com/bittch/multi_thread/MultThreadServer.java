package com.bittch.multi_thread;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 处理客户端请求的线程
 */
public class MultThreadServer {
    //使用ConcurrentHashMap来保存所有连接的客户端信息
    private static Map<String,Socket> clientsMap =
            new ConcurrentHashMap<>();

    public static void main(String[] args) throws Exception{
        ServerSocket serverSocket = new ServerSocket(6666);
        ExecutorService service = Executors.newFixedThreadPool(20);
        for(int i = 0;i<20;i++){
            System.out.println("等待客户端连接...");
            Socket client = serverSocket.accept();
            System.out.println("有新的客户端连接，端口号为："+client.getPort());
            service.submit(new ExecuteClientRequest(client));
        }
    }
    static class ExecuteClientRequest implements Runnable{
        private Socket client;
        public ExecuteClientRequest(Socket client){
            this.client = client;
        }
        @Override
        public void run(){
            //获取输入流不断的读取用户发来的信息
            Scanner readFromClient = null;
            try {
                readFromClient = new Scanner(client.getInputStream());
                readFromClient.useDelimiter("\n");
                while(true){
                    if(readFromClient.hasNextLine()){
                        String str = readFromClient.nextLine();
                        //进行 /r 过滤
                        //windows下进行换行的过滤
                        Pattern pattern = Pattern.compile("\r");
                        Matcher matcher = pattern.matcher(str);
                        str = matcher.replaceAll("");

                        if(str.startsWith("userName")){
                            //userName :zhangsan
                            //用户的注册流程
                            String userName =
                                    str.split(":")[1];
                            userRegister(userName,client);
                            continue;
                        }else if(str.startsWith("G:")){
                            //群聊流程
                            // G:hello i am...
                            String msg = str.split(":")[1];
                            groupChat(msg);
                            continue;
                        }else if(str.startsWith("P:")){
                            //私聊流程
                            // P：zhangsan - hello i am...
                            String tempMsg = str.split(":")[1];
                            String userName = tempMsg.split("-")[0];
                            String privateMsg = tempMsg.split("-")[1];
                            privateChat(userName,privateMsg);
                            continue;
                        }else if(str.contains("byebye")){
                            //用户退出流程
                            //zhangsan : byebye
                            String userName = str.split(":")[0];
                            userExist(userName);
                            continue;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * 用户注册方法
         * @param userName 用户名
         * @param client 对应的Socket
         */
        private void userRegister(String userName,Socket client){
            //将用户信息保存到服务器中
            clientsMap.put(userName,client);
            //取得当前注册到服务器的所有人的个数
            int size = clientsMap.size();
            System.out.println("当前聊天室内共有"+size+"人");
           String userOnLine = userName+"上线了！";
           groupChat(userOnLine);
        }

        /**
         * 群聊流程
         * @param msg  用户要发送的群聊信息
         */
        private void groupChat(String msg){
            //取出所有连接的客户端，依次拿到输出流进行遍历输出
            Collection<Socket> clients = clientsMap.values();
            for(Socket client : clients){
                //取出此客户端的输出流
                try {
                    PrintStream out = new PrintStream(client.getOutputStream());
                    out.println("群聊信息为："+msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        }

        /**
         * 私聊流程
         * @param userName 私聊的用户名
         * @param msg 私聊的信息
         */
        private void privateChat(String userName,String msg){
            Socket client = clientsMap.get(userName);
            try {
                PrintStream out =
                        new PrintStream(client.getOutputStream(),true,"UTF-8");
                out.println("私聊信息为："+msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * 用户退出
         * @param userName 退出的用户名
         */
        private void userExist(String userName){
            clientsMap.remove(userName);
            System.out.println("当前聊天室人数为 ："+clientsMap.size());
            String groupChatMsg = userName+"已下线";
            groupChat(groupChatMsg);
        }
    }
}
