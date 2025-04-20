package NetworkPrograming78;

import java.io.*;
import java.net.Socket;
import java.rmi.UnknownHostException;

public class NetP8_4 {
    public static void main(String[] args) {
        Socket theSocket = null;
        String host;
        InputStream is;
        BufferedReader reader, userInput;
        OutputStream os;
        BufferedWriter writer;
        String theLine;
        if(args.length>0) {
            host = args[0]; // 원격 호스트를 입력받음
        }else {
            host = "localhost";
        }
        try{

            theSocket = new Socket(host, 7); // echo 서버 접속
            is = theSocket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is));
            userInput = new BufferedReader(new InputStreamReader(System.in));
            os = theSocket.getOutputStream();
            writer = new BufferedWriter(new OutputStreamWriter(os));
            System.out.println("전송할 문장을 입력하십시오.");

            while (true){
                theLine = userInput.readLine();
                if(theLine.equals("quit")) break;
                for (int i = 0; i < 1; i++) {
                    writer.write(theLine + "\r\n");
                    writer.flush();              // 서버에 데이터 전송
                }
                System.out.println(reader.readLine()); // 다시 수신해서 화면에 출력한다.
            }
            theSocket.close();
        }catch (UnknownHostException e){
            System.err.println(args[0]+" 호스트를 찾을 수 없습니다.");
        } catch (IOException e){
            System.err.println(e.getMessage());
        }
    }
}
