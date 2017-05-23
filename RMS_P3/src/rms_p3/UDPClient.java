/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rms_p3;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 *
 * @author antonio
 */
public class UDPClient {

    public static void main(String[] argumentos) throws UnknownHostException, IOException {
        int puerto = 8767;
        String direccionServidor = "127.0.0.1";
        // por si queremos especificar la dirección IP y el puerto por  la línea de comandos
        if (argumentos.length == 2) {
            direccionServidor = argumentos[0];
            puerto = Integer.parseInt(argumentos[1]);

        }
        // Creamos un cliente
        new UDPClient(direccionServidor, puerto);
    }

    // Constructor de esta clase
    private UDPClient(String direccionServidor, int puerto) throws SocketException, UnknownHostException, IOException {

        //1. Creamos el socket
        DatagramSocket clientSocket = new DatagramSocket();

        //2. Enviamos un mensaje al servidor para pedir la imagen
        byte[] sendData;
        String sentence = "imagen";
        sendData = sentence.getBytes();

        DatagramPacket sendPacket;
        sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(direccionServidor), puerto);

        clientSocket.send(sendPacket);

        //3. Recibimos el número de datagramas que se va a recibir
        //4. Recibimos y almacenamos la información del servidor
        //5. Creamos un stream de bytes ByteArrayInputStream para ir leyendo la información recibida
        //6. Leemos el primer dato que es el número de filas
        //7. Leemos el segundo dato que es el número de columnas
        
        byte[] datos = new byte[2048];

        DatagramPacket receivePacket = new DatagramPacket(datos, datos.length);

        clientSocket.receive(receivePacket);
        datos = receivePacket.getData();

        int nD = 0;
        int filas = 0;
        int columnas = 0;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        byte[] aux = new byte[4];
        for (int i = 0; i < datos.length; i += 4) {
            aux[0] = datos[i];
            aux[1] = datos[i + 1];
            aux[2] = datos[i + 2];
            aux[3] = datos[i + 3];

            switch (i) {
                case 0:
                    nD = byteArrayToInt(aux);
                    break;
                case 4:
                    filas = byteArrayToInt(aux);
                    break;
                case 8:
                    columnas = byteArrayToInt(aux);
                    break;
                default:
                    baos.write(aux);
            }
        }

        System.err.println("Numero de datagrmas: " + nD);
        System.err.println("Filas: " + filas);
        System.err.println("Columnas: " + columnas);

        int i = 0;
        while (i < nD) {
            clientSocket.receive(receivePacket);
            datos = receivePacket.getData();
            baos.write(datos);
            i++;
        }

        byte[] imgB = baos.toByteArray();
        int[] img = new int[imgB.length / 4];

        int c = 0;
        for (i = 0; i < imgB.length; i += 4) {
            aux[0] = imgB[i];
            aux[1] = imgB[i + 1];
            aux[2] = imgB[i + 2];
            aux[3] = imgB[i + 3];

            img[c] = byteArrayToInt(aux);
            c++;
        }

        //8. Copiamos los pixeles recibidos a un objeto imagen
        BufferedImage image = new BufferedImage(columnas, filas, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, columnas, filas, img, 0, columnas);

        //9. Representamos la imagen
        JLabel label = new JLabel(new ImageIcon(image));
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(label);
        f.pack();
        f.setLocation(200, 200);
        f.setVisible(true);
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
