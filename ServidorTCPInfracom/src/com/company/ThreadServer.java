package com.company;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class ThreadServer extends Thread {
    public static CyclicBarrier barrier;
    private final String hash;
    private final Socket clientSocket;
    private final DataOutputStream out;
    private final DataInputStream in;
    private final MainServer mainServer;
    private String filepath;

    public ThreadServer(String filepath, String hash, Socket clientSocket, DataOutputStream out, DataInputStream in, MainServer mainServer) {

        this.filepath = filepath;
        this.hash = hash;
        this.clientSocket = clientSocket;
        this.out = out;
        this.in = in;
        this.mainServer = mainServer;

    }

    @Override
    public void run() {
        try {

            MainServer.log("Server started, there are now " + mainServer.threads.size() + " threads.");
            out.writeUTF("Conection successful. Please send \"YES\" to confirm you're ready to receive file. ");
            // Makes sure client confirms they are ready
            while (true) {
                try {
                    if (in.readUTF().trim().equalsIgnoreCase("yes")) {
                        break;
                    } else {
                        out.writeUTF("Invalid command, please sent \"YES\" to confirm you're ready to receive file. ");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                MainServer.log("Waiting for other connections to send file. Currently " + (1 + barrier.getNumberWaiting()) + " threads waiting to send file.");
                out.writeUTF("Waiting for other connections to send file. Currently " + (1 + barrier.getNumberWaiting()) + " threads waiting to send file.");
                // Barrier that waits until all clients have connected
                barrier.await();
                //Send file, all clients are ready
                sendFile(filepath);
                String recieved = in.readUTF();

            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
                //Remove thread from threadlist if the connection drops or something else happens
                mainServer.threads.remove(this);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void sendFile(String path) throws Exception{
        int bytes = 0;
        File file = new File(path);
        FileInputStream fileInputStream = new FileInputStream(file);
        //Write hash to output stream
        out.writeUTF(hash);
        // send file size
        out.writeLong(file.length());
        // break file into chunks
        byte[] buffer = new byte[4*1024];
        while ((bytes=fileInputStream.read(buffer))!=-1){
            out.write(buffer,0,bytes);
            out.flush();

        }
        out.write(-1);
        fileInputStream.close();
    }

}
