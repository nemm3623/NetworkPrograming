package NetworkPrograming8;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.zip.GZIPOutputStream;

public class NetP78_3_Server {
    public static void main(String[] args) {

        try(ServerSocket serverSocket = new ServerSocket(4001)){
            while(true){
                Socket socket = serverSocket.accept();

                new ClientThread(socket).start();
            }
        }catch(IOException e){
            System.err.println(e.getMessage());
        }
    }
    public static class ClientThread extends Thread{
        private Socket socket;

        public ClientThread(Socket socket){
            this.socket = socket;
        }

        public void run(){

            try(BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));){


                bw.write("파일 이름을 입력하세요.\n");
                bw.flush();
                String name = br.readLine()+".gz";

                    try(GZIPOutputStream gout= new GZIPOutputStream(new FileOutputStream(name));) {

                        String buffer;
                        int len;

                        bw.write("파일 내용을 입력하세요.(입력을 멈추려면 end를 입력)\n");
                        bw.flush();
                        while ((buffer = br.readLine()) != null) {
                            if (buffer.equals("end"))
                                break;
                            gout.write(buffer.getBytes());
                        }
                    }
                    try(BufferedInputStream fis = new BufferedInputStream(new FileInputStream(name));
                    OutputStream out = socket.getOutputStream();) {

                        byte[] buffer = new byte[1024];
                        int len;

                        bw.write("파일 전송 시작\n");
                        bw.flush();

                        while ((len = fis.read(buffer)) != -1) {
                           out.write(buffer,0,len);
                        }
                        // 클라이언트에서 파일이 저장되었는지 확인하기 위해
                        // 클라이언트로 파일 전송완료 후 파일 삭제
                        File f = new File(name);
                        if(f.delete()){
                            System.out.println("파일 삭제 성공");
                        }else{
                            System.out.println("파일 삭제 실패");
                        }
                        socket.close();
                    }
            }catch (IOException e){
                    System.err.println(e.getMessage());
            }
        }
    }
}
