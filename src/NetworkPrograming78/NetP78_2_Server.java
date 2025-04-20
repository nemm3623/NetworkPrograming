package NetworkPrograming78;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;



public class NetP78_2_Server {

    public static void main(String[] args) {
        ServerSocket serverSocket;
        Socket[] socket = new Socket[2];
        int client_num = 0;
        try {
            serverSocket = new ServerSocket(8888);
            while (client_num < 2) {
                System.out.println("클라이언트 접속 대기중...");
                socket[client_num] = serverSocket.accept();
                System.out.println("클라이언트 1명 접속... " + socket[client_num++]);
            }
            new ClientHandler(socket[0], 0).start();
            new ClientHandler(socket[1], 1).start();
            serverSocket.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }


    public static class ClientHandler extends Thread {
        static Client[] client = new Client[2];
        private int my_num;

        public ClientHandler(Socket socket, int client_num) throws IOException {
            try {
                client[client_num] = new Client(socket);
                my_num = client_num;
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }

        public void run() {
            try {
                client[my_num].setName();
                while (client[0].name == null || client[1].name == null) {
                    Thread.sleep(5000);     // 두 클라이언트 동기화
                }
                if(client[my_num] == client[0]) game();
            } catch (InterruptedException | IOException e) {
                System.err.println(e.getMessage());
            }
        }

        public void game() throws InterruptedException, IOException {
            String result = "";
            for (int i = 1; i <= 10; i++) {
                for (Client c : client) {
                    c.out.println(i + "라운드 가위, 바위, 보 중 하나를 선택하세요.");
                }

                // 입력 시간에 따라 서로 결과, 라운드 시작이 달라지기 때문에 서브 스레드 사용
                Thread t0 = new Thread(() -> {client[0].setInput();});
                Thread t1 = new Thread(() -> {client[1].setInput();});

                t0.start();
                t1.start();
                t0.join();
                t1.join();

                result = rule(client[0], client[1]);

                for (Client c : client) {
                    c.out.println(i + " 라운드 " + result);
                    c.input = "";
                }
            }

            if(client[0].win > client[1].win) result = "\t최종 승자 : " + client[0].name;
            else if (client[0].win < client[1].win)  result = "\t최종 승자 : " + client[1].name;
            else result = "\t무승부";
            for(Client c : client){
                c.out.println( client[0].name+" "+client[0].win+ " : " +client[1].name+" "+client[1].win + result);
                c.close();
            }
        }

        public String rule(Client c1, Client c2) {

            if (c1.input.equals(c2.input)) {
                return (c1.name + " : " + c1.input + "\t" +c2.name + " : " + c2.input + "\t무승부 ");
            }
            else if ((c1.input.equals("가위") && c2.input.equals("보"))
                    || (c1.input.equals("보") && c2.input.equals("바위"))
                    || (c1.input.equals("바위") && c2.input.equals("가위"))) {
                c1.win++;
                return (c1.name + " : " + c1.input +"\t"+ c2.name + " : " + c2.input + "\t승자 : " + c1.name);
            }
            else if (c1.input.equals("기권패")) {
                c2.win++;
                return (c1.name + "의 기권\t승자 : " + c2.name);
            }
            else if (c2.input.equals("기권패")) {
                c1.win++;
                return (c2.name + "의 기권\t승자 : " + c1.name);
            }
            else {
                c2.win++;
                return (c1.name + " : " + c1.input +"\t"+c2.name + " : " + c2.input + "\t승자 : " + c2.name);
            }
        }
    }

    public static class Client{

        Socket socket;
        String name;
        BufferedReader in;
        PrintWriter out;
        String input;
        int win = 0;    // 최종결과 계산

        public Client(Socket socket)throws IOException{
            this.socket = socket;
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        }

        // 이름 필터링
        void setName()throws IOException{
            out.println("이름을 입력해주세요.");
            while((name = in.readLine()).isEmpty() || !name.matches("^[A-Za-z가-힣]+$")){
                out.println("한글 혹은 영어로 입력해주세요.");
            }
        }
        void setInput() {
            try {
                while (true){
                    if ((input = in.readLine()).equals("가위") ||
                            input.equals("바위") || input.equals("보")){
                        break;
                    }
                    out.println("가위, 바위, 보 중 하나를 선택하세요.");
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
        void close()throws IOException{
            in.close();
            out.close();
            socket.close();
        }
    }
}
