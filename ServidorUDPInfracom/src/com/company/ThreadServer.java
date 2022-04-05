package com.company;

import com.sun.tools.javac.Main;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class ThreadServer extends Thread {
    private final Socket clientSocket;
    private final DataOutputStream out;
    private final DataInputStream in;
    private final MainServer mainServer;
    private String filepath;
    private int clientId = 0;
    private int tamFragmento;

    public ThreadServer(String filepath, Socket clientSocket, DataOutputStream out, DataInputStream in, MainServer mainServer, int tamFragmento) {

        this.filepath = filepath;
        this.clientSocket = clientSocket;
        this.out = out;
        this.in = in;
        this.mainServer = mainServer;
        this.tamFragmento = tamFragmento;

    }

    @Override
    public void run() {
        try {

            MainServer.log("Server started, there are now " + mainServer.threads.size() + " threads.");

            try {
                sendFile(filepath);
                MainServer.log("==========================================================");
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
                //Remove thread from threadlist if the connection drops or something else happens
                mainServer.threads.remove(this);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void sendFile(String path) throws Exception{
        int bytes = 0;
        File file = new File(path);
        FileInputStream fileInputStream = new FileInputStream(file);
        // send file size
        out.writeLong(file.length());
        // break file into chunks
        long startTime = System.currentTimeMillis();
        byte[] buffer = new byte[tamFragmento*1024];
        while ((bytes=fileInputStream.read(buffer))!=-1){
            out.write(buffer,0,bytes);
            out.flush();

        }
        out.write(-1);
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        MainServer.log("File sent to client " + clientId + " in " + elapsedTime + " milliseconds.");
        fileInputStream.close();
    }

}
