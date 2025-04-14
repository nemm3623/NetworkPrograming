package NetworkPrograming8;

import java.io.*;
import java.net.*;

public class GameServer {
    private static final int PORT = 12345;
    private static final int MAX_ROUNDS = 10;

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("서버가 시작되었습니다. 클라이언트 접속 대기 중...");

        Socket player1 = serverSocket.accept();
        System.out.println("플레이어 1 연결 됨");
        DataInputStream dis1 = new DataInputStream(player1.getInputStream());
        DataOutputStream dos1 = new DataOutputStream(player1.getOutputStream());

        Socket player2 = serverSocket.accept();
        System.out.println("플레이어 2 연결 됨");
        DataInputStream dis2 = new DataInputStream(player2.getInputStream());
        DataOutputStream dos2 = new DataOutputStream(player2.getOutputStream());

        String name1 = dis1.readUTF();
        String name2 = dis2.readUTF();

        int score1=0, score2=0;

        for(int i=0; i<MAX_ROUNDS; i++) {
            dos1.writeUTF("[" + (i+1) + "라운드 ] 가위, 바위, 보! : ");
            dos2.writeUTF("[" + (i+1) + "라운드 ] 가위, 바위, 보! : ");

            String choice1 = normallizeChoice(dis1.readUTF());
            String choice2 = normallizeChoice(dis2.readUTF());

            int result = determineWinner(choice1, choice2);
            String resultMessage;

            if (result == 0) {
                resultMessage = "무승부!";
            } else if (result == 1) {
                resultMessage = name1 + " 승!";
                score1++;
            } else {
                resultMessage = name2 + " 승!";
                score2++;
            }
            try {
                dos1.writeUTF("상대: " + choice2 + " -> " + resultMessage);
            } catch (IOException e) {
                System.out.println("플레이어1에게 결과 전송 실패: " + e.getMessage());
            }

            try {
                dos2.writeUTF("상대: " + choice1 + " -> " + resultMessage);
            } catch (IOException e) {
                System.out.println("플레이어2에게 결과 전송 실패: " + e.getMessage());
            }
        }

        String finalresult;
        if(score1 > score2){
            finalresult = "최종 승자: " + name1 + "(" + score1 + ":" + score2 + ")";
        }else if(score2 > score1){
            finalresult = "최종 승자: " + name2 + "(" + score2 + ":" + score1 + ")";
        }else {
            finalresult = "최종 승자: 무승부 (" + score1 + ":" + score2 + ")";
        }

        dos1.writeUTF(finalresult);
        dos2.writeUTF(finalresult);
        dos1.close();
        dos2.close();
        player1.close();
        dis1.close();
        dis2.close();
        player2.close();
        serverSocket.close();
        System.out.println("서버와의 연결이 종료됩니다.");
    }
    private static String normallizeChoice(String input) {
        input = input.trim();
        if(input.equals("주먹")) {
            return "바위";
        }
        return input;
    }

    private static int determineWinner(String p1, String p2) {
        if (p1.equals(p2)) {
            return 0; // 무승부
        }

        if ((p1.equals("가위") && p2.equals("보")) ||
                (p1.equals("바위") && p2.equals("가위")) ||
                (p1.equals("보") && p2.equals("바위"))) {
            return 1; // player1 승
        }

        return 2; // player2 승
    }
}
