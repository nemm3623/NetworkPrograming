package NetworkPrograming9;

import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.ConcurrentHashMap;

public class ChatWhisperS extends JFrame {
    JTextArea display;
    JLabel info;
    ArrayList<ServerThread2> list;
    ConcurrentHashMap<String,ServerThread2> hash;

    public ChatWhisperS() {
        super("서버");
        info = new JLabel();
        add(info, BorderLayout.NORTH);
        display = new JTextArea("", 0, 0);
        display.setEditable(false);
        JScrollPane scroll = new JScrollPane(display);
        add(scroll, BorderLayout.CENTER);
        addWindowListener(new WinListener());
        setSize(300, 250);
        setVisible(true);
    }

    public void runServer() {
        ServerSocket server;
        Socket sock;
        ServerThread2 SThread;
        try {
            server = new ServerSocket(5001, 100);
            hash = new ConcurrentHashMap<>();
            list = new ArrayList<>();
            try {
                while (true) {
                    sock = server.accept();
                    SThread = new ServerThread2(this, sock, display, info);
                    SThread.start();
                    info.setText(sock.getInetAddress().getHostName() + " 클라이언트와 연결됨");
                }
            } catch (IOException ioe) {
                server.close();
                System.err.println(ioe.getMessage());
            }
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        }
    }

    public static void main(String[] args) {
        ChatWhisperS s = new ChatWhisperS();
        s.runServer();
    }

    static class WinListener extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
            System.exit(0);
        }
    }
}
class ServerThread2 extends Thread {
    Socket sock;
    BufferedWriter output;
    BufferedReader input;
    JTextArea display;
    JLabel info;
    String clientdata = "";
    ChatWhisperS cs;
    String ID;

    private static final String SEPARATOR = "|";
    private static final String LOGON_FAIL = "999";
    private static final String LOGON_SUCCESS = "1000";
    private static final int REQ_LOGON = 1001;
    private static final int REQ_LOGOUT = 1002;
    private static final int REQ_SENDWORDS = 1021;
    private static final int REQ_WISPERSEND = 1022;

    public ServerThread2(ChatWhisperS c, Socket s, JTextArea ta, JLabel l) {
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
        try {
            while ((clientdata = input.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(clientdata, SEPARATOR);
                int command = Integer.parseInt(st.nextToken().trim());


                switch (command) {

                    case REQ_LOGON: {

                        ID = st.nextToken();
                        if(check_id(ID)){
                            output.write(LOGON_FAIL + "\r\n");
                            output.flush();
                            ID = "";
                        }else {
                            cs.list.add(this);
                            output.write(LOGON_SUCCESS + "\r\n");
                            output.flush();
                            logon();
                        }

                        break;
                    }
                    case REQ_LOGOUT: {

                        logout(cs.list.size());

                        break;
                    }
                    case REQ_SENDWORDS: {

                        send_all(st.nextToken(),cs.list.size());

                        break;
                    }
                    case REQ_WISPERSEND: {

                        send_one(st.nextToken(), st.nextToken());

                        break;
                    }
                }
            }
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        }
        try {
            cs.list.remove(this);
            sock.close();
        } catch (IOException ea) {
            System.err.println(ea.getMessage());
        }
    }

    public boolean check_id(String new_id){
        for(String id :  cs.hash.keySet()) {
            if(id.equals(new_id))
                return true;
        }
        return false;
    }

    // 로그온 기능 구현
    public void logon(){

        display.append("클라이언트가 " + ID + "(으)로 로그인 하였습니다.\r\n");
        cs.hash.put(ID, this); // 해쉬 테이블에 아이디, 스레드 저장

        for(ServerThread2 s : cs.list) {
            try {
                if (s.ID == null)  // 로그인하지 않은 클라이언트 처리
                    continue;

                if (s.equals(this)) {   // 로그인한 클라이언트에게 이미 로그인한 클라이언트 ID를 알림
                    for (ServerThread2 SThread : cs.list) {
                        if (SThread != this && SThread.ID != null) {
                            s.output.write(SThread.ID + "(이)가 접속해 있습니다.\r\n");
                            s.output.flush();
                        }
                    }
                } else {    // 다른 클라이언트에게 로그인한 클라이언트의 접속을 알림

                    s.output.write(ID + "(이)가 접속했습니다.\r\n");
                    s.output.flush();
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public void logout(int size)throws IOException {
        display.append("클라이언트 " + ID + "(이)가 로그아웃 하였습니다.\r\n");
        for (int i = 0; i < size; i++) {
            ServerThread2 SThread = cs.list.get(i);

            if (!SThread.equals(this)) {
                SThread.output.write(ID + "(이)가 로그아웃 하였습니다.\r\n");
                SThread.output.flush();
            }else {
                output.write("로그인 후 이용할 수 있습니다." + "\r\n");
                output.flush();
            }
        }
        cs.list.remove(this);
        cs.hash.remove(ID);
    }

    // 귓속말 기능 구현
    public void send_one(String to, String m) throws IOException {

        display.append(ID + "->" + to + " : " + m + "\r\n");
        ServerThread2 SThread = (ServerThread2)cs.hash.get(ID); //전송할 클라이언트
        SThread.output.write(ID + " -> " + to + " : " + m + "\r\n"); // 귓속말 메시지를 전송한 클라이언트에 전송함
        SThread.output.flush();
        SThread = cs.hash.get(to); // 전송 받을 클라이언트
        SThread.output.write(ID + " -> " + to + " : " + m + "\r\n"); // 귓속말 메시지를 수신할 클라이언트에 전송함
        SThread.output.flush();
    }

    // 전체 채팅 구현
    public void send_all(String m, int size) throws IOException {
        display.append(ID + ": " + m + "\r\n");
        for (int i = 0; i < size; i++) {
            ServerThread2 SThread = cs.list.get(i);
            SThread.output.write(ID + ": " + m + "\r\n");
            SThread.output.flush();
        }
    }
}
