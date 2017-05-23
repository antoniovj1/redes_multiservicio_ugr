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
import com.github.sarxos.webcam.Webcam;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.imageio.ImageIO;

public class TCPServer {

    public static void main(String[] args) throws IOException {
        Webcam webcam = Webcam.getDefault();
        webcam.open();

        try (ServerSocket listener = new ServerSocket(9090)) {
            while (true) {
                try (Socket socket = listener.accept()) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    // get image
                    BufferedImage image = webcam.getImage();
                    ImageIO.write(image, "png", socket.getOutputStream());

                    socket.close();
                }
            }
        }
    }
}
