package NetworkPrograming78;

import java.io.*;
import java.net.*;


public class NetP7_4_Server {
    public static void main(String[] args) {

        ServerSocket theServer = null;
        Socket theSocket = null;

        try{
            theServer = new ServerSocket(7);
            theServer.setReuseAddress(true);

            while(true){
                theSocket = theServer.accept();

                new Client_Handler(theSocket).start();
            }
        } catch(IOException e) {
            System.err.println(e.getMessage());
        }finally {
            try {
                theSocket.close();
                theServer.close();
            }catch (IOException e){
                System.err.println("close");
            }
        }
    }
    public static class Client_Handler extends Thread{

        Socket theSocket = null;
        BufferedReader reader;
        BufferedWriter writer;
        String theLine;

        Client_Handler(Socket socket){
            theSocket = socket;
        }

        public void run() {
            try {
                reader = new BufferedReader(new InputStreamReader(theSocket.getInputStream()));
                writer = new BufferedWriter(new OutputStreamWriter(theSocket.getOutputStream()));
                while(true){
                    theLine = reader.readLine();
                    if(theLine.equals("quit"))
                        quit();
                    System.out.println(theLine); // 받은 데이터를 화면에 출력한다.
                    writer.write(theLine + "\r\n"); //클라이언트에 데이터를 재전송
                    writer.flush(); //클라이언트에 데이터를 재전송
                }
            }catch(IOException e){
                System.out.println(e.getMessage());
            }
        }
        public void quit() {
            try {
                reader.close();
                writer.close();
                theSocket.close();
            }catch(IOException e){
                System.err.println(e.getMessage());
            }

        }
    }
}
