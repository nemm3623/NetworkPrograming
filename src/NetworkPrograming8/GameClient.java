package NetworkPrograming8;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class GameClient {
    private static final String SERVER_IP = "localhost"; // 또는 서버 IP
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket(SERVER_IP, SERVER_PORT);
        DataInputStream dis = new DataInputStream(socket.getInputStream());
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        Scanner scanner = new Scanner(System.in);

        System.out.print("당신의 이름을 입력하세요: ");
        String name = scanner.nextLine();
        dos.writeUTF(name);

        for (int i = 0; i < 10; i++) {
            String prompt = dis.readUTF();
            System.out.print(prompt + " ");
            String choice = scanner.nextLine();
            dos.writeUTF(choice);

            String result = dis.readUTF();
            System.out.println(result);
        }

        String finalResult = dis.readUTF();
        System.out.println("게임 종료 - " + finalResult);

        dis.close();
        dos.close();
        socket.close();
        scanner.close();
    }
}
