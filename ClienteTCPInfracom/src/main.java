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

        for (int i = 0; i < clientes; i++) {
            clientThread ct = new clientThread(i, clientes);
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

    public static Boolean verifyHash(File file, String hash) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("MD5");
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
        return sb.toString().equals(hash);


    }
}
