package NetworkPrograming9;

import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;

public class ChatWhisperC extends Frame implements ActionListener, KeyListener{

    JTextArea display;
    JTextField wtext, Itext;
    JLabel mlbl, wlbl, loglbl;
    JButton logout;
    BufferedWriter output;
    BufferedReader input;
    Socket client;
    StringBuffer clientdata;
    String serverdata;
    String ID;
    JPanel ptotal, plabel, pword, pane;


    private static final String SEPARATOR = "|";
    private static final String LOGON_FAIL = "999";
    private static final String LOGON_SUCCESS = "1000";
    private static final int REQ_LOGON = 1001;
    private static final int REQ_LOGOUT = 1002;
    private static final int REQ_SENDWORDS = 1021;
    private static final int REQ_WISPERSEND = 1022;


    public ChatWhisperC() {
        super("클라이언트");
        mlbl = new JLabel("채팅 상태를 보여줍니다.");
        add(mlbl, BorderLayout.NORTH);

        display = new JTextArea("", 0, 0);
        display.setEditable(false);
        JScrollPane scroll = new JScrollPane(display);
        add(scroll, BorderLayout.CENTER);

        ptotal = new JPanel(new BorderLayout());
        pword = new JPanel(new BorderLayout());
        wlbl = new JLabel("대화말");
        wtext = new JTextField(25); //
        wtext.addKeyListener(this); //입력된 데이터를 송신하기 위한 이벤트 연결
        pword.add(wlbl, BorderLayout.WEST);
        pword.add(wtext, BorderLayout.EAST);
        ptotal.add(pword, BorderLayout.CENTER);

        plabel = new JPanel(new BorderLayout());
        loglbl = new JLabel("ID");
        pane = new JPanel(new CardLayout());
        Itext = new JTextField(25);
        Itext.addActionListener(this);
        pane.add(Itext,"logon");
        logout = new JButton("Logout");
        logout.addActionListener(this);
        logout.setVisible(false);
        pane.add(logout,"logout");
        plabel.add(loglbl, BorderLayout.WEST);
        plabel.add(pane, BorderLayout.EAST);
        ptotal.add(plabel, BorderLayout.SOUTH);

        add(ptotal, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                logout();
                try {
                    client.close();
                } catch (IOException ex) {
                    System.err.println(ex.getMessage());
                }
                System.exit(0);
            }
        });

        setSize(350,250);
        setVisible(true);
    }

    public void runClient() {
        try {
            client = new Socket(InetAddress.getLocalHost(), 5001);
            mlbl.setText("연결된 서버 이름 : " + client.getInetAddress().getHostName());

            input = new BufferedReader(new InputStreamReader(client.getInputStream()));
            output = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
            clientdata = new StringBuffer(2048);

            mlbl.setText("접속 완료 ! 사용할 아이디를 입력하세요.");


            while (true) {

                while (ID == null) {
                    serverdata = input.readLine();
                    if (serverdata.equals(LOGON_FAIL)){
                        mlbl.setText("이미 사용중인 ID 입니다.");
                        ID = null;
                    }else if(serverdata.equals(LOGON_SUCCESS)){
                        mlbl.setText("로그인 성공 !");
                        display.setText("");
                        Itext.setVisible(false);
                        loglbl.setVisible(false);
                        logout.setVisible(true);
                        break;
                    }
                    output.flush();
                }

                serverdata = input.readLine();
                display.append(serverdata + "\r\n");
                output.flush();
            }
        }catch (IOException e){
            System.err.println(e.getMessage());
        }
    }

    public void actionPerformed (ActionEvent ae) {
        if (ID == null) {
            try {

                ID = Itext.getText();
                clientdata.setLength(0);
                clientdata.append(REQ_LOGON);
                clientdata.append(SEPARATOR);
                clientdata.append(ID);
                output.write(clientdata + "\r\n");
                output.flush();
                Itext.setText("");

            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }else if(ae.getActionCommand().equals("Logout")) {
            logout();
        }
    }

    public static void main(String[] args) {
        ChatWhisperC c = new ChatWhisperC();
        c.runClient();
    }

    // 로그아웃 기능
    public void logout(){
        try {
            mlbl.setText(" 로그아웃 하였습니다");
            output.write(REQ_LOGOUT + SEPARATOR + "\r\n");
            output.flush();
            logout.setVisible(false);
            loglbl.setVisible(true);
            Itext.setVisible(true);
            display.setText("");
            ID = null;
            System.out.println("Logout after -> " + ID);
        }catch (IOException e){
            System.err.println(e.getMessage());
        }
    }

    public void keyPressed(KeyEvent ke) {
        if (ke.getKeyChar() == KeyEvent.VK_ENTER) {
            String message = wtext.getText();
            StringTokenizer st = new StringTokenizer(message, " ");
            if (ID == null) {
                mlbl.setText("다시 로그인하세요!!!");
                wtext.setText("");
            } else {
                try {
                    if (st.nextToken().equals("/w")) {
                        String WID = st.nextToken();
                        StringBuilder Wmessage = new StringBuilder(st.nextToken());
                        while (st.hasMoreTokens()) {
                            Wmessage.append(" ").append(st.nextToken());
                        }
                        clientdata.setLength(0);
                        clientdata.append(REQ_WISPERSEND);
                        clientdata.append(SEPARATOR);
                        clientdata.append(WID);
                        clientdata.append(SEPARATOR);
                        clientdata.append(Wmessage);
                        output.write(clientdata + "\r\n");
                        output.flush();
                        wtext.setText("");
                    } else {
                        clientdata.setLength(0);
                        clientdata.append(REQ_SENDWORDS);
                        clientdata.append(SEPARATOR);
                        clientdata.append(message);
                        output.write(clientdata + "\r\n");
                        output.flush();
                        wtext.setText("");
                    }
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            }
        }
    }
    public void keyReleased (KeyEvent ke) {}
    public void keyTyped(KeyEvent ke) {}
}