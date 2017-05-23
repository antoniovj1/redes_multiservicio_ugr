/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rms_p3;

/**
 *
 * @author antonio
 */
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TCPServerConcurrent extends Thread {

    Socket id;

    public TCPServerConcurrent(Socket s) {
        id = s;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(id.getInputStream()));

            System.out.print("Conexion entrante!\n");
            
            PrintWriter out = new PrintWriter(id.getOutputStream(), true);
            String data = "Ejecutado en thread: " + Thread.currentThread().getName();
            System.out.print("Enviando: " + data + "\n");
            out.print(data);
            
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(TCPServerConcurrent.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket(9090);

        while (true) {
            new TCPServerConcurrent(ss.accept()).start();
        }
    }
}
