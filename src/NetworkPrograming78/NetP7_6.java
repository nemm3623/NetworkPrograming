package NetworkPrograming78;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class NetP7_6 {

    public static void main(String[] args){

        try(ServerSocket server = new ServerSocket(80);){
            while (true) {
                Socket connection = null;
                FileDownload client = null;
                try {
                    connection = server.accept();
                    client = new FileDownload(connection);
                    client.start();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        } catch(Exception e){
                System.out.println(e.getMessage());
        }
    }
    static class FileDownload extends Thread {

        String contenttype = "text/plain";
        Socket connection;

        public FileDownload(Socket connection) throws UnsupportedEncodingException {
            this.connection = connection;
        }

        public void run() {

            try (BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
                 BufferedOutputStream out = new BufferedOutputStream(connection.getOutputStream());){


                String file;
                StringBuilder request = new StringBuilder(80);
                StringTokenizer token;

                while (true) {
                    int c = in.read();
                    if (c == '\r' || c == '\n' || c == -1)
                        break;
                    request.append((char) c);
                }

                token = new StringTokenizer(request.toString(), " ");
                token.nextToken();

                if ((file = token.nextToken()).charAt(0) == '/' && request.toString().contains("HTTP/")) {

                    file = file.substring(1);

                    try(FileInputStream fin = new FileInputStream(file);
                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();) {

                        if( file.contains(".html") || file.contains(".htm"))
                            contenttype = "text/html";

                        int b;

                        while((b = fin.read()) != -1)
                            buffer.write(b);

                        byte[] content = buffer.toByteArray();
                        String header = "HTTP 1.0 200 OK\r\n" + "Server: OneFile 1.0\r\n" + "Content-length:" + content.length + "\r\nContent-Type: " + contenttype + "\r\n\r\n";
                        byte[] header1 = header.getBytes(StandardCharsets.UTF_8);

                        out.write(header1);
                        out.write(content);
                        out.flush();

                    }catch (IOException e){
                        System.out.println(e.getMessage());
                    }
                }

            } catch (IOException e) {
                System.err.println(e.getMessage());
            }finally {
                try {
                    connection.close();
                }
                catch (IOException ioe) {
                    System.out.println(ioe.getMessage());
                }
            }
        }
    }
}

