package NetworkPrograming9;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;

public class NetP9_1_Server extends Frame implements ActionListener {

    TextArea display;
    TextField text;
    Label lword;
    Socket connection;
    BufferedWriter output;
    BufferedReader input;
    String clientdata = "";
    String serverdata = "";

    public NetP9_1_Server() {
        super("서버");
        display = new TextArea("", 0, 0, TextArea.SCROLLBARS_VERTICAL_ONLY);
        add(display, BorderLayout.CENTER);

        Panel pword = new Panel(new BorderLayout());
        lword = new Label("대화말");
        text = new TextField(30); //전송할 데이터를 입력하는 필드
        text.addActionListener(this); //입력된 데이터를 송신하기 위한 이벤트 연결
        pword.add(lword, BorderLayout.WEST);
        pword.add(text, BorderLayout.EAST);
        add(pword, BorderLayout.SOUTH);
        addWindowListener(new WinListener());
        setSize(300, 200);
        setVisible(true);
    }

    public void runServer() {
        ServerSocket server;
        try {

            server = new ServerSocket(4000, 100);
            while (true) {
                connection = server.accept();
                new ServerThread(connection,display).start();

            }

//            InputStream is = connection.getInputStream();
//            InputStreamReader isr = new InputStreamReader(is);
//            input = new BufferedReader(isr);
//
//            OutputStream os = connection.getOutputStream();
//            OutputStreamWriter osw = new OutputStreamWriter(os);
//            output = new BufferedWriter(osw);
//
//            while (true) {
//                clientdata = input.readLine();
//                if (clientdata.equals("quit")) {
//                    display.append("\n클라이언트와의 접속이 중단되었습니다");
//                    output.flush();
//                    break;
//                } else {
//                    display.append("\n클라이언트 메시지 :" + clientdata);
//                    output.flush();
//                }
//            }
//            connection.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void actionPerformed(ActionEvent ae) {
        serverdata = text.getText();
        try {
            display.append("\n" + serverdata);
            output.write(serverdata + "\r\n");
            output.flush();
            text.setText("");
            if (serverdata.equals("quit"))
                connection.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public static class ServerThread extends Thread {

        Socket connection;
        BufferedWriter output;
        BufferedReader input;
        String clientdata = "";
        TextArea display;

        public ServerThread(Socket connection, TextArea display) throws IOException {
            this.connection = connection;
            this.display = display;
            InputStream is = connection.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            input = new BufferedReader(isr);

            OutputStream os = connection.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os);
            output = new BufferedWriter(osw);
        }

        public void run() {
            try {
                InputStream is = connection.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                input = new BufferedReader(isr);

                OutputStream os = connection.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os);
                output = new BufferedWriter(osw);

                while (true) {
                    clientdata = input.readLine();
                    if (clientdata.equals("quit")) {
                        output.flush();
                        break;
                    } else {
                        display.append("\n클라이언트 메시지 :" + clientdata);
                        output.flush();
                    }
                }
                connection.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        NetP9_1_Server server = new NetP9_1_Server();

        server.runServer();
    }

    static class WinListener extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
            System.exit(0);
        }
    }
}
