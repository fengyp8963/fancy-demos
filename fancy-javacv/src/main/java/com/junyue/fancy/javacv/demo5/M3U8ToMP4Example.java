package com.junyue.fancy.javacv.demo5;

import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;

public class M3U8ToMP4Example {
    public static void main(String[] args) {
        try {
            String inputFilePath = "https://d2zihajmogu5jn.cloudfront.net/big-buck-bunny/master.m3u8";
            String outputFilePath = "D://big-buck-bunny3.mp4";

            // Load FFmpeg libraries
            avutil.av_log_set_level(avutil.AV_LOG_INFO);
            Loader.load(org.bytedeco.ffmpeg.ffmpeg.class);

            // Create frame grabber
            FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputFilePath);
            grabber.setOption("ffmpeg_command", "ffmpeg -i " + inputFilePath + " -c copy " + outputFilePath);

            // Start grabbing frames
            grabber.start();

            Frame frame;
            while ((frame = grabber.grabFrame()) != null) {
                // Do something with the frame if needed
            }

            // Stop grabbing
            grabber.stop();
            grabber.release();

            System.out.println("M3U8 to MP4 conversion completed.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}