package com.junyue.fancy.javacv.demo5;

import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;

public class HLSVideoPlayer {
    public static void main(String[] args) {
        // 设置M3U8播放列表文件的URL
        String m3u8URL = "D:\\hls\\output.m3u8";

        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(m3u8URL);
        try {
            grabber.start();

            CanvasFrame canvasFrame = new CanvasFrame("HLS Video Player");
            Frame frame;
            while ((frame = grabber.grabFrame()) != null) {
                canvasFrame.showImage(frame);
                Thread.sleep(10);
            }

            canvasFrame.dispose();
            grabber.stop();
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}