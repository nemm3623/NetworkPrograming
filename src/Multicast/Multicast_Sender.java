package Multicast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Multicast_Sender {
    public static void main(String[] args) {
        int port = 4002;
        try{
        InetAddress group = InetAddress.getByName("239.255.10.10");
        System.out.println(group.getHostAddress()+ " 그룹 주소 및 " + port + " 포트에 바인드된 멀티캐스트 소켓을 생성함.");
        MulticastSocket msocket = new MulticastSocket();
        msocket.setSoTimeout(10000);
        msocket.setTimeToLive(1);

        // 멀티캐스트 패킷을 생성하고 전송한다.
        String sendmsg = "This is a multicast data";
        byte data[] = sendmsg.getBytes();
        DatagramPacket packet = new DatagramPacket(data, data.length, group, port);
        System.out.println("멀티캐스트 메시지를 전송하고 있습니다 -> " + sendmsg);
        msocket.send(packet);

        }catch (IOException e){
            System.err.println(e.getMessage());
        }
    }
}
