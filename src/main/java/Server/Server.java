package Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {



    Socket socket = null;

    String outputMessege;

    public Server() {



        try (ServerSocket serverSocket = new ServerSocket(8181)) {
            System.out.println("server on\n");
            socket = serverSocket.accept();
            System.out.println("client connect\n");
            DataInputStream dis = new DataInputStream(socket.getInputStream());

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {

                        String clientMessege = null;
                        try {
                            clientMessege = dis.readUTF();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        System.out.println(clientMessege);

                        if (clientMessege.equals("/quit")) {
                            break;
                        }
                    }
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }


        while (true) {
            DataOutputStream dos = null;
            try {
                dos = new DataOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            try {
                outputMessege = reader.readLine();
                if(!(outputMessege==null||outputMessege.trim().equals(""))){
                dos.writeUTF(outputMessege);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

    public static void main(String[] args) {

        new Server();

    }
}
