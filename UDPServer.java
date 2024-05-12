import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.io.IOException;
import java.net.*;
import java.net.UnknownHostException;

class UDPServer extends Thread{
    DatagramSocket socket;
    private byte[] buffer;

    public UDPServer(int port) throws SocketException {
        socket = new DatagramSocket(port);
        buffer = new byte[250];
    }


    @Override
    public void run() {
        super.run();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
      
        while (true){
            try {
                socket.receive(packet);
                String message = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Got message from client: " + message);

                // Sending message back to client

                DatagramPacket sendPacket = new DatagramPacket(buffer, buffer.length, packet.getAddress(), packet.getPort());
                socket.send(sendPacket);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
}

class UDPClient extends Thread{
    String serverName;
    String message;

    private byte[] buffer;
    int port;
    DatagramSocket socket;
    InetAddress serverAddress;

    public UDPClient(String serverName, int port, String message) throws UnknownHostException, SocketException {
        this.serverName = serverName;
        this.port = port;
        this.buffer = message.getBytes();
        this.socket = new DatagramSocket();

        serverAddress = InetAddress.getByName(serverName);
    }

    @Override
    public void run() {
        super.run();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, port);

        try {
            socket.send(packet);
            DatagramPacket packetRecieved = new DatagramPacket(buffer, buffer.length, serverAddress, port);
            socket.receive(packetRecieved);
            String messageRecieved = new String(packetRecieved.getData(), 0, packetRecieved.getLength());

            System.out.println("Server sent: " + messageRecieved);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    
}

public class UDPMain {

    public static void main(String[] args) throws SocketException, InterruptedException, UnknownHostException {
        UDPServer server = new UDPServer(8080);
        UDPClient client = new UDPClient("localhost", 8080, "Hello:)");

        server.start();
        client.start();
    }
}
