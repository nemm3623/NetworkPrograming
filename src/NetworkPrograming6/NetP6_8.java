package NetworkPrograming6;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class NetP6_8 extends Frame implements ActionListener {

    private TextField enter;
    private TextArea info, contents;

    public NetP6_8(){

        super("호스트 파일 읽기");
        setLayout( new BorderLayout());

        enter = new TextField( "URL을 입력하세요");
        enter.addActionListener(this);
        add(enter, BorderLayout.NORTH);

        info = new TextArea("", 0, 0, TextArea.SCROLLBARS_VERTICAL_ONLY);;
        add(info, BorderLayout.CENTER);

        contents = new TextArea("", 0, 0, TextArea.SCROLLBARS_VERTICAL_ONLY);
        add( contents, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        setSize(350, 650);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        URL url;
        InputStream is;
        BufferedReader input;
        String line;
        StringBuffer buffer = new StringBuffer();
        String location= e.getActionCommand();



        try {
            url = new URL( location );
            URLConnection connection = url.openConnection();
            is = url.openStream(); // location(호스트)과 연결시키는 InputStream 객체생성
            input = new BufferedReader(new InputStreamReader(is));

            info.setText("프로토콜 : " + url.getProtocol() + "\n"
                    + "호스트 네임 : " + url.getHost() + "\n"
                    + "포트 번호 : " + url.getPort() + "\n"
                    + "파일 명 : "  + url.getFile() + "\n"
                    + "해시 코드 : " + url.hashCode() + "\n"
            );

            if(connection.getContentType().startsWith("text/")){
                contents.setText("...");
                while ((line= input.readLine()) != null) //
                    buffer.append(line).append('\n');
                contents.setText(buffer.toString()); // 200
            } else if (connection.getContentType().startsWith("image/")) {
                JFrame newframe = new JFrame();
                newframe.add(new JLabel(new ImageIcon(url)));
                newframe.setSize(350,350);
                newframe.setVisible(true);
            }
            input.close();
        }catch(MalformedURLException mal) {
            contents.setText("URL [0]");
        }catch (IOException io ) {
            contents.setText(io.toString());
        }catch (Exception E){
            contents.setText("호스트의 컴퓨터만 파일을 열 수 있습니다.");
        }
    }

    public static void main(String[] args) {
        NetP6_8 frame = new NetP6_8();
    }
}
