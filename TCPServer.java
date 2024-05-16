package Networking;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.net.SocketException;
import java.net.UnknownHostException;

class TCPServer extends Thread{
    ServerSocket serverSocket;

    public TCPServer(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
    }

    @Override
    public void run() {
        super.run();
        System.out.println("Starting the server...");
        System.out.println("Waiting for clients...");

        while (true) {
            try {
                Socket client = serverSocket.accept();
                System.out.println("Connected to the client " + client.getInetAddress() + "\nCreating worker thread...");
                TCPWorker worker = new TCPWorker(client);
                worker.start();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }
}

class TCPWorker extends Thread{
    Socket client;

    public TCPWorker(Socket client){
        this.client = client;
    }

    @Override
    public void run() {
        super.run();

        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            String[] requestParts = bufferedReader.readLine().split("\\s+");
            Request request = new Request(requestParts);

            bufferedReader.lines().forEach(line -> {
                String[] parts = line.split("\\s+", 2);
                request.mapHeaders(parts[0],parts[1]);
            });

            request.send(client);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    static class Request{
        String method;
        String request;
        String http;
        Map<String, String> headers;

        public Request(String[] request){
            method = request[0];
            this.request = request[1];
            http = request[2];

            this.headers = new HashMap<>();
        }

        public void mapHeaders(String key, String value){
            headers.put(key, value);
        }

        public void send(Socket client) throws IOException {
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
            bufferedWriter.write("You created a " + method + " request for " + request + "\n");
            bufferedWriter.write("Headers:\n");

            headers.entrySet().forEach(entry -> {
                try {
                    bufferedWriter.write(entry.getKey() + " " + entry.getValue() + "\n");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            bufferedWriter.flush();
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



