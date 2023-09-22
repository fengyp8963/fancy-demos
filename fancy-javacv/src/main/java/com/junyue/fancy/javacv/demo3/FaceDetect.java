package com.junyue.fancy.javacv.demo3;

import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

/**
 * OpenCV-4.0.0 测试文件
 */
public class FaceDetect {

    static {
        //在使用OpenCV前必须加载Core.NATIVE_LIBRARY_NAME类,否则会报错
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {
        face();
    }

    /**
     * OpenCV-4.0.0 人脸识别
     *
     * @return: void
     * @date: 2019年5月7日12:16:55
     */
    public static void face() {
        // 1 读取OpenCV自带的人脸识别特征XML文件
        //OpenCV 图像识别库一般位于 opencv\sources\data 下面
        CascadeClassifier facebook = new CascadeClassifier("D:\\opencv\\sources\\data\\haarcascades\\haarcascade_frontalface_alt.xml");
        // 2 读取测试图片
        Mat image = Imgcodecs.imread("D:\\opencv\\images\\01.jpeg");
        // 3 特征匹配
        MatOfRect face = new MatOfRect();
        facebook.detectMultiScale(image, face);
        // 4 匹配 Rect 矩阵 数组
        Rect[] rects = face.toArray();
        System.out.println("匹配到 " + rects.length + " 个人脸");
        // 5 为每张识别到的人脸画一个圈
        for (int i = 0; i < rects.length; i++) {
            Imgproc.rectangle(image, new Point(rects[i].x, rects[i].y), new Point(rects[i].x + rects[i].width, rects[i].y + rects[i].height), new Scalar(0, 0, 255));
            Imgproc.putText(image, "Human", new Point(rects[i].x, rects[i].y), Imgproc.FONT_HERSHEY_SCRIPT_SIMPLEX, 1.0, new Scalar(0, 255, 0), 1, Imgproc.LINE_AA, false);
        }
        // 6 展示图片
        HighGui.imshow("人脸识别", image);
        HighGui.waitKey(0);
    }

}