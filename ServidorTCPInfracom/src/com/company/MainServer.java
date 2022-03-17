package com.company;

import com.sun.tools.javac.Main;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.CyclicBarrier;


public class MainServer {

    // https://www.baeldung.com/a-guide-to-java-sockets
    private static BufferedWriter logBW;
    private static FileWriter logFW;
    public LinkedList<ThreadServer> threads;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private String filepath;
    private String hash;

    //TODO Calcular tiempo de transferencia
    //TODO Recibir verificacion de que el archivo llego bien
    //TODO Handle cuando se acaba de mandar todos los archivos
    //TODO


    public MainServer(int archivo) {
        if (archivo == 1) {
            filepath = "./Archivos/pequenio.txt";
        } else {
            filepath = "./Archivos/grande.txt";
        }
        try {
            //Calculate hash of selected file
            File file = new File(filepath);
            MessageDigest md5Digest = MessageDigest.getInstance("MD5");
            hash = getFileChecksum(md5Digest, file);
            log("El archivo seleccionado es el de " + (archivo == 1 ? "100MB" : "250MB"));
            log("El hash del archivo seleccionado es: " + hash);
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();

        }
    }

    public static void main(String[] args) throws IOException {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
        LocalDateTime now = LocalDateTime.now();

        String filename = "./Logs/" + dtf.format(now) + ".txt";
        MainServer.logFW = new FileWriter(filename);
        MainServer.logBW = new BufferedWriter(MainServer.logFW);
        Scanner scan = new Scanner(System.in);

        int clientes;
        int archivo;
        while (true) {
            System.out.println("Cuantos clientes quieres que se conecten antes de enviar? ");
            clientes = scan.nextInt();
            System.out.println("Cual archivo quieres subir? 1 para el de 100MB o 2 para el de 250MB: ");
            archivo = scan.nextInt();
            if (clientes < 1 || archivo < 1 || archivo > 2) {
                System.out.println("Error, opciones invalidas");
            } else {
                break;
            }
        }
        log(String.format("Comenzando servidor del archivo %s para %s clientes%n", archivo, clientes));
        MainServer server = new MainServer(archivo);
        ThreadServer.barrier = new CyclicBarrier(clientes);
        try {
            server.start(25505, clientes, archivo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void log(String log) {
        System.out.println(log);
        try {
            MainServer.logBW.write(log + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //https://howtodoinjava.com/java/java-security/sha-md5-file-checksum-hash/
    private static String getFileChecksum(MessageDigest digest, File file) throws IOException {
        //Get file input stream for reading the file content
        FileInputStream fis = new FileInputStream(file);

        //Create byte array to read data in chunks
        byte[] byteArray = new byte[1024];
        int bytesCount = 0;

        //Read file data and update in message digest
        while ((bytesCount = fis.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        }
        ;

        //close the stream; We don't need it now.
        fis.close();

        //Get the hash's bytes
        byte[] bytes = digest.digest();

        //This bytes[] has bytes in decimal format;
        //Convert it to hexadecimal format
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        //return complete hash
        return sb.toString();
    }

    public void start(int port, int clientes, int archivo) throws IOException {
        log("Starting server on port "+ port);
        serverSocket = new ServerSocket(port);
        threads = new LinkedList<>();
        // Create new ThreadServer for every connection that arrives.
        while (threads.size() < clientes) {
            clientSocket = serverSocket.accept();
            MainServer.log("New connection created!");
            DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
            DataInputStream in = new DataInputStream(clientSocket.getInputStream());

            ThreadServer connectionServer = new ThreadServer(filepath, hash, clientSocket, out, in, this);
            threads.add(connectionServer);
            connectionServer.start();

        }
        for (ThreadServer t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        log("All threads finished");
        log("Closing server");
        MainServer.logBW.close();
        MainServer.logFW.close();
        stop();

    }

    public void stop() throws IOException {
        clientSocket.close();
        serverSocket.close();
    }

}
