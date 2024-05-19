package TemperatureMonitor;

import java.io.*;
import java.util.OptionalDouble;

public class TemperatureMonitor {
    public static void main(String[] args) throws IOException, InterruptedException {
        String filePath = "C:\\Users\\ANA\\Desktop\\os_exercises\\ServerRoomTemperature\\src\\temperature.txt";
        File file = new File(filePath);
        FileWriter fileWriter = new FileWriter(filePath, true);
        if(!file.exists()){
            file.createNewFile();
            Thread.sleep(30000);
        }
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        while (true) {
            OptionalDouble avg = br.lines().mapToInt(i -> Integer.parseInt(i))
                    .average();
            br.close();
            if(avg.isPresent()) {
                double average_temperature = avg.getAsDouble();

                if (5 <= average_temperature && average_temperature <= 19) {
                    fileWriter.append("Low");
                } else if (19 <= average_temperature && average_temperature <= 35) {
                    fileWriter.append("Medium");
                } else {
                    fileWriter.append("High");
                }

                fileWriter.flush();
                fileWriter.close();
            }
            Thread.sleep(60000);
        }
    }
}

