import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.Socket;

public class clientThread extends Thread {

    public static int clientes;

    private String log = "";
    private int id;
    private int tamFragmento;

    public clientThread(int id, int clientes, int tamFragmento) {
        this.id = id;
        this.clientes = clientes;
        this.tamFragmento = tamFragmento;
    }

    @Override
    public void run() {
        try{
            Socket clientSocket = new Socket("192.168.135.134", 25505);
            DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
            DataInputStream in = new DataInputStream(clientSocket.getInputStream());
            System.out.println("Cliente " + id + " conectado");
            log += "Cliente " + id + " conectado\n";

            long fileSize = in.readLong();
            System.out.println("File size: " + fileSize);

            String filename = "./ArchivosRecibidos/Cliente" + id + "-Prueba-" + clientes + ".txt";
            FileOutputStream fos = new FileOutputStream(filename);
            log += "Nombre Archivo: Cliente" + id + "-Prueba-" + clientes + ".txt"+ "\n";

            long startTime = System.currentTimeMillis();
            // Lee el archivo del DataInputStream y lo escribe en el FileOutputStream
            int bytes = 0;
            int current = 0;
            byte[] buffer = new byte[tamFragmento*1024];
            while (fileSize > 0 && (bytes = in.read(buffer, 0, (int)Math.min(buffer.length, fileSize))) != -1) {
                fos.write(buffer,0,bytes);
                fileSize -= bytes;      // read upto file size
                current += bytes;
            }

            fos.close();
            long stopTime = System.currentTimeMillis();
            System.out.println("Bytes leidos: " + current);
            log += "Tamaño archivo: " + current + "\n";

            long elapsedTime = stopTime - startTime;
            log += "Chronometer: " + elapsedTime + "\n";
            log += "-------------------------------------------------------\n";

            in.close();
            out.close();
            clientSocket.close();
            System.out.println("Archivo recibido");
            main.addLog(log);

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
