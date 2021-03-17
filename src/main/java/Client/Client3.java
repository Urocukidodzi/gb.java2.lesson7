package Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Client3 {

    String userName = "Клиент";
    Windows window;

    private final String SERVER_HOST = "localhost";
    private final int SERVER_PORT = 8181;

    private JTextField msgInputField;
    private JTextArea chatArea;

    private Socket socket;
    private DataOutputStream dos;
    private DataInputStream dis;

    private boolean isAuthorized;

    public Client3() {
        try {
            openConnect();
        } catch (IOException e) {
            System.out.println("нет соединения");
        }
        window = new Windows(userName, this);
    }



    private void openConnect() throws IOException {

        socket = new Socket(SERVER_HOST, SERVER_PORT);

        dis = new DataInputStream(socket.getInputStream());
        dos = new DataOutputStream(socket.getOutputStream());
        setAuthorized(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    while (true) {
                        String serverMessege = dis.readUTF();

                        if (serverMessege.startsWith("/")) {
                            if (serverMessege.startsWith("/authok")) {
                                Client3.Windows.text = Client3.Windows.text.concat("Welcome to the CHAT" + "\n");
                                Client3.Windows.setInArea();
                                String[] part = serverMessege.split("\\s");
//                                Arrays.stream(part).forEach(a-> System.out.println(a));
//                                System.out.println(serverMessege);
                                window.windowsName(part[1]);

                                continue;
                            }
                            if (serverMessege.equals("/quit")) {
                                break;
                            }
                        } else {
                            Client3.Windows.text = Client3.Windows.text.concat("[" + Client3.Windows.date() + "]" + serverMessege + "\n");
                            Client3.Windows.setInArea();
                        }
                    }
                } catch (IOException e) {
                    System.out.println("connection closed");
                }
            }
        }).start();
    }

    public void closeConnection() {
        try {
            dis.close();
        } catch (IOException e) {
            e.printStackTrace();

        } catch (NullPointerException ignored) {
        }
        try {
            dos.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException ignored) {
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException ignored) {
        }
    }

    public static void main(String[] args) {

        Client client = new Client();

    }

    public boolean isAuthorized() {
        return isAuthorized;
    }

    public void setAuthorized(boolean authorized) {
        isAuthorized = authorized;
    }

    static class Windows extends JFrame {

        static String text = "";

        static String name;

        static Client3 client;

        static JTextField field = new JTextField();
        static JTextArea area = new JTextArea();
        static JScrollPane scroll = new JScrollPane(area);


        public static void setInArea() {
            area.setText("" + text);
        }

        public void windowsName(String name){
            setTitle(name);
        }

        public Windows(String name, Client3 client) {
            this.client = client;
            this.name = name;

            area.setEditable(false);
            area.setLineWrap(true);
            windowsName("Please LogIn");


            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            setBounds(200, 200, 600, 600);

            //JScrollPane jscroll = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            // jscroll.setSize(new Dimension(20,100));

            JButton btnSend = new JButton("Sent");

            field.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {
                }

                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getExtendedKeyCode() == 10 && !(field.getText().trim().equals(""))) {
                        text = text.concat(" " + name + " " + date() + ": " + field.getText() + "\n");
                        area.setText("" + text);
                        sendMessege(field.getText());
                        field.setText("");
                    } else if (e.getExtendedKeyCode() == 10 ){
                        field.setText("");
                    }
                }

                @Override
                public void keyReleased(KeyEvent e) {
                }

            });
            btnSend.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 1 && !(field.getText().trim().equals(""))) {

                        text = text.concat(" " + name + " " + date() + ": " + field.getText() + "\n");

                        area.setText("" + text);

                        sendMessege(field.getText());

                        field.setText("");


                    }  else if (e.getClickCount() == 1 ) {
                        field.setText("");
                    }
                }
            });

            JPanel upPanel = new JPanel();
            JPanel sendPanel = new JPanel();

            upPanel.setSize(250, 250);


            upPanel.setLayout(new BorderLayout());
            sendPanel.setLayout(new BorderLayout());
            //area.add(jscroll);
//        textField.setSize(1500,500);
            btnSend.setSize(50, 20);

            sendPanel.add(field, BorderLayout.CENTER);
            sendPanel.add(btnSend, BorderLayout.EAST);


            upPanel.add(sendPanel, BorderLayout.SOUTH);
            //       upPanel.add(btn5, BorderLayout.WEST);
//        upPanel.add(jscroll, BorderLayout.EAST);
//       upPanel.add(btn4, BorderLayout.NORTH);
            upPanel.add(scroll, BorderLayout.CENTER);

            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    super.windowClosing(e);
                    try {
                        client.dos.writeUTF("/quit");

                    } catch (IOException ignored) {
                    } catch (NullPointerException ignored) {
                    } finally {
                        client.closeConnection();
                    }
                }
            });


            add(upPanel);
            setVisible(true);

        }

        private void sendMessege(String text) {
            try {
                client.dos.writeUTF(text);
            } catch (Exception e) {
                System.out.println("Сообщение не отправлено");
            }
        }


        private static String date() {

            Date date = new Date();

            SimpleDateFormat ft =
                    new SimpleDateFormat("hh:mm:ss");

            return ft.format(date);


        }

    }

}
