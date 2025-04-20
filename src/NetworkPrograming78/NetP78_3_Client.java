package NetworkPrograming78;

import java.io.*;
import java.net.Socket;

public class NetP78_3_Client {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 4001)) {

            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

            // 파일명
            System.out.println(br.readLine());
            String name = input.readLine();
            bw.write(name+"\n");
            bw.flush();

            // 파일 내용
            System.out.println(br.readLine());
            String line;
            while (true) {
                line = input.readLine();
                if ("end".equals(line)) {
                    bw.write("end\n");
                    bw.flush();
                    break;
                }
                bw.write(line + "\n");
                bw.flush();
            }

            // 파일 저장
            System.out.println(br.readLine());

            try (
                    InputStream in = socket.getInputStream();
                    BufferedOutputStream fout = new BufferedOutputStream(new FileOutputStream(name + ".gz"))
            ) {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = in.read(buffer)) != -1) {
                    fout.write(buffer, 0, len);
                }
                fout.flush();
                System.out.println("파일 저장 완료: " + name + ".gz");
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
