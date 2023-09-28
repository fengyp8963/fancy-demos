package com.junyue.fancy.javacv.demo5;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.*;

public class HLSVideoRecorder {
    public static void main(String[] args) {
        // 设置输入流的URL
        String inputURL = "D:\\sparkle_your_name_am360p.mp4";

        // 设置输出目录和文件名
        String outputDirectory = "D:\\hls\\";
        String outputFilename = "output.m3u8";


        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputURL);
        try {
            grabber.start();

            FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputDirectory + outputFilename, grabber.getImageWidth(), grabber.getImageHeight());
            recorder.setFormat("hls");
            recorder.setOption("hls_list_size", "0");
            recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
            recorder.setFrameRate(grabber.getFrameRate());
            recorder.setVideoBitrate((int) grabber.getVideoBitrate());
            recorder.setAudioChannels(grabber.getAudioChannels());
            recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);
            recorder.setAudioBitrate((int) grabber.getAudioBitrate());
            recorder.setSampleRate((int) grabber.getSampleRate());

            recorder.start();

            Frame frame;
            while ((frame = grabber.grabFrame()) != null) {
                recorder.record(frame);
            }

            recorder.stop();
            grabber.stop();
        } catch (FrameGrabber.Exception | FrameRecorder.Exception e) {
            e.printStackTrace();
        }
    }
}