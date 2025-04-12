package NetworkProgramin78;

import java.io.*;
import java.net.Socket;

public class NetP78_2_Client {

    public static void main(String[] args) {
        try {

            Socket socket = new Socket("localhost", 8888);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
            String serverMessage;

            while ((serverMessage = in.readLine()) != null) {
                System.out.println(serverMessage);

                // 서버가 입력을 요구할 때만 입력
                if (serverMessage.contains("입력해주세요") || serverMessage.contains("선택하세요.")) {
                    String userInput = input.readLine();
                    out.println(userInput);
                }
            }

            in.close();
            out.close();
            socket.close();
            System.out.println("서버와의 연결이 종료되었습니다.");

        } catch (IOException e) {
            System.err.println("클라이언트 오류: " + e.getMessage());
        }
    }
}
