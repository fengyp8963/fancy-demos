package com.junyue.fancy.javacv.demo3;

import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.VideoInputFrameGrabber;

/**
 * VideoInputFrameGrabber类从默认的摄像头捕获帧，并使用CanvasFrame类显示捕获的图像。
 */
public class CameraCapture {

    public static void main(String[] args) throws FrameGrabber.Exception {
        FrameGrabber grabber = new VideoInputFrameGrabber(0); // 使用默认的摄像头
        grabber.start(); // 开始捕获帧
        CanvasFrame canvasFrame = new CanvasFrame("Camera"); // 创建一个窗口用于显示图像
        canvasFrame.setCanvasSize(grabber.getImageWidth(), grabber.getImageHeight());
        while (canvasFrame.isVisible()) {
            canvasFrame.showImage(grabber.grab()); // 显示捕获的图像
        }
        grabber.stop(); // 停止捕获帧
        canvasFrame.dispose(); // 关闭窗口
    }

}