package com.company;

import com.sun.tools.javac.Main;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.Scanner;


public class MainServer {

    // https://www.baeldung.com/a-guide-to-java-sockets
    private static BufferedWriter logBW;
    private static FileWriter logFW;
    public LinkedList<ThreadServer> threads;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private String filepath;

    public MainServer(int archivo) {
        if (archivo == 1) {
            filepath = "./Archivos/pequenio.txt";
        } else {
            filepath = "./Archivos/grande.txt";
        }
        try {
            File file = new File(filepath);
            log("El archivo seleccionado es el de " + (archivo == 1 ? "100MB" : "250MB"));
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
        int tamFragmento;
        while (true) {
            System.out.println("Cuantos clientes quieres que se conecten antes de enviar? ");
            clientes = scan.nextInt();
            System.out.println("Cual archivo quieres subir? 1 para el de 100MB o 2 para el de 250MB: ");
            archivo = scan.nextInt();
            System.out.println("Determina el tamanio de los mensajes en que se van a fragmentar los archivos (no puede ser mayor de 64 (KB)): ");
            tamFragmento = scan.nextInt();
            if (clientes < 1 || archivo < 1 || archivo > 2 || tamFragmento > 64) {
                System.out.println("Error, opciones invalidas");
            } else {
                break;
            }
        }
        log(String.format("Comenzando servidor del archivo %s para %s clientes%n", archivo, clientes));
        MainServer server = new MainServer(archivo);
        try {
            server.start(25505, clientes, archivo, tamFragmento);
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

    public void start(int port, int clientes, int archivo, int tamFragmento) throws IOException {
        log("Starting server on port "+ port);
        serverSocket = new ServerSocket(port);
        threads = new LinkedList<>();
        // Create new ThreadServer for every connection that arrives.
        while (threads.size() < clientes) {
            clientSocket = serverSocket.accept();
            MainServer.log("New connection created!");
            DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
            DataInputStream in = new DataInputStream(clientSocket.getInputStream());

            ThreadServer connectionServer = new ThreadServer(filepath, clientSocket, out, in, this, tamFragmento);
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
