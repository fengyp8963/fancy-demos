package com.junyue.fancy.javacv.demo5;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;

public class DASHSlicing {

    // ffmpeg -i https://static-b905bdbb-5254-4483-af4c-16e5bf477a2e.bspapp.com/mpd/wolf.mpd -c copy C:/Users/John/Desktop/output.mp4
    // https://d2zihajmogu5jn.cloudfront.net/big-buck-bunny/master.m3u8

    // ffmpeg -i https://d2zihajmogu5jn.cloudfront.net/big-buck-bunny/master.m3u8 -c copy D:/big-buck-bunny.mp4
    public static void main(String[] args) {
        String inputFile = "D:\\sparkle_your_name_am360p.mp4";
        String outputDirectory = "D:\\dash\\";

        sliceToDASH(inputFile, outputDirectory);
    }

    private static void sliceToDASH(String inputFile, String outputDirectory) {
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputFile);
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputDirectory + "%03d.m4s", 0);
        try {
            grabber.start();
            recorder.setFormat("dash");
            recorder.setVideoCodecName("libx264");
            recorder.setVideoBitrate(2000000); // 视频比特率
            recorder.start();
            Frame frame;
            int segmentCounter = 1;
            while ((frame = grabber.grab()) != null) {
                recorder.record(frame);
                if (segmentCounter % 10 == 0) {
                    recorder.flush();
                }
                segmentCounter++;
            }
            recorder.stop();
            grabber.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}