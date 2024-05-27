package UDP;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UDPServer extends Thread {
    int port;
    DatagramSocket datagramSocket;
    byte[] buffer;
    public UDPServer(int port) throws SocketException {
        this.datagramSocket = new DatagramSocket(port);
        this.buffer = new byte[256];
    }

    @Override
    public void run() {
        System.out.println("Waiting for packets...");

        while (true){
            buffer = new byte[256];
            DatagramPacket packetRecieved = new DatagramPacket(buffer, buffer.length);

            try {
                datagramSocket.receive(packetRecieved);
                String messageRecieved = new String(packetRecieved.getData(), 0, packetRecieved.getLength());
                System.out.println("Client: " + messageRecieved);

                String messageSend = null;

                switch (messageRecieved){
                    case "login":
                        messageSend = "logged in";
                        break;

                    case "logout":
                        messageSend = "logged out";
                        break;

                    default:
                        messageSend = "echo- " + messageRecieved;
                        break;
                }

                if(messageSend != null){
                    buffer = new byte[256];

                    buffer = messageSend.getBytes();
                    DatagramPacket packetSend = new DatagramPacket(buffer, buffer.length, packetRecieved.getAddress(), packetRecieved.getPort());
                    datagramSocket.send(packetSend);
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public static void main(String[] args) throws SocketException {
        UDPServer server = new UDPServer(8080);
        server.start();
    }
}
