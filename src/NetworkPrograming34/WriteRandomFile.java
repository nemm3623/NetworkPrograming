package NetworkPrograming34;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class WriteRandomFile extends JFrame implements ActionListener {
    private final JTextField accountField, nameField, balanceField;
    private final JButton enter, done, delete;
    private RandomAccessFile output;
    private Record data;

    public WriteRandomFile() {
        super("파일 쓰기");
        data = new Record();

        try{
            output = new RandomAccessFile(new File("costumer.txt"),"rw");
        }catch (IOException e){
            System.err.println(e.getMessage());
        }

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        setSize( 300, 150 );
        setLayout( new GridLayout( 4, 2));
        add( new Label("구좌번호"));
        accountField = new JTextField();
        add( accountField );
        add( new Label("이름"));
        nameField = new JTextField( 20 );
        add( nameField );
        add( new Label("잔고"));
        balanceField = new JTextField( 20 );
        add( balanceField );
        enter = new JButton("입력");
        enter.addActionListener( this );
        add(enter);
        done = new JButton("출력");
        done.addActionListener(this);
        delete = new JButton("출력");
        delete.addActionListener(this);
        add(done);
        setVisible(true);

    }

    public void findRecord() {
        int accountNo = 0;

        // 계좌번호만 공백이 아니도록 설정
        if (!accountField.getText().isEmpty()) {
            accountNo = Integer.parseInt(accountField.getText());
            if (accountNo > 0 && accountNo <= 100) {
                try {
                    output.seek((long) (accountNo - 1) * Record.size());
                    data.read(output);
                    nameField.setText(data.getName());
                    balanceField.setText(String.valueOf(data.getBalance()));
                } catch (IOException | IllegalStateException e) {
                    System.err.println(e.getMessage());
                }
            }
        } else if (!nameField.getText().isEmpty()) {

            String name = nameField.getText();
            char[] s = new char[name.length()];
            try {

                while ((long)(accountNo = output.readInt()) * Record.size() < output.length()) {
                    System.out.println(accountNo);
                    for (int i = 0; i < name.length(); i++){
                        s[i] = output.readChar();
                    }

                    if (name.equals(new String(s))) {

                        output.seek((long) (accountNo - 1) * Record.size());
                        data.read(output);
                        accountField.setText(String.valueOf(data.getAccount()));
                        balanceField.setText(String.valueOf(data.getBalance()));
                    }
                    output.seek((long) (++accountNo - 1) * Record.size());
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }


    public void addRecord(){
        int accountNo = 0;
        double d;

        if(!accountField.getText().isEmpty()){
            try {
                accountNo = Integer.parseInt(accountField.getText());
                String name = nameField.getText();
                if(accountNo > 0 && accountNo <= 100) {

                    // 중복된 계좌 검증
                    if(output.length() > 0 && output.length() > (long) accountNo * Record.size()) {
                        output.seek((long) (accountNo - 1) * Record.size());
                        int temp = output.readInt();
                        if (temp == accountNo) {
                            System.err.println("이미 존재하는 계좌번호입니다.");
                            return;
                        }
                        output.seek((long) (accountNo - 1) * Record.size());
                        char[] arr = new char[15];
                        for (int i = 0; i < Record.size();) {
                            output.readInt();
                            for (int j = 0; j < arr.length; j++)
                                arr[j] = output.readChar();
                            if(name.equals(new String(arr))){
                                System.err.println("이미 존재하는 이름입니다.");
                                return;
                            }
                        }

                    }

                    // 이름 필터링
                    if (!nameField.getText().matches("^[A-Za-z가-힣]+$")) {
                        System.err.println("이름은 영어 혹은 한글만 입력해주세요.");
                        return;
                    }

                    data.setAccount(accountNo);
                    data.setName(nameField.getText());
                    d = Double.parseDouble(balanceField.getText());
                    data.setBalance(d);

                    output.seek((long) (accountNo - 1) * Record.size());
                    data.write(output);
                }
                accountField.setText("");
                nameField.setText("");
                balanceField.setText("");

            }catch (NumberFormatException nfe){
                System.err.println(nfe.getMessage() + "-> 숫자를 입력하세요.");
            }catch (IOException e){
                System.err.println(e.getMessage() + "-> 파일 쓰기 에러");
            }
        }
    }

    public void actionPerformed(ActionEvent e){

        if(e.getSource() == enter)
            addRecord();    // 입력데이터 저장
        else if (e.getSource() == done) {
            findRecord();   // 해당 계좌에 해당하는 정보 출력
        }
    }

    public static void main(String[] args) {
        new WriteRandomFile();
    }
}


class Record{
    private int account;
    private String name;
    private Double balance;

    public void read(RandomAccessFile file) throws IOException {

        account = file.readInt();   // 계좌번호 읽기

        char[] namearr = new char[15];

        for (int i = 0; i < namearr.length; i++)
            namearr[i] = file.readChar();

        name = new String(namearr);
        balance = file.readDouble();


    }

    public void write(RandomAccessFile file) throws IOException {

        StringBuffer buf;
        file.writeInt(account);

        if (name != null)
            buf = new StringBuffer(name);
        else
            buf = new StringBuffer(15);

        buf.setLength(15); // 이름을 저장하는 메모리 크기를 15로 설정
        file.writeChars(buf.toString());
        file.writeDouble(balance);

    }

    public void setAccount(int a) { account = a; } // 구좌번호를 설정한다.
    public int getAccount() { return account; }
    public void setName(String f) { name = f; }
    public String getName() { return name; }
    public void setBalance(double b) { balance = b; }
    public double getBalance() { return balance; }
    public static int size() { return 42;}
}
