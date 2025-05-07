package NetworkPrograming9;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;

public class MultipleChatS extends JFrame {
    JTextArea display;
    JLabel info;
    ArrayList<ServerThread> list;
    public ServerThread SThread;

    public MultipleChatS() {
        super("서버");
        info = new JLabel();
        add(info, BorderLayout.NORTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        display = new JTextArea("", 0, 0);
        JScrollPane scroll = new JScrollPane(display);
        add(scroll, BorderLayout.CENTER);

        display.setEditable(false);
        setSize(300, 250);
        setVisible(true);
    }

    public void runServer() {
        ServerSocket server;
        Socket sock;
        try {
            list = new ArrayList<>();
            server = new ServerSocket(4001, 100);
            try {
                while (true) {
                    sock = server.accept();
                    SThread = new ServerThread(this, sock, display, info);
                    SThread.start();
                    info.setText(sock.getInetAddress().getHostName() + " 서버는 클라이언트와 연결됨");
                }
            } catch (IOException ioe) {
                server.close();
                System.err.println(ioe.getMessage());
            }
        }catch (IOException e){
            System.err.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        MultipleChatS s = new MultipleChatS();
        s.runServer();
    }
}

class ServerThread extends Thread {

    Socket sock;
    InputStream is;
    InputStreamReader isr;
    BufferedReader input;
    OutputStream os;
    OutputStreamWriter osw;
    BufferedWriter output;
    JTextArea display;
    JLabel info;
    String clientdata = "";
    MultipleChatS cs;


    public ServerThread(MultipleChatS c, Socket s, JTextArea ta, JLabel l) {
        sock = s;
        display = ta;
        info = l;
        cs = c;
        try {
            is = sock.getInputStream();
            isr =  new InputStreamReader(is);
            input = new BufferedReader(isr);
            os = sock.getOutputStream();
            osw = new OutputStreamWriter(os);
            output = new BufferedWriter(osw);
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        }
    }

    public void run() {
        cs.list.add(this);

        try {
            while ((clientdata = input.readLine()) != null) {
                System.out.println(clientdata);
                display.append(clientdata + "\r\n");
                int cnt = cs.list.size();
                for (int i = 0; i < cnt; i++) { //모든 클라이언트에 데이터를 전송한다.
                    ServerThread SThread = cs.list.get(i);
                    SThread.output.write(clientdata + "\r\n");
                    SThread.output.flush();
                }
            }
        }catch(IOException e){
            System.err.println(e.getMessage());
        }
        cs.list.remove(this);
        try {
            sock.close(); //소켓닫기
        } catch (IOException ea) {
            System.err.println(ea.getMessage());
        }
    }
}