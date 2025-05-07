package NetworkPrograming9;
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;


public class NetP9_1_Client extends Frame implements ActionListener {
    TextArea display;
    TextField text;
    Label lword;
    BufferedWriter output;
    BufferedReader input;
    Socket client;
    String clientdata = "";
    String serverdata = "";

    public NetP9_1_Client() {
        super("클라이언트");
        display = new TextArea("", 0, 0, TextArea.SCROLLBARS_VERTICAL_ONLY);
        display.setEditable(false);
        add(display, BorderLayout.CENTER);

        Panel pword = new Panel(new BorderLayout());
        lword = new Label("대화말");
        text = new TextField(30);
        text.addActionListener(this); //입력된 데이터를 송신하기 위한 이벤트 연결
        pword.add(lword, BorderLayout.WEST);
        pword.add(text, BorderLayout.EAST);
        add(pword, BorderLayout.SOUTH);

        addWindowListener(new WindowListener());
        setSize(300, 200);
        setVisible(true);
    }

    public void runClient() {
        try {
            client = new Socket(InetAddress.getLocalHost(), 4000);
            input = new BufferedReader(new InputStreamReader(client.getInputStream()));
            output = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));

            while (true) {
                serverdata = input.readLine();

                if (serverdata.equals("quit")) {
                    display.append("");
                    output.flush();
                    break;
                } else {
                    display.append("\n" + serverdata);
                    output.flush();
                }
            }
            client.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public void actionPerformed (ActionEvent ae) {
        clientdata = text.getText();
        try {

            display.append("\n client : " + clientdata);
            output.write(clientdata + "\r\n");
            output.flush();
            text.setText("");

            if (clientdata.equals("quit"))
                client.close();

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
    public static void main(String[] args) {
        NetP9_1_Client client = new NetP9_1_Client();
        client.runClient();
    }

    class WindowListener extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
            System.exit(0);
        }
    }
}