package com.junyue.fancy.javacv.demo2;

import org.opencv.core.*;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class Demo {

    public static void main(String[] args) {
        imageFaceDetectionDnn();
    }

    public static void imageFaceDetectionDnn() {
        //加载opencv本地库
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        //加载预训练好的模型
        Net net = Dnn.readNetFromCaffe("E:\\电脑文件路径\\deploy.prototxt", "E:\\电脑文件路径\\res10_300x300_ssd_iter_140000_fp16.caffemodel");

        //读取图片
        String imgPath = "C:\\Users\\A80759\\Pictures\\Saved Pictures\\20200924194319998.jpg";
        Mat image = Imgcodecs.imread(imgPath);

        //为了获得最佳精度，必须分别对蓝色、绿色和红色通道执行 `(104, 177, 123)` 通道均值减法
        Mat inputBlob = Dnn.blobFromImage(image, 1.0f, new Size(image.size().width, image.size().height), new Scalar(104, 117, 123), false, false);
        net.setInput(inputBlob);
        Mat res = net.forward();
        Mat faces = res.reshape(1, res.size(2));
        System.out.println("faces" + faces);
        float[] data = new float[7];
        System.out.println("识别到人脸数：" + faces.rows());
        for (int i = 0; i < faces.rows(); i++) {
            faces.get(i, 0, data);
            float confidence = data[2];
            if (confidence > 0.2f) {
                int left = (int) (data[3] * image.cols());
                int top = (int) (data[4] * image.rows());
                int right = (int) (data[5] * image.cols());
                int bottom = (int) (data[6] * image.rows());
                System.out.println("(" + left + "," + top + ")(" + right + "," + bottom + ") " + confidence);
                Imgproc.rectangle(image, new Point(left, top), new Point(right, bottom), new Scalar(0, 200, 0), 3);
            }
        }

        Imgcodecs.imwrite("C:\\电脑文件路径\\new.jpg", image);
        // 展示图片
        HighGui.imshow("人脸识别", image);
        HighGui.waitKey(0);
    }
}
