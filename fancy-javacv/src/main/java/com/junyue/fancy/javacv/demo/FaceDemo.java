package com.junyue.fancy.javacv.demo;

import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_face.FaceRecognizer;
import org.bytedeco.opencv.opencv_face.FisherFaceRecognizer;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.bytedeco.opencv.global.opencv_imgproc.*;

/**
 * 人脸识别
 */
public class FaceDemo {

    public static void main(String[] args) throws IOException {
        Map<Integer, String> kindNameMap = new HashMap<>(2);
        kindNameMap.put(1, "msk");
        kindNameMap.put(2, "tlp");
        faceRecognize("D:\\face_img\\test\\msk.jpg", kindNameMap);
    }


    /**
     * 调整后的文件宽度
     */
    public final static int RESIZE_WIDTH = 164;
    /**
     * 调整后的文件高度
     */
    public final static int RESIZE_HEIGHT = 164;
    /**
     * 超过这个置信度就明显有问题了
     */
    public final static double MAX_CONFIDENCE = 50d;

    public final static String frontalFaceModelPath = "D:\\face_img\\lbpcascade_frontalface.xml";
    public final static String faceRecognizerPath = "D:\\face_img\\faceRecognizer.xml";

    /**
     * 人脸检测
     *
     * @param grayImg grayImg
     */
    public static RectVector faceDetect(Mat grayImg) throws IOException {
        // 读取opencv人脸检测器
        CascadeClassifier cascade = new CascadeClassifier(frontalFaceModelPath);
        // 检测到的人脸
        RectVector faces = new RectVector();
        //多人脸检测
        cascade.detectMultiScale(grayImg, faces);
        return faces;

    }

    /**
     * 人脸识别
     *
     * @param kindNameMap 人物名称集合
     */
    public static void faceRecognize(String filePath, Map<Integer, String> kindNameMap) throws IOException {
        File file = new File(filePath);
        BufferedImage image = ImageIO.read(file);
        Java2DFrameConverter imageConverter = new Java2DFrameConverter();
        Frame frame = imageConverter.convert(image);
        //类型转换
        OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
        Mat original = converter.convertToMat(frame);
        //存放灰度图
        Mat grayImg = new Mat();
        //模式设置成ImageMode.Gray下不需要再做灰度 摄像头获取的是彩色图像，所以先灰度化下
        cvtColor(original, grayImg, COLOR_BGRA2GRAY);
        // 均衡化直方图
        equalizeHist(grayImg, grayImg);
        RectVector faces = faceDetect(grayImg);
        // 遍历人脸
        for (int i = 0; i < faces.size(); i++) {
            Rect face_i = faces.get(i);
            //绘制人脸矩形区域，scalar色彩顺序：BGR(蓝绿红)
            rectangle(original, face_i, new Scalar(0, 255, 0, 1));
            int pos_x = Math.max(face_i.tl().x() - 10, 0);
            int pos_y = Math.max(face_i.tl().y() - 10, 0);
            oneFaceRecognize(grayImg, face_i, kindNameMap);
            // 在人脸矩形上方绘制提示文字（中文会乱码）
            putText(original, "people face", new Point(pos_x, pos_y), FONT_HERSHEY_COMPLEX, 1.0, new Scalar(0, 0, 255, 2.0));
        }
        frame = converter.convert(original);
        image = imageConverter.convert(frame);
        String fileName = file.getName();
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
        String newFileName = fileName.substring(0, fileName.lastIndexOf(".")) + "_result." + extension;
        ImageIO.write(image, extension, new File(file.getParent() + File.separator + newFileName));
    }

    /**
     * 人脸识别
     *
     * @param kindNameMap 人物名称集合
     */
    public static String oneFaceRecognize(Mat grayImg, Rect face, Map<Integer, String> kindNameMap) {
        String kindName = null;
        Mat mat = new Mat(grayImg, face);
        Size size = new Size(RESIZE_WIDTH, RESIZE_HEIGHT);
        // 核心代码，把检测到的人脸拿去识别
        // 调整到和训练一致的尺寸
        resize(mat, mat, size);

        // 推理结果的标签
        int[] labels = new int[1];
        // 推理结果的置信度
        double[] confidences = new double[1];
        try {
            // 推理(这一行可能抛出RuntimeException异常，因此要补货，否则会导致程序退出)
            FaceRecognizer faceRecognizer = FisherFaceRecognizer.create();
            // 加载的是训练时生成的模型
            faceRecognizer.read(faceRecognizerPath);
            // 设置门限，这个可以根据您自身的情况不断调整
            faceRecognizer.setThreshold(MAX_CONFIDENCE);
            //人脸检测
            faceRecognizer.predict(mat, labels, confidences);
        } catch (RuntimeException runtimeException) {
            runtimeException.printStackTrace();
        }
        // 得到分类编号后，从map中取得名字，用来显示
        if (kindNameMap.containsKey(labels[0])) {
            kindName = String.format("%s, confidence : %.4f", kindNameMap.get(labels[0]), confidences[0]);
        } else {
            // 取不到名字的时候，就显示unknown
            kindName = "unknown(" + labels[0] + ")";
        }
        System.out.println(kindName);
        return kindName;
    }

}