package Server;

import Client.Client;
import Server.Interface.AuthService;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MyServer {

    private final int PORT = 8181;

    private List<ClientHandler> clients;
    private AuthService authService;



    public AuthService getAuthService() {
        return authService;
    }

    public MyServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            authService = new BaseAuthService() {
            };
            authService.start();
            clients = new ArrayList<>();
            while (true) {
                System.out.println("Server wait to connection");
                Socket socket = serverSocket.accept();
                System.out.println("new client connected");
                new ClientHandler(this, socket);
            }


        } catch (IOException e) {
            System.out.println("server error");
        } finally {
            if (authService != null) {
                authService.stop();
            }
        }
    }

    public void subscribe(ClientHandler c) {
        clients.add(c);
    }

    public void unSubscribe(ClientHandler c) {
        clients.remove(c);
    }

    public synchronized boolean isNickBusy(String nick) {
        for (ClientHandler c : clients) {
            if (c.getNickname().equals(nick)) {
                return true;
            }
        }
        return false;
    }

    public synchronized void broadCastMessage (String message){
        for (ClientHandler c: clients) {
            c.sendMessage(": " + message);
        }
    }
    public synchronized boolean privateMessage(String nickName, String message) {
        for (ClientHandler c: clients) {
            if (c.nickname.equals(nickName)){
                c.sendMessage(message);
                return true;
            }
        }
        return false;
    }


    public void allList(ClientHandler client) {
        for (ClientHandler c: clients) {
            client.sendMessage(c.nickname);
        }
    }
}
