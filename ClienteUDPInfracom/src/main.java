import java.io.*;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.security.MessageDigest;

public class main {

    public static List<clientThread> sockets = new ArrayList<clientThread>();
    public static String log = "";

    public static void main(String[] args) throws InterruptedException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Ingrese el numero de clientes: ");
        int clientes = sc.nextInt();
        System.out.println("Determina el tamanio de los mensajes en que se van a fragmentar los archivos (no puede ser mayor de 64 (KB)): ");
        int tamFragmento = sc.nextInt();

        for (int i = 0; i < clientes; i++) {
            clientThread ct = new clientThread(i, clientes, tamFragmento);
            ct.start();
            sockets.add(ct);
        }
        for (int i = 0; i < clientes; i++) {
            sockets.get(i).join();
        }
        writeLog(log);
    }

    public static void writeLog(String message){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
        LocalDateTime now = LocalDateTime.now();

        String filename = "./Logs/" + dtf.format(now) + ".txt";
        try{
            FileWriter fw = new FileWriter(filename, true);
            PrintWriter pw = new PrintWriter(fw);
            pw.println(message);
            pw.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void addLog(String message){
        log += message + "\n";
    }

}
