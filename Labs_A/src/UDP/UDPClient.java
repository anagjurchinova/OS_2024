package UDP;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class UDPClient extends Thread{
    int port;
    String address;
    DatagramSocket datagramSocket;
    byte[] buffer;
    public UDPClient(int port, String address) throws SocketException {
        this.port = port;
        this.address = address;
        this.datagramSocket = new DatagramSocket();
        this.buffer = new byte[256];
    }


    @Override
    public void run() {
        try {
            InetAddress serverAddress = InetAddress.getByName(address);
            Scanner scanner = new Scanner(System.in);
            while (true){
                //message to server
                buffer = new byte[256];

                String messageSend = scanner.nextLine();
                buffer = messageSend.getBytes();
                DatagramPacket packetSend = new DatagramPacket(buffer, buffer.length, serverAddress, port);
                datagramSocket.send(packetSend);

                //response
                buffer = new byte[256];

                DatagramPacket packetRecieved = new DatagramPacket(buffer, buffer.length);
                datagramSocket.receive(packetRecieved);
                String messageRecieved = new String(packetRecieved.getData(), 0, packetRecieved.getLength());
                System.out.println("Server: " + messageRecieved);

                if(messageRecieved.equals("logged out")) break;
            }

        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void main(String[] args) throws SocketException {
        UDPClient client = new UDPClient(8080, "localhost");
        client.start();
    }
}
