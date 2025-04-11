package Netp78;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class NetP78_1_Server {
    public static void main(String[] args) {

        try(ServerSocket serverSocket = new ServerSocket(8888);
            RandomAccessFile file = new RandomAccessFile("new_costumer.txt","rw");){

            while(true) {
                Socket socket = serverSocket.accept();

                new ClientHandler(socket).start();
            }
        }catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public static class ClientHandler extends Thread {

        Socket socket;
        DataInputStream in;
        DataOutputStream out;
        RandomAccessFile file;
        Record record;
        ClientHandler(Socket socket) {
            this.socket = socket;
            try {
                in = new DataInputStream(socket.getInputStream());
                out = new DataOutputStream(socket.getOutputStream());
                file = new RandomAccessFile("new_costumer.txt","rw");
                record = new Record(file);
            }catch (IOException e){
                System.err.println(e.getMessage());
            }
        }

        public void run() {
            try {
                while(true) {
                    String input = in.readUTF();

                    switch (input) {
                        case "입력" -> enter();
                        case "출력" -> print();
                        case "삭제" -> delete();
                    }

                }
            }catch (EOFException ex){
                System.err.println("클라이언트가 접속을 종료했습니다.");
            }
            catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }

        // 계좌 등록
        void enter() throws IOException {

            int account = in.readInt();
            String name = in.readUTF();
            double balance = in.readDouble();

            if(account > 0 && account <= 100 && name.length() < 16 ){
                // 입력된 계좌가 없거나 입력된 계좌번호보다 큰 계좌번호가 아닌 경우 중복 계좌 검증
                if(file.length() > 0 && file.length() >= (account-1) * record.size()) {

                    file.seek((account - 1) * record.size());
                    int temp = file.readInt();
                    if (temp == account) {
                        out.writeUTF("이미 존재하는 계좌번호입니다.");
                        return;
                    }
                }
                // 이름 영어, 한글외의 문자 필터링
                if (!name.matches("^[A-Za-z가-힣]+$")) {
                    out.writeUTF("이름은 영어 혹은 한글만 입력해주세요.");
                    return;
                }

                file.seek((account-1) * record.size());
                record.setAccount(account);
                record.setName(name);
                record.setBalance(balance);
                record.write();
                out.writeUTF("입력 완료");
                out.flush();
            }
        }

        // 계좌 정보 출력
        void print() throws IOException{
            int account = in.readInt();

            // 등록된 계좌가 없거나 조회할 계좌번호가 존재하는 계좌번호들보다 큰 경우 배제
            if(file.length() > 0 && file.length() >= (account-1) * record.size()){
                file.seek((account-1) * record.size());
                //file.readInt();
                record.read();
                if (account == record.getAccount()){
                    out.writeUTF("출력 완료");
                    out.writeInt(record.getAccount());
                    out.writeUTF(record.getName());
                    out.writeDouble(record.getBalance());
                }
                else
                    out.writeUTF("존재하지 않는 계좌번호입니다.");
                out.flush();
            }else
                out.writeUTF("존재하지 않는 계좌번호입니다.");
            out.flush();
        }

        // 계좌 삭제
        void delete() throws IOException{
            int account = in.readInt();

            if(file.length() > 0 && file.length() >= (account-1) * record.size()) {
                file.seek((account - 1) * record.size());
                record.read();
                if (account == record.getAccount()) {
                    file.seek((account - 1) * record.size());
                    record.delete();
                    out.writeUTF("삭제 완료");
                } else {
                    out.writeUTF("존재하지 않는 계좌번호입니다.");
                }
            }else {
                out.writeUTF("존재하지 않는 계좌번호입니다.");
            }
            out.flush();
        }
    }
}

class Record{
    private int account;
    private String name;
    private Double balance;
    RandomAccessFile file;

    Record(RandomAccessFile file) throws FileNotFoundException {
        this.file = file;
    }
    public void read() throws IOException {

        account = file.readInt();   // 계좌번호 읽기
        name = file.readUTF();
        balance = file.readDouble();

    }

    public void write() throws IOException {

        file.writeInt(account);
        file.writeUTF(name);
        file.writeDouble(balance);

    }

    public void delete() throws IOException {
        file.writeInt(0);
        file.writeUTF("");
        file.writeDouble(Double.NaN);
    }

    public void setAccount(int a) { account = a; } // 구좌번호를 설정한다.
    public int getAccount() { return account; }
    public void setName(String f) { name = f; }
    public String getName() { return name; }
    public void setBalance(double b) { balance = b; }
    public double getBalance() { return balance; }
    public long size() { return 42;}
}
