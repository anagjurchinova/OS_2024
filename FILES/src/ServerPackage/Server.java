//package ServerPackage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class Server extends Thread{
    ServerSocket serverSocket;
    public Server(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
    }

    static class Worker extends Thread{
        Socket socket;
        BufferedReader bufferedReader;
        BufferedWriter bufferedWriter;
        static final String FOLDER_PATH = "C:\\Users\\ANA\\Desktop\\os_exercises\\FILES\\files\\";

        public Worker(Socket socket) throws Exception{
            this.socket = socket;
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
            bufferedReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        }

        public void listFiles(){
            File folder = new File(FOLDER_PATH);

            Arrays.stream(folder.listFiles()).sequential().forEach(f -> {
                String fileName = f.getName();
                if(fileName.split("\\.")[1].equals("txt")){
                    long fileSize = f.length();
                    try {
                        bufferedWriter.write("File name: " + fileName + ", file size: " + fileSize + "\n");
                        bufferedWriter.flush();

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }

        public void readFile(String fileName){
            File folder = new File(FOLDER_PATH);

            Arrays.stream(folder.listFiles()).sequential().forEach(file -> {
                if(file.getName().equals(fileName)){
                    try {
                        BufferedReader reader = new BufferedReader(new FileReader(file.getAbsolutePath()));
                        bufferedWriter.write("BEGIN" + "\n");
                        bufferedWriter.flush();

                        reader.lines().forEach(line -> {
                            try {
                                bufferedWriter.write(line + "\n");
                                bufferedWriter.flush();

                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });

                        bufferedWriter.write("END" + "\n");
                        bufferedWriter.flush();

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }

        @Override
        public void run() {
            try {
                String command = bufferedReader.readLine();
                switch (command){
                    case "UPLOAD":
                        String line = bufferedReader.readLine();
                        String fileName = line.split(" ")[0];
                        String filePath = FOLDER_PATH + fileName;

                        File file = new File(filePath);

                        if(!file.exists()){
                            file.createNewFile();
                        }
                        FileWriter fileWriter = new FileWriter(filePath, true);

                        while (!(line = bufferedReader.readLine()).equals("END")){
                            fileWriter.append(line + "\n");
                            fileWriter.flush();
                        }
                        socket.close();
                        fileWriter.close();
                        break;

                    case "LIST": listFiles();

                    case "DOWNLOAD":
                        fileName = bufferedReader.readLine();
                        readFile(fileName);
                        break;
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void run() {
        System.out.println("Waiting for clients...");
        while (true){
            try {
                Socket socket = serverSocket.accept();
                System.out.println("Creating worker thread...");
                Worker worker = new Worker(socket);
                worker.start();

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) throws Exception{
        Server server = new Server(9357);
        server.start();
    }
}
