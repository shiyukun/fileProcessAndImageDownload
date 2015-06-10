package com.noahgroup.sky.tool.fileProcessor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

public class StickerFileProcessor {
    
    private static String[] resolutions = {"12x12.png", "14x14.png", "16x16.png", "24x24.png", "36x36.png", "48x48.png", "150x90.png", "200x120.png", "300x150.png", "300x180.png"};
    private static String toSizeSuffix = "400x240.png";
    
    public Set<String> processFiles(String path, String toPath, String prefix){
        
        Set<String> urls = new HashSet<>();
        File file = new File(path);
        try {
            PrintWriter writer = new PrintWriter(toPath + "/tmp.txt", "UTF-8");
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            line = reader.readLine();
            while(line != null) {
                // process
                String[] str = line.split(","); 
                String png = str[7];
                png = png.substring(1, (png.length() - 1));
                String fileName = prefix + png;
                for(int i=0; i<resolutions.length; i++){
                    if(png.endsWith(resolutions[i])){
                        png = png.replace(resolutions[i], toSizeSuffix);
                        break;
                    }
                }
                urls.add(png);
                String toFileName = "assets://emoticons/stickers/" + png;
                writer.println(fileName + " " + toFileName);
                line = reader.readLine();
            }
            writer.close();
        } catch (FileNotFoundException e) {
            System.out.println("file do not exist");
        } catch (IOException e) {
            System.out.println("i/o exception");
        }
        
        return urls;
    }

}
