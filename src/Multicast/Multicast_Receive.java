package Multicast;

import java.io.IOException;
import java.net.*;

public class Multicast_Receive {
    public static void main(String[] args) {
        int port = 4002;
        try (MulticastSocket socket = new MulticastSocket(port)){
            InetAddress group = InetAddress.getByName("230.0.0.1₩");
            byte[] buf = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.joinGroup(group);

            System.out.println("멀티캐스트 패킷을 수신하고 있습니다.");
            socket.receive(packet);
            String s = new String(packet.getData());
            System.out.println(s);

        }catch (IOException e){
            System.err.println(e.getMessage());
        }

    }
}
