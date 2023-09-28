package com.junyue.fancy.javacv.demo5;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;

public class CustomFFmpegCommandExample {

    public static void main(String[] args) {
        try {
            String inputFilePath = "https://d2zihajmogu5jn.cloudfront.net/big-buck-bunny/master.m3u8";
            String outputFilePath = "D://big-buck-bunny.mp4";

            // Load FFmpeg libraries
            avutil.av_log_set_level(avutil.AV_LOG_INFO);

            // Create frame grabber
            FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputFilePath);
            //grabber.setOption("c", "copy");

            // Create frame recorder
            FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputFilePath, grabber.getImageWidth(), grabber.getImageHeight());
            recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
            recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);
            recorder.setAudioChannels(2);
            recorder.setFormat("mp4");
            recorder.start();
            // Start grabbing frames and record to output file
            grabber.start();

            Frame frame;
            while ((frame = grabber.grabFrame()) != null) {
                recorder.record(frame);
            }

            // Stop grabbing and recording
            recorder.stop();
            recorder.release();
            grabber.stop();
            grabber.release();

            System.out.println("M3U8 to MP4 conversion completed.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}