package NetworkPrograming9;

import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;

public class ChatMessageS extends JFrame {
    JTextArea display;
    JLabel info;
    ArrayList<ServerThread1> list;


    public ChatMessageS() {
        super("서버");

        info = new JLabel();
        add(info, BorderLayout.NORTH);

        display = new JTextArea("", 0, 0);
        add(new JScrollPane(display),BorderLayout.CENTER);

        display.setEditable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300,250);
        setVisible(true);
    }

    public void runServer() {
        ServerSocket server;
        Socket sock;
        ServerThread1 SThread;

        try {
            list =new ArrayList<>();
            server = new ServerSocket(5001, 100);
            try {
                while(true) {
                    sock = server.accept();
                    SThread = new ServerThread1(this, sock, display, info);
                    SThread.start();
                    info.setText(sock.getInetAddress().getHostName() + " 서버는 클라이언트와 연결됨");
                }
            } catch (IOException ioe) {
                server.close();
                System.err.println(ioe.getMessage());
            }
        } catch (IOException ioe){
            System.err.println(ioe.getMessage());
        }
    }

    public static void main(String[] args) {
        ChatMessageS cms = new ChatMessageS();
        cms.runServer();
    }
}

class ServerThread1 extends Thread {
    Socket sock;
    BufferedWriter output;
    BufferedReader input;
    JTextArea display;
    JLabel info;
    String clientdata;
    String ID;
    ChatMessageS cs;

    private static final String SEPARATOR = "|";
    private static final int REQ_LOGON = 1001;
    private static final int REQ_LOGOUT = 1002;
    private static final int REQ_SENDWORDS = 1021;

    public ServerThread1(ChatMessageS c, Socket s, JTextArea ta, JLabel l) {

        sock = s;
        display = ta;
        info = l;
        cs = c;

        try {
            input = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            output = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        }
    }

    public void run() {
        cs.list.add(this);
        try {
            while ((clientdata = input.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(clientdata, SEPARATOR);
                int command = Integer.parseInt(st.nextToken());
                int cntcs = cs.list.size();

                switch (command) {
                    case REQ_LOGON: { // 아이디를 수신한 경우
                        ID = st.nextToken();

                        display.append("클라이언트가 " + ID + "(으)로 로그인 하였습니다.\r\n");
                        break;
                    }
                    case REQ_SENDWORDS: {

                        String ID = st.nextToken();
                        String message = st.nextToken();
                        display.append(ID + " : " + message + "\r\n");

                        for (int i = 0; i < cntcs; i++) {
                            ServerThread1 SThread = cs.list.get(i);
                            SThread.output.write(ID + " : " + message + "\r\n");
                            SThread.output.flush();
                        }
                        break;
                    }
                    case REQ_LOGOUT: {
                        String ID = st.nextToken();

                        logout(cs.list.size());
                        break;
                    }
                }
            }
        }catch (SocketException se){
            System.err.println("로그아웃 성공");
        } catch (IOException ioe){
            System.err.println(ioe.getMessage());
        }
        cs.list.remove(this);
        try {
            sock.close();
        }catch (IOException ioe){
            System.err.println(ioe.getMessage());
        }
    }

    public void logout(int size)throws IOException {
        display.append("클라이언트 " + ID + "(이)가 로그아웃 하였습니다.\r\n");
        for (int i = 0; i < size; i++) {
            ServerThread1 SThread = cs.list.get(i);

            if (!SThread.equals(this)) {
                SThread.output.write(ID + "(이)가 로그아웃 하였습니다.\r\n");
                SThread.output.flush();
            }else {
                output.write("로그인 후 이용할 수 있습니다." + "\r\n");
                output.flush();
            }
        }
        cs.list.remove(this);
    }
}