package NetworkProgramin78;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;


public class NetP78_1_Client {
    public static void main(String[] args) {

        try {
            Socket socket = new Socket("localhost", 8888);
            Client_UI client_ui = new Client_UI(socket);
        } catch (IOException e){
            System.err.println("CLear");
            System.err.println(e.getMessage());
        }
    }

    public static class Client_UI extends JFrame implements ActionListener {
        private final JTextField accountField, nameField, balanceField;
        private final JButton enter, print, delete, clear;
        private RandomAccessFile output;
        DataInputStream in;
        DataOutputStream out;


        public Client_UI(Socket socket) {
            super("파일 쓰기");

            try{
                out = new DataOutputStream(socket.getOutputStream());
                in = new DataInputStream(socket.getInputStream());
            }catch (IOException e){
                System.err.println(e.getMessage());
            }

            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    try {
                        out.close();
                        socket.close();
                    } catch (IOException ex) {
                        System.err.println(ex.getMessage());
                    }
                    System.exit(0);
                }
            });
            setSize( 300, 200 );
            setLayout(new GridLayout(5,2));
            add( new Label("계좌번호"));
            accountField = new JTextField();
            add( accountField );
            add( new Label("이름"));
            nameField = new JTextField();
            add( nameField );
            add( new Label("잔고"));
            balanceField = new JTextField( 20 );
            add( balanceField );

            enter = new JButton("입력");
            enter.addActionListener( this );
            add(enter);
            print = new JButton("조회");
            print.addActionListener(this);
            add(print);
            delete = new JButton("삭제");
            delete.addActionListener(this);
            add(delete);
            clear = new JButton("Clear");
            clear.addActionListener(this);
            add(clear);
            setVisible(true);
        }

        public void actionPerformed(ActionEvent e) {
            String result;
            try {
                if (e.getSource() == enter){

                    out.writeUTF("입력");
                    out.writeInt(Integer.parseInt(accountField.getText()));
                    out.writeUTF(nameField.getText());
                    out.writeDouble(Double.parseDouble(balanceField.getText()));
                    out.flush();

                    if((result = in.readUTF()).equals("입력 완료"))
                        System.out.println(result);
                    else
                        System.err.println(result);
                }
                else if (e.getSource() == print){
                    out.writeUTF("출력");
                    out.writeInt(Integer.parseInt(accountField.getText()));
                    out.flush();
                    if((result= in.readUTF()).equals("출력 완료")) {
                        accountField.setText(String.valueOf(in.readInt()));
                        nameField.setText(in.readUTF());
                        balanceField.setText(String.valueOf(in.readDouble()));
                        System.out.println(result);
                    }
                    else
                        System.err.println(result);

                }
                else if (e.getSource() == delete){

                    out.writeUTF("삭제");
                    out.writeInt(Integer.parseInt(accountField.getText()));
                    out.flush();
                    if((result = in.readUTF()).equals("삭제 완료"))
                        System.out.println(result);
                    else
                        System.err.println(result);
                }else if (e.getSource() == clear){
                    accountField.setText("");
                    nameField.setText("");
                    balanceField.setText("");
                }
            }catch (IOException ioe){
                System.err.println(ioe.getMessage());
            }

        }
    }
}
