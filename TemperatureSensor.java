package TemperatureSensor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class TemperatureSensor {
    public static void main(String[] args) throws IOException, InterruptedException {
        Random random = new Random();
        String filePath = "C:\\Users\\ANA\\Desktop\\os_exercises\\ServerRoomTemperature\\src\\temperature.txt";
        File file = new File(filePath);
        if(!file.exists()){
            file.createNewFile();
        }
        while (true){
            FileWriter fileWriter = new FileWriter(filePath);
            for (int i=0; i<5; i++){
                fileWriter.write(random.nextInt(5,50) + 1 + "\n");
            }
            fileWriter.flush();
            fileWriter.close();
            Thread.sleep(30000);
        }
    }
}
