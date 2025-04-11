package NetworkPrograming34;

import java.awt.*;
import java.awt.event.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.text.SimpleDateFormat;
import javax.swing.*;

public class FileTest extends JFrame implements ActionListener{
    private JTextField enter;
    private JTextArea output;
    private JTextArea data;
    public FileTest() {
        super("File 클래스 테스트");
        enter = new JTextField("파일 및 디렉토리명을 입력하세요");
        enter.addActionListener(this);
        output = new JTextArea();
        data = new JTextArea();
        add(enter, BorderLayout.NORTH);
        add(output, BorderLayout.CENTER);
        add(data, BorderLayout.SOUTH);
        addWindowListener(new WinListener());
        setSize(400, 400);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e)  {
        File name = new File(e.getActionCommand());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy년 MM월 dd일 EEEE HH시 mm분 ss초");

        if(name.exists()){
            try {
                output.setText(name.getName()+"이 존재한다.\n"+
                        (name.isFile()? "파일이다.\n":"파일이 아니다.\n")+
                        (name.isDirectory()? "디렉토리이다.\n" : "디렉토리가 아니다.\n")+
                        (name.isAbsolute()? "절대경로이다. \n" : "절대경로가 아니다.\n")+
                        "마지막 수정날짜 : " + formatter.format(name.lastModified()) +
                        "\n파일의 길이는 : " + name.length() +
                        "\n파일의 경로는 : " +name.getPath() +
                        "\n절대경로는 : " + name.getAbsolutePath() +
                        "\nCanonicalPath : " + name.getCanonicalPath() +
                        "\n상위 디렉토리는 : " + name.getParent());
            } catch (IOException ex) {
                System.err.println(e.getActionCommand());
            }
        }
        if(name.isFile()){
            try {
                RandomAccessFile r = new RandomAccessFile(name, "r");
                StringBuffer buf = new StringBuffer();
                String text;
                while((text = r.readLine()) != null)
                    buf.append( text + "\n");
                data.setText(buf.toString());
            }catch (IOException E){
                System.err.println(E.getMessage());
            }
        }else if(name.isDirectory()){
            String[] directory = name.list();
            data.append("\n\n디렉토리의 내용은 : \n\n");
            for (int i=0; i < directory.length; i++)
                data.append( directory[i] + "\n");
        }else{
            output.setText( e.getActionCommand() +"은 존재하지 않는다.\n");
            data.setText("");
        }
    }
    public static void main(String args[]) {
        FileTest f = new FileTest();
    }
    class WinListener extends WindowAdapter {

        public void windowClosing(WindowEvent we) {
            System.exit(0);
        }
    }
}
