package NetworkPrograming45;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class GetHostInfor extends JFrame implements ActionListener {
    JTextField hostname;
    JButton getinfor, localinfor;
    JTextArea display1, display2;

    public GetHostInfor(String str) {
        super(str);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        setLayout(new BorderLayout());

        JPanel inputpanel1 = new JPanel(); // 첫 번째 패널
        inputpanel1.setLayout(new BorderLayout());

        localinfor = new JButton("로컬 호스트 정보 얻기");
        inputpanel1.add("North", localinfor);
        localinfor.addActionListener(this);

        display1 = new JTextArea("", 10, 0);
        inputpanel1.add("Center", display1);
        add("North", inputpanel1);


        JPanel inputpanel2 = new JPanel(); // 두 번째 패널
        inputpanel2.setLayout(new BorderLayout());
        inputpanel2.add("North", new JLabel("호스트 이름:"));

        hostname = new JTextField("", 30);

        getinfor = new JButton("원격 호스트 정보 얻기");
        inputpanel2.add("Center", hostname);
        inputpanel2.add("South", getinfor);
        getinfor.addActionListener(this);
        add("Center", inputpanel2);

        Panel outputpanel = new Panel();
        outputpanel.setLayout(new BorderLayout());

        display2 = new JTextArea("", 10, 0);
        display2.setEditable(false);
        outputpanel.add("North", new Label("원격 호스트 정보"));
        outputpanel.add("Center", display2);
        add("South", outputpanel);
        setSize(270, 500);
    }

    public void actionPerformed(ActionEvent e) {

        String name;
        StringBuilder info = new StringBuilder();

        if (e.getSource() == localinfor)
            name = "127.0.0.1";
        else
            name = hostname.getText();

        try {
            InetAddress inet = InetAddress.getByName(name);

            info.append("Host Name : ").append(inet.getHostName()).append("\n");

            info.append("Class : ").append(getNetworkClass(inet)).append("\n");

            info.append("Host Address : ").append(inet.getHostAddress()).append("\n");

            info.append("Canonical Host Name : ").append(inet.getCanonicalHostName()).append("\n");

            info.append("HashCode : ").append(inet.hashCode()).append("\n");

        } catch (UnknownHostException ue) {
            String ip = name + "해당 호스트가 없습니다 \n";
            info.append(ip);
        }finally {
            if(e.getSource() == localinfor){
                info.append("로컬 호스트 루프백 주소 : ").append(name).append("\n");
                display1.setText(info.toString());
            }
            else
                display2.setText(info.toString());
        }
    }

    public String getNetworkClass(InetAddress ip) {

        int i = ip.getAddress()[0] & 0xFF;

        if (i <= 126)
            return "A";
        else if (i == 127)
            return "Localhost";
        else if (i <= 191)
            return "B";
        else if (i <= 223)
            return "C";
        else if (i <= 239)
            return "D";
        else if (i <= 255)
            return "E";
        else
            return "해당되는 클래스가 없습니다.";

    }

    public static void main(String[] args) {
        GetHostInfor host = new GetHostInfor("InetAddress 클래스");
        host.pack();
        host.setVisible(true);
    }
}
