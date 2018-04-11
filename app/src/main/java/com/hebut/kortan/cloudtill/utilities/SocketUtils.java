package com.hebut.kortan.cloudtill.utilities;

import android.os.Handler;
import android.os.Message;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketUtils {
    public final int Broadcast = -1;
    private List<Socket> mList = new ArrayList<>();
    private ServerSocket server = null;
    private ExecutorService mExecutorService = null; //thread pool
    private Handler mHandler;

    public SocketUtils(int port, Handler h) {
        this.mHandler = h;
        try {
            server = new ServerSocket(port);
            mExecutorService = Executors.newCachedThreadPool();  //create a thread pool
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void SocketServerStart() {
        try {
            Socket client = null;

            while(true) {
                client = server.accept();
                System.out.println("Server received");
                mList.add(client);
                mExecutorService.execute(new Service(client, mList.indexOf(client))); //start a new thread to handle the connection
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int SocketGetTotalNum(){
        return mList.size();
    }

    public void SocketSendMsg(String msg, int sIndex){

        int up = 0;
        int down = 0;
        int num =mList.size();

        if (sIndex < num){
            if (sIndex == Broadcast) {
                up = num;
                down = 0;
            }else {
                up = sIndex + 1;
                down = sIndex;
            }

            for (int index = down; index < up; index++){
                sendmsg(msg, index);
            }
        }

    }

    private void sendmsg(String msg, int sIndex) {
        System.out.println(String.valueOf(sIndex) + ":" + msg);
        Socket mSocket = mList.get(sIndex);
        PrintWriter pout = null;
        try {
            pout = new PrintWriter(
                    new BufferedWriter(
                            new OutputStreamWriter(mSocket.getOutputStream())), true);
            pout.println(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    class Service implements Runnable {
        private Socket socket;
        private BufferedReader in = null;
        private String msg = "";
        private int sIndex;

        public Service(Socket socket, int sIndex) {
            this.socket = socket;
            this.sIndex = sIndex;
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                msg = "user" +this.socket.getInetAddress() + "come total:" + mList.size();
                sendmsg(msg, sIndex);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                while(true) {
                    if((msg = in.readLine())!= null) {
                        if(msg.equals("exit")) {
                            msg = "user:" + socket.getInetAddress() + "exit total:" + mList.size();
                            sendmsg(msg, sIndex);
                            in.close();
                            socket.close();
                            mList.remove(socket);
                            break;
                        } else {
                            msg = socket.getInetAddress() + ":" + msg;
//                            sendmsg(msg, sIndex);
                            Message socketMessage = mHandler.obtainMessage(sIndex, msg);
                            socketMessage.sendToTarget();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
