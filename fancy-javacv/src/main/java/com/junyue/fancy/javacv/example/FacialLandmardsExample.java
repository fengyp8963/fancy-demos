//package com.junyue.fancy.fancycv.example;
//
//import com.emaraic.jdlib.Jdlib;
//import com.emaraic.utils.FaceDescriptor;
//import com.emaraic.utils.ImageUtils;
//
//import javax.imageio.ImageIO;
//import java.awt.*;
//import java.awt.image.BufferedImage;
//import java.io.File;
//import java.io.IOException;
//
///**
// * 人脸检测
// */
//public class FacialLandmardsExample {
//
//    private static BufferedImage loadImage(String imagepath) {
//        BufferedImage img = null;
//        try {
//            img = ImageIO.read(new File(imagepath));
//        } catch (IOException e) {
//            System.err.println("Error During Loading File: " + imagepath);
//        }
//        return img;
//    }
//
//    public static BufferedImage resize(BufferedImage img, int w, int h) {
//        Image tempimg = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
//        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
//        Graphics2D g2d = image.createGraphics();
//        g2d.drawImage(tempimg, 0, 0, null);
//        g2d.dispose();
//        return image;
//    }
//
//    public static void main(String[] args) {
//
//        String facialLandmarksModelPath = "/models/shape_predictor_68_face_landmarks.dat";
//        String imagepath = "/doc/bald_guys.jpg";
//
//        Jdlib jdlib = new Jdlib(facialLandmarksModelPath);
//
//        BufferedImage img = loadImage(imagepath);
//
//        ArrayList<FaceDescriptor> faces = jdlib.getFaceLandmarks(img);
//
//        for (FaceDescriptor face : faces) {
//            ImageUtils.drawFaceDescriptor(img, face);
//        }
//
//        img = resize(img, 800, 800);
//
//        ImageUtils.showImage(img);
//    }
//}
