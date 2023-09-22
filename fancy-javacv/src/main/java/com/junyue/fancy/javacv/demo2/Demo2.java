package com.junyue.fancy.javacv.demo2;

import cn.hutool.core.io.FileUtil;
import org.opencv.core.*;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.nio.charset.Charset;
import java.util.List;

public class Demo2 {

    public static void main(String[] args) {
        //加载opencv本地库
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        //读取ImageNet类名
        List<String> strings = FileUtil.readLines("E:\\xxx\\classification_classes_ILSVRC2012.txt", Charset.defaultCharset());
        Net net = Dnn.readNetFromCaffe("E:\\xxx\\DenseNet_121.prototxt", "E:\\xxx\\DenseNet_121.caffemodel");
        //读取图片
        String imgPath = "E:\\xxx\\af0cc6c118a37e9170db1c2ade2ac9c3.jpg";
        Mat image = Imgcodecs.imread(imgPath);
        Mat inputBlob = Dnn.blobFromImage(image, 0.01f, new Size(image.size().width, image.size().height), new Scalar(104, 117, 123), false, false);
        net.setInput(inputBlob);
        Mat res = net.forward();
        Mat temp = res.reshape(1, 1);
        Core.MinMaxLocResult minMaxLocResult = Core.minMaxLoc(temp);
        Point maxLoc = minMaxLocResult.maxLoc;
        double x = maxLoc.x;
        int classId = (int) x;
        double confidence = minMaxLocResult.maxVal;
        System.out.println(classId + ":" + confidence);
        Imgproc.putText(image, strings.get(classId) + " " + confidence, new Point(100, 150), Imgproc.CHAIN_APPROX_SIMPLE, 0.5, new Scalar(0, 255, 0, 0));
        HighGui.imshow("图像分类", image);
        HighGui.waitKey(0);
    }

}