package com.junyue.fancy.javacv.demo6;

import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.file.FileWriter;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * 解析mp4视频为m3u8,内存式hls切片.通过流的方式来保存 进行视频点播
 * <p>
 * m3u8转MP4 ffmpeg -i https://d2zihajmogu5jn.cloudfront.net/big-buck-bunny/master.m3u8 -c copy D:/big-buck-bunny.mp4
 *
 * @author fengyp
 */
@CrossOrigin(origins = "*")
@RestController
public class TestController {

    /**
     * 目录路径,enc.info文件，enc.key文件和sample-mp4-file.mp4文件
     */
    private static final String PATH = "D://test//";

    @PostMapping("uploadToM3u8")
    public void uploadToM3u8() throws Exception {
        try (FileInputStream inputStream = new FileInputStream(PATH + "sample-mp4-file.mp4")) {
            String m3u8Url = "http://localhost:8080/upload/enc.m3u8";
            String infoUrl = "http://localhost:8080/preview/enc.keyinfo";
            FFmpegProcessor.convertMediaToM3u8ByHttp(inputStream, m3u8Url, infoUrl);
        }
    }

    @PostMapping("upload/{fileName}")
    public void upload(HttpServletRequest request, @PathVariable("fileName") String fileName) throws IOException {
        try (ServletInputStream inputStream = request.getInputStream()) {
            FileWriter writer = new FileWriter(PATH + fileName);
            writer.writeFromStream(inputStream);
        }
    }

    /**
     * 预览加密文件
     */
    @PostMapping("preview/{fileName}")
    public void preview(@PathVariable("fileName") String fileName, HttpServletResponse response) throws IOException {
        FileReader fileReader = new FileReader(PATH + fileName);
        fileReader.writeToStream(response.getOutputStream());
    }

    /**
     * 下载视频文件
     */
    @GetMapping("download/{fileName}")
    public void download(@PathVariable("fileName") String fileName, HttpServletResponse response) throws IOException {
        FileReader fileReader = new FileReader(PATH + fileName);
        fileReader.writeToStream(response.getOutputStream());
    }

}
