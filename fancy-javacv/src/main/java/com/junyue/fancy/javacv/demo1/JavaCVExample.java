package com.junyue.fancy.javacv.demo1;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.Mat;

import java.util.ArrayList;
import java.util.List;

/**
 * 我们使用JavaCV读取名为input.mp4的视频文件，并对每一帧进行处理（这里是简单地水平翻转）。
 * 通过导入相关的类和方法，我们可以直接使用JavaCV提供的功能。
 */
public class JavaCVExample {

    public static void main(String[] args) {
        String videoFilePath = "D:\\v1_DeMainV2_batch-2.webm";
        // 创建视频帧抓取器
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(videoFilePath)) {
            List<Frame> frames = new ArrayList<>();
            grabber.start();
            // 逐帧读取视频
            Frame frame;
            while ((frame = grabber.grabFrame()) != null) {
                // 处理视频帧
                // 您可以在这里对视频帧进行各种处理操作
                // 创建帧转换器
                OpenCVFrameConverter.ToMat opencvConverter = new OpenCVFrameConverter.ToMat();
                // 将JavaCV帧转换为OpenCV的Mat对象
                Mat mat = opencvConverter.convertToMat(frame);
                // 在这里可以使用OpenCV函数对Mat对象进行图像处理
                // 将OpenCV的Mat对象转换回JavaCV帧
                if (mat != null) {
                    // opencv_core.flip(mat, mat, 1);
                    // 进行清晰度增强处理
                    // opencv_imgproc.Laplacian(mat, mat, -1);
                    frames.add(opencvConverter.convert(mat));
                } else {
                    frames.add(frame);
                }
            }
            // 保存视频
            String videoNewFilePath = "D:\\v1_DeMainV2_batch-211.mp4";
            try (FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(videoNewFilePath, grabber.getImageWidth(), grabber.getImageHeight())) {
                recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
                recorder.setFormat("mp4");
                recorder.start();
                for (Frame reversedFrame : frames) {
                    recorder.record(reversedFrame);
                }
                recorder.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 关闭视频帧抓取器
            grabber.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
