package TCP;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class TCPClient extends Thread{
    InetAddress serverAddress;
    Socket socket;
    BufferedReader bufferedReader;
    BufferedWriter bufferedWriter;
    public TCPClient(String serverAddress, int port) throws IOException {
        this.serverAddress = InetAddress.getByName(serverAddress);
        this.socket = new Socket(serverAddress, port);
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        while (true){
            String message = scanner.nextLine();
            try {

                bufferedWriter.write(message + "\n");
                bufferedWriter.flush();

                //server response
                String response = bufferedReader.readLine();
                System.out.println("Server: " + response);

                if(response.equals("logged out")){
                    bufferedReader.close();
                    bufferedWriter.close();
                    socket.close();
                    break;
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        TCPClient client = new TCPClient("localhost", 8080);
        client.start();
    }
}
