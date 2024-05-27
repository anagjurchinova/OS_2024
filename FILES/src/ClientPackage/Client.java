

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client extends Thread{
    InetAddress serverAddress;
    int port;
    Socket socket;
    BufferedReader bufferedReader;
    BufferedWriter bufferedWriter;
    public Client(String serverAddress, int port) throws Exception{
        this.serverAddress = InetAddress.getByName(serverAddress);
        this.port = port;
        this.socket = new Socket(this.serverAddress, port);
        this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        String fileName = null;

        try {
            String line = scanner.nextLine();

            if(line.split("\\s+")[0].equals("DOWNLOAD")){
                fileName = line.split("\\s+")[1];
                line = "DOWNLOAD";
            }
            bufferedWriter.write(line + "\n");
            bufferedWriter.flush();

            if(line.equals("UPLOAD")){
                while (true){
                    line = scanner.nextLine();
                    if(line.equals("END")){
                        socket.close();
                        break;
                    }
                    bufferedWriter.write(line + "\n");
                    bufferedWriter.flush();
                }
            }

            if(line.equals("LIST")){
                bufferedReader.lines().forEach(l -> System.out.println(l));
            }

            if(line.equals("DOWNLOAD")){
                bufferedWriter.write(fileName + "\n");
                bufferedWriter.flush();

                bufferedReader.lines().forEach(l -> System.out.println(l));
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws Exception{
        Client client = new Client("localhost", 9357);
        client.start();
    }
}
