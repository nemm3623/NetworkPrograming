package NetworkProgramin78;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

public class NetP8_3 {

    public static void main(String[] args) {
        Socket theSocket;
        String host;
        InputStream is;
        BufferedReader reader;
        /*if (args.length > 0) {
            host = args[0]; // 원격 호스트를 입력받음
        } else {*/
        host = "localhost"; // 로컬 호스트를 원격 호스트로 사용

        //}
        try {
            theSocket = new Socket(host, 4000); // daytime 서버에 접속한다.
            is = theSocket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is));
            String theTime = reader.readLine(); // 날짜를 읽는다
            System.out.println("호스트의 시간은 " + theTime + "이다");
        } catch (UnknownHostException e) {
            System.err.println(args[0] + " 호스트를 찾을 수 없습니다.");
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}
