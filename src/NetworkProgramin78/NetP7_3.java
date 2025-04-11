package NetworkProgramin78;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.*;
import java.util.Date;

public class NetP7_3 {

    public final static int daytimeport=4000;

    public static void main(String[] args) throws IOException {


        Socket theSocket = null;
        BufferedWriter writer;

        try(ServerSocket theServer = new ServerSocket(daytimeport)){
            while(true){
                theSocket = theServer.accept();
                OutputStream os = theSocket.getOutputStream();
                writer = new BufferedWriter(new OutputStreamWriter(os));

                Date date = new Date();

                writer.write(date.toString());
                writer.newLine();
                writer.flush();

                System.out.println(theSocket.getInetAddress());

                theSocket.close();
            }
        }catch (IOException e){
            System.err.println(e.getMessage());
        }finally {
            if(theSocket != null) theSocket.close();
        }

        System.out.println();
    }
}
