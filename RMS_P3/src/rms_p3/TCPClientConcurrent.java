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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class TCPClientConcurrent {

    public static void main(String[] args) throws IOException {

        BufferedReader in;
        Socket s;

        for (int i = 0; i < 10; i++) {
            s = new Socket("127.0.0.1", 9090);

            in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            System.out.print("Recibido: ");

            while (!in.ready()) {
            }
            System.out.println(in.readLine());

            System.out.print("\n");
            in.close();
        }
    }
}
