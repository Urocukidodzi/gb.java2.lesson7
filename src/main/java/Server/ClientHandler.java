package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

public class ClientHandler {

    private MyServer myServer;
    private Socket socket;
    private DataOutputStream dos;
    private DataInputStream dis;


    String login;
    String password;
    String nickname;

    public ClientHandler(MyServer myServer, Socket socket) {

        try {
            this.myServer = myServer;
            this.dis = new DataInputStream(socket.getInputStream());
            this.dos = new DataOutputStream(socket.getOutputStream());
            this.nickname = "";
            new Thread(() -> {
                try {
                    authentication();
                    readMessages();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    closeConnection();
                }
            }).start();


        } catch (IOException e) {
            throw new RuntimeException("Проблемы при создании обработчика клиента");
        }


    }

    private void closeConnection() {

        myServer.unSubscribe(this);
        myServer.broadCastMessage(nickname + " вышел из чата");
        try {
            dis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            dos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void readMessages() throws IOException {
        while (true) {
            String strFromClient = dis.readUTF();

            if (strFromClient.startsWith("/")) {

                if (strFromClient.equals("/quit")) {
                    return;
                }

                if (strFromClient.equals("/all")) {
                    allLst();
                    continue;
                }

                if (strFromClient.startsWith("/w")) {
                    String[] part = strFromClient.substring(2).split("\\s", 3);

                    if (part[0] != null && part[1] != null) {
                        if (myServer.privateMessage(part[1], nickname + " private massage: " + part[2])) {
                        } else {
                            sendMessage("пользователь не в сети");
                        }

                    } else {
                        sendMessage("ошибка в приватном сообщении");
                    }
                    continue;
                }
                continue;
            }
            myServer.broadCastMessage(nickname + ": " + strFromClient);

        }
    }

    private void allLst() {
        myServer.allList(this);
    }

    private void authentication() throws IOException {
        while (true) {
            String str = dis.readUTF();
            if (str.startsWith("/auth")) {
                String[] parts = str.split("\\s");

                Arrays.stream(parts).forEach(a-> System.out.println(a));

                if (parts.length<2){
                    sendMessage("Ошибка ввода логин/пароль");
                    continue;
                }

                String nick = myServer.getAuthService().getNickByLoginPass(parts[1], parts[2]);
                if (nick != null) {
                    if (!myServer.isNickBusy(nick)) {
                        sendMessage("/authok " + nick);
                        nickname = nick;
                        myServer.broadCastMessage(nickname + " зашел в чат");
                        myServer.subscribe(this);
                        return;
                    } else {
                        sendMessage("Учетная запись уже используется");
                    }
                } else {
                    sendMessage("Неверные логин/пароль");
                }
            }
        }
    }


    public String getNickname() {
        return nickname;
    }

    public void sendMessage(String message) {

        try {
            dos.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
