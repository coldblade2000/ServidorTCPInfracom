import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.Socket;

public class clientThread extends Thread {

    public static int clientes;

    private String log = "";
    private int id;

    public clientThread(int id, int clientes) {
        this.id = id;
        this.clientes = clientes;
    }

    @Override
    public void run() {
        try{
            Socket clientSocket = new Socket("192.168.135.134", 25505);
            DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
            DataInputStream in = new DataInputStream(clientSocket.getInputStream());
            System.out.println("Cliente " + id + " conectado");
            log += "Cliente " + id + " conectado\n";
            String handshake = in.readUTF();
            System.out.println(handshake);
            if(handshake.equals("Conection successful. Please send \"YES\" to confirm you're ready to receive file. ")) {
                out.writeUTF("YES");
                out.writeInt(id);
            }
            String barrierStatus = in.readUTF();
            System.out.println(barrierStatus);
            String hash = in.readUTF();
            System.out.println("Hash: " + hash);
            Long fileSize = in.readLong();
            System.out.println("File size: " + fileSize);

            String filename = "./ArchivosRecibidos/Cliente" + id + "-Prueba-" + clientes + ".txt";
            FileOutputStream fos = new FileOutputStream(filename);
            log += "Nombre Archivo: Cliente" + id + "-Prueba-" + clientes + ".txt"+ "\n";

            // Lee el archivo del DataInputStream y lo escribe en el FileOutputStream
            int current = 0;
            long startTime = System.currentTimeMillis();
            byte[] buffer = new byte[4*1024];
            int bytesRead = in.read(buffer);


            while ((bytesRead) > 1) {
                fos.write(buffer, 0, bytesRead);
                current += bytesRead;
                bytesRead = in.read(buffer);
            }

            fos.close();
            boolean status = main.verifyHash(new File(filename), hash);
            long stopTime = System.currentTimeMillis();
            System.out.println("Bytes leidos: " + current);
            log += "Tamaño archivo: " + current + "\n";
            log += "Status: " + (status? "OK": "ERROR") + "\n";

            long elapsedTime = stopTime - startTime;
            log += "Chronometer: " + elapsedTime + "\n";
            log += "-------------------------------------------------------\n";

            if (!status){
                System.out.println("El archivo no coincide con el hash");
                out.writeUTF("ERROR");
                out.flush();
            }else{
                System.out.println("El archivo coincide con el hash");
                out.writeUTF("OK");
                out.flush();

            }

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
