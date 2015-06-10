package com.noahgroup.sky.tool.imageFetcher;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.util.EntityUtils;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

public class ImageFetcher {
    
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.44 Safari/537.36";
    
    public static final ExecutorService exe;

    private final String outputPath;

    static {
        ThreadFactory tf = new ThreadFactoryBuilder().setNameFormat("img-%d").build();
        exe = new ThreadPoolExecutor(20, 100, 1000l, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(20), tf, new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public ImageFetcher(String outpath) {
        this.outputPath = outpath;
    }

    public void fetchImages(Set<String> urls, final String baseImageUrl) {
        for (final String imageUrl : urls) {
            exe.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        fetchImage(imageUrl, baseImageUrl);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private boolean fetchImage(String imageUrl, String baseImageUrl) throws IOException {

        String url = baseImageUrl + imageUrl;
        try {
            HttpResponse response = fetch(url);
            int status = response.getStatusLine().getStatusCode();
            System.out.println("[fetch image] " + status + " " + url);
            if (status == 200) {
                storeImage(response, imageUrl);
                return true;
            }
            
        } catch (IOException ex) {
        }

        throw new IOException("[fetch image] failed. No further attempts. ");
    }

    private void storeImage(HttpResponse response, String imageUrl) throws IOException {
        byte[] raw = EntityUtils.toByteArray(response.getEntity());

        String[] tokens = imageUrl.split("/");
        String folder = tokens[0];
        String fileName = tokens[1];
        
        String toPath = outputPath + "/" + folder;
        File f = new File(toPath);
        if (!f.exists()) {
            f.mkdirs();
        }
        try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(toPath + "/" + fileName)))) {
            out.write(raw);
        }
    }
    
    public static HttpResponse fetch(String url) throws IOException {

        // @formatter:off
        Request request = Request.Get(url)
                .userAgent(USER_AGENT)
                .connectTimeout(5 * 60 * 1000)
                .socketTimeout(5 * 60 * 1000);
        // @formatter:on

        return request.execute().returnResponse();
    }

    public static void main(String[] args) throws Exception {
    }
}
