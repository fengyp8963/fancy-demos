package com.junyue.fancy.javacv.demo4;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.*;

public class VideoStreamingExample {
    public static void main(String[] args) {
        // 输入视频文件路径
        String inputFile = "D:\\sparkle_your_name_am360p.mp4";
        // 推流地址
        String streamUrl = "rtmp://192.168.50.47:11935/live/livestream";
        try {
            // 创建视频采集器
            FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputFile);
            grabber.start();

            // 创建视频推流器
            FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(streamUrl, grabber.getImageWidth(), grabber.getImageHeight());
            recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
            recorder.setFormat("flv");
            recorder.setFrameRate(grabber.getFrameRate());
            recorder.setAudioChannels(grabber.getAudioChannels());
            recorder.start();

            Frame frame;
            while ((frame = grabber.grabFrame()) != null) {
                recorder.record(frame);
            }

            // 停止推流和采集
            recorder.stop();
            recorder.release();
            grabber.stop();
            grabber.release();
        } catch (FrameGrabber.Exception | FrameRecorder.Exception e) {
            e.printStackTrace();
        }
    }
}