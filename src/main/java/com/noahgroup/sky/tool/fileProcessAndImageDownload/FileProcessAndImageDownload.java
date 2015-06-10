package com.noahgroup.sky.tool.fileProcessAndImageDownload;

import java.util.Set;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import com.noahgroup.sky.tool.fileProcessor.StickerFileProcessor;
import com.noahgroup.sky.tool.imageFetcher.ImageFetcher;

public class FileProcessAndImageDownload {
    
    private Configuration conf = null;
    
    public String filePath;
    public String toPath;
    public String imageBaseUrl;
    public int threadSize = 0;
    
    public FileProcessAndImageDownload(){
        this(null);
    }
    
    public FileProcessAndImageDownload(String confPath){
        try {
            if(confPath == null || "".equals(confPath)){
                conf = new PropertiesConfiguration("conf/configuration.properties");
            }else{
                conf = new PropertiesConfiguration(confPath);
            }
            init();   
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }
    
    private void init(){
        filePath = conf.getString("filePath");
        toPath = conf.getString("toPath");
        imageBaseUrl = conf.getString("imageBaseUrl");
        threadSize = conf.getInt("threadSize");
    }
    
    
    public static void main(String[] args){
        FileProcessAndImageDownload task = new FileProcessAndImageDownload();
        task.init();
        System.out.println(task.filePath);
        System.out.println(task.toPath);
        System.out.println(task.imageBaseUrl);
        System.out.println(task.threadSize + "");
        
        StickerFileProcessor processor = new StickerFileProcessor();
        Set<String> urls = processor.processFiles(task.filePath, task.toPath, task.imageBaseUrl);
        
        ImageFetcher imageFetcher = new ImageFetcher(task.toPath);
        imageFetcher.fetchImages(urls, task.imageBaseUrl);
        
        System.out.println("done!");
        
    }
}
