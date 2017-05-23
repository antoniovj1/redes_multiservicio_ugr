/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rms_p3;

import com.github.sarxos.webcam.Webcam;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;

/**
 *
 * @author antonio
 */
public class UDPServer {

    public static void main(String[] argumentos) throws SocketException, IOException, InterruptedException {
        int puerto = 8767;
        // por si queremos especificar el puerto por la línea de comandos
        if (argumentos.length > 0) {
            puerto = Integer.parseInt(argumentos[0]);
        }
        // Creamos una objeto de esta clase, pasando como argumento el puerto donde debe escuchar
        new UDPServer(puerto);
    }
    // Constructor de esta clase

    private UDPServer(int puerto) throws SocketException, IOException, InterruptedException {

        byte[] receiveData = new byte[2048];

        Webcam webcam = Webcam.getDefault();
        webcam.open();

        //   1. Creamos el socket
        DatagramSocket serverSocket = new DatagramSocket(puerto);

        //   2. Esperamos un mensaje
        while (true) {

            //   3. Almacenamos dir. IP y puerto en el datagrama a enviar
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);
            
            String sentence = new String(receivePacket.getData());
            System.err.println("Peticion recibida: " + sentence);
            
            InetAddress IPAddress = receivePacket.getAddress();
            int port = receivePacket.getPort();

            //   4. Abrimos la cámara y capturamos una imagen
            BufferedImage image = webcam.getImage();

            //   5. Copiamos la imagen a un stream (ByteArrayOutputStream)
            //      Para ello, añadimos primero el número de filas (getHeight),
            //      luego el número de columnas (getWidth) y por último los
            //      pixeles RGB (getRGB)         
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            int filas = image.getHeight();
            int columnas = image.getWidth();
            int pixeles[] = image.getRGB(0, 0, columnas, filas, null, 0, columnas);

            int nDat = (Integer.BYTES * 2 + pixeles.length * Integer.BYTES) / 2048;
            
            baos.write(toBytes(nDat));
            baos.write(toBytes(filas));
            baos.write(toBytes(columnas));

            for (int i : pixeles) {
                baos.write(toBytes(i));
            }

            while (baos.size() % 2048 != 0) {
                baos.write((byte) 0);
            }

            byte[] datos = baos.toByteArray();

            //   7. Transmitimos el número de datagramas que se van a enviar 
            //   8. Transmitimos los datagramas que contienen los datos de la
            //      imagen (se recomienda usar la función sleep entre cada
            //      envío)
            for (int i = 0; i < datos.length; i += 2048) {
                byte a[] = Arrays.copyOfRange(datos, i, i + 2047);
                DatagramPacket packet = new DatagramPacket(a, a.length, IPAddress, port);
                serverSocket.send(packet);
                Thread.sleep(10);
            }
        }
    }

    // Funcion que convierte un entero a bytes
    public static byte[] toBytes(int i) {
        byte[] resultado = new byte[4];
        resultado[0] = (byte) (i >> 24);
        resultado[1] = (byte) (i >> 16);
        resultado[2] = (byte) (i >> 8);
        resultado[3] = (byte) (i /*>> 0*/);
        return resultado;
    }

    public static int byteArrayToInt(byte[] b) {
        switch (b.length) {
            case 4:
                return b[0] << 24 | (b[1] & 0xff) << 16 | (b[2] & 0xff) << 8 | (b[3] & 0xff);
            case 2:
                return 0x00 << 24 | 0x00 << 16 | (b[0] & 0xff) << 8 | (b[1] & 0xff);
            case 1:
                return b[0];
            default:
                return 0;
        }
    }
}
