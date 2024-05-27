package TCP;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TCPServer extends Thread{
    int port;
    ServerSocket serverSocket;
    Counter counter;

    public static class Worker extends Thread{
        Socket socket;
        BufferedReader bufferedReader;
        BufferedWriter bufferedWriter;
        TCPServer server;

        public Worker(Socket socket, TCPServer server) throws IOException {
            this.server = server;
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        }

        @Override
        public void run() {
            Lock lock = new ReentrantLock();
            String firstMessage = null;
            String message = null;

            try {
                firstMessage = bufferedReader.readLine();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            if(!firstMessage.equals("login")){
                try {
                    socket.close();

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }else message = "login";

            while (true){
                try {
                    if(message == null){
                        message = bufferedReader.readLine();
                        if(message == null) break;
                    }
                    String response = null;

                    switch (message){
                        case "login":
                            response = "logged in";
                            break;

                        case "logout":
                            response = "logged out";
                            break;

                        default:
                            response = "echo: " + message;
                            break;
                    }
                    lock.lock();
                    server.counter.increment();
                    lock.unlock();

                    bufferedWriter.write(response + "\n");
                    bufferedWriter.flush();
                    message = null;

                    if(response != null && response.equals("logout")){
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
    }

    public static class Counter{
        static int messageCounter = 0;
        public static void increment(){messageCounter++;}

        public int getCounter(){return messageCounter;}
    }

    public TCPServer(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.counter = new Counter();
    }

    @Override
    public void run() {

        System.out.println("Waiting for clients...");
        while (true) {
            Socket socket = null;
            try {

                socket = serverSocket.accept();
                System.out.println("Connected with a client.\nCreating worker thread...");
                Worker worker = new Worker(socket, this);
                worker.start();
                worker.join();

                System.out.println("Messages count: " + counter.getCounter());

            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
    }

    public static void main(String[] args) throws IOException {
        TCPServer server = new TCPServer(8080);
        server.start();
    }
}
