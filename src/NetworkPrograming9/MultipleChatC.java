package NetworkPrograming9;

import java.io.*;
import java.net.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class MultipleChatC extends JFrame implements ActionListener{
        JTextArea display;
        JTextField text;
        JLabel lword;
        BufferedWriter output;
        BufferedReader input;
        Socket client;
        String clientdata = "";
        String serverdata = "";

        public MultipleChatC() {
            super("클라이언트");
            display = new JTextArea("", 0, 0);
            display.setEditable(false);
            add(display, BorderLayout.CENTER);
            JPanel pword = new JPanel(new BorderLayout());
            lword = new JLabel("");
            text = new JTextField(30); //전송할 데이터를 입력하는 필드
            text.addActionListener(this); //입력된 데이터를 송신하기 위한 이벤트 연결
            pword.add(lword, BorderLayout.WEST);
            pword.add(text, BorderLayout.EAST);
            add(pword, BorderLayout.SOUTH);
            setSize(300, 150);
            setVisible(true);
        }

        public void runClient() {

            try {
                client = new Socket(InetAddress.getLocalHost(), 4001);
                input = new BufferedReader(new InputStreamReader(client.getInputStream()));
                output = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
                while (true) {
                    serverdata = input.readLine();
                    display.append("\r\n" + serverdata);
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
                try {
                    client.close();
                } catch (IOException ioe) {
                    System.err.println(ioe.getMessage());
                }
            }
        }
        public void actionPerformed (ActionEvent ae){
            clientdata =text.getText();
            try {
                display.append("\r\n 나의 대화말 : " + clientdata);
                output.write(clientdata + "\r\n");
                output.flush();
                text.setText("");
            }catch(IOException e){
                e.printStackTrace();
            }
        }

        public static void main (String[] args){
                MultipleChatC c = new MultipleChatC();
                c.runClient();
        }
}