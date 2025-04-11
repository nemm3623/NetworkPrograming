package NetworkPrograming45;

import java.io.*;
import java.net.*;

public class NetP5_4 {
    public static void main(String args[]){

        String hostname;
        BufferedReader br;

        br = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("호스트 이름 또는 IP 주소를 입력하세요.");

        try{
            if((hostname = br.readLine()) != null) {
                InetAddress addr = InetAddress.getByName (hostname);
                System.out.println("호스트이름 : " + addr.getHostName());
                System.out.println("IP주소 : " + addr.getHostAddress());
            }

            InetAddress laddr = InetAddress.getLocalHost();
            System.out.println("로컬호스트의 주소 :" + laddr.getHostName());
            System.out.println("로컬IP 주소 : " + laddr.getHostAddress());

        }catch(UnknownHostException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
