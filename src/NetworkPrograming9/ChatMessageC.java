package NetworkPrograming9;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.awt.event.*;

public class ChatMessageC extends JFrame implements ActionListener, KeyListener {

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
    private static final String SEPARATOR = "|";
    private static final int REQ_LOGON =1001;
    private static final int REQ_LOGOUT =1002;
    private static final int REQ_SENDWORDS =1021;

    JPanel ptotal, plabel, pword, pane;

    public ChatMessageC() {
        super("클라이언트");
        mlbl= new JLabel("채팅 상태를 보여줍니다.");
        add(mlbl, BorderLayout.NORTH);
        display = new JTextArea("", 0, 0 );
        display.setEditable(false);
        add(new JScrollPane(display), BorderLayout.CENTER);

        ptotal = new JPanel(new BorderLayout());

        pword = new JPanel(new BorderLayout());
        wlbl = new JLabel("대화말");
        wtext = new JTextField(30);
        wtext.addKeyListener(this); // 입력된 데이터를 송신하기 위한 이벤트 연결
        pword.add(wlbl, BorderLayout.WEST);
        pword.add(wtext, BorderLayout.EAST);
        ptotal.add(pword, BorderLayout.CENTER);

        plabel = new JPanel(new BorderLayout());
        loglbl = new JLabel("ID");
        pane = new JPanel(new CardLayout());
        Itext = new JTextField(30);
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
            @Override
            public void windowClosing(WindowEvent e) {

                try {
                    if (client != null && !client.isClosed())
                        client.close();
                    System.exit(0);
                } catch (IOException ex) {
                    System.err.println(ex.getMessage());
                }
            }
        });


        setSize(400,250);
        setVisible(true);
    }

    public void runClient() {
        try {
                client = new Socket(InetAddress.getLocalHost(), 5001);
                mlbl.setText("연결된 서버이름 : " + client.getInetAddress().getHostName());
                input = new BufferedReader(new InputStreamReader(client.getInputStream()));
                output = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
                clientdata = new StringBuffer(2048);
                mlbl.setText("접속 완료 사용할 아이디를 입력하세요.");

                do {
                    serverdata = input.readLine();
                    display.append(serverdata + "\r\n");
                } while (client.isConnected());
        }catch (SocketException se) {
            System.err.println("로그아웃 하였습니다.");
        } catch (IOException e){
            System.err.println(e.getMessage());
        }
    }

    public void actionPerformed (ActionEvent ae) {
        if (ID == null) {
            ID = Itext.getText();
            mlbl.setText(ID + "(으)로 로그인 하였습니다.");
            try {
                clientdata.setLength(0);
                clientdata.append(REQ_LOGON);
                clientdata.append(SEPARATOR);
                clientdata.append(ID);
                output.write(clientdata + " \r\n");
                output.flush();
                loglbl.setVisible(false);
                Itext.setVisible(false);
                logout.setVisible(true);
                plabel.revalidate();
                plabel.repaint();

            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        } else if(ae.getActionCommand().equals("Logout")) {
            try {
                mlbl.setText(ID + " 로그아웃 하였습니다");
                clientdata.setLength(0);
                clientdata.append(REQ_LOGOUT);
                clientdata.append(SEPARATOR);
                clientdata.append(ID);
                output.write(clientdata + " \r\n");
                output.flush();
                display.setText("");
                logout.setVisible(false);
                loglbl.setVisible(true);
                Itext.setVisible(true);

            }catch (IOException e){
                System.err.println(e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        ChatMessageC c = new ChatMessageC();
        c.runClient();
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyChar() == KeyEvent.VK_ENTER) {
            String message = wtext.getText();

            if (ID == null) {
                mlbl.setText("다시 로그인 하세요!!!");
                wtext.setText("");
            } else {
                try {
                    clientdata.setLength(0);
                    clientdata.append(REQ_SENDWORDS);
                    clientdata.append(SEPARATOR);
                    clientdata.append(ID);
                    clientdata.append(SEPARATOR);
                    clientdata.append(message);
                    output.write(clientdata + "\r\n");
                    output.flush();
                    wtext.setText("");
                } catch (IOException ioe) {
                    System.err.println(ioe.getMessage());
                }
            }
        }
    }
    public void keyReleased (KeyEvent ke) {}
    public void keyTyped(KeyEvent ke) {}
}