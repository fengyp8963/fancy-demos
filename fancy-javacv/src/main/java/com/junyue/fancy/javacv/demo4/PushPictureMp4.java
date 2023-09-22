package com.junyue.fancy.javacv.demo4;

import lombok.extern.slf4j.Slf4j;
import org.bytedeco.ffmpeg.avcodec.AVCodecParameters;
import org.bytedeco.ffmpeg.avformat.AVFormatContext;
import org.bytedeco.ffmpeg.avformat.AVStream;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * 读取指定的mp4文件，推送到SRS服务器，增加字幕
 */
@Slf4j
public class PushPictureMp4 {

    /**
     * 本地MP4文件的完整路径(两分零五秒的视频)
     */
    private static final String MP4_FILE_PATH = "D:\\sample-mp4-file.mp4";

    /**
     * SRS的推流地址
     */
    private static final String SRS_PUSH_ADDRESS = "rtmp://192.168.50.47:11935/live/livestream";

    /**
     * 读取指定的mp4文件，推送到SRS服务器
     *
     * @param sourceFilePath 视频文件的绝对路径
     * @param pushAddress    推流地址
     * @throws Exception
     */
    private static void grabAndPush(String sourceFilePath, String pushAddress) throws Exception {
        // ffmepg日志级别
        avutil.av_log_set_level(avutil.AV_LOG_ERROR);
        FFmpegLogCallback.set();

        // 实例化帧抓取器对象，将文件路径传入
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(sourceFilePath);

        long startTime = System.currentTimeMillis();

        log.info("开始初始化帧抓取器");

        // 初始化帧抓取器，例如数据结构（时间戳、编码器上下文、帧对象等），
        // 如果入参等于true，还会调用avformat_find_stream_info方法获取流的信息，放入AVFormatContext类型的成员变量oc中
        grabber.start(true);

        log.info("帧抓取器初始化完成，耗时[{}]毫秒", System.currentTimeMillis() - startTime);

        // grabber.start方法中，初始化的解码器信息存在放在grabber的成员变量oc中
        AVFormatContext avFormatContext = grabber.getFormatContext();

        // 文件内有几个媒体流（一般是视频流+音频流）
        int streamNum = avFormatContext.nb_streams();

        // 没有媒体流就不用继续了
        if (streamNum < 1) {
            log.error("文件内不存在媒体流");
            return;
        }

        // 取得视频的帧率
        int frameRate = (int) grabber.getVideoFrameRate();

        log.info("视频帧率[{}]，视频时长[{}]秒，媒体流数量[{}]", frameRate, avFormatContext.duration() / 1000000, avFormatContext.nb_streams());

        // 遍历每一个流，检查其类型
        for (int i = 0; i < streamNum; i++) {
            AVStream avStream = avFormatContext.streams(i);
            AVCodecParameters avCodecParameters = avStream.codecpar();
            log.info("流的索引[{}]，编码器类型[{}]，编码器ID[{}]", i, avCodecParameters.codec_type(), avCodecParameters.codec_id());
        }

        // 视频宽度
        int frameWidth = grabber.getImageWidth();
        // 视频高度
        int frameHeight = grabber.getImageHeight();
        // 音频通道数量
        //int audioChannels = grabber.getAudioChannels(); //Specified channel layout '5.1' is not supported
        int audioChannels = 1;

        log.info("视频宽度[{}]，视频高度[{}]，音频通道数[{}]", frameWidth, frameHeight, audioChannels);

        // 实例化FFmpegFrameRecorder，将SRS的推送地址传入
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(pushAddress, frameWidth, frameHeight, audioChannels);

        // 设置编码格式
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);

        // 设置封装格式
        recorder.setFormat("flv");

        // 一秒内的帧数
        recorder.setFrameRate(frameRate);

        // 两个关键帧之间的帧数
        recorder.setGopSize(frameRate);

        // 设置音频通道数，与视频源的通道数相等
        recorder.setAudioChannels(audioChannels);

        recorder.setPixelFormat(avutil.AV_PIX_FMT_YUV420P);


        startTime = System.currentTimeMillis();
        log.info("开始初始化帧抓取器");

        // 初始化帧录制器，例如数据结构（音频流、视频流指针，编码器），
        // 调用av_guess_format方法，确定视频输出时的封装方式，
        // 媒体上下文对象的内存分配，
        // 编码器的各项参数设置
        recorder.start();

        log.info("帧录制初始化完成，耗时[{}]毫秒", System.currentTimeMillis() - startTime);

        Frame frame;

        startTime = System.currentTimeMillis();

        log.info("开始推流");

        long videoTS = 0;

        int videoFrameNum = 0;
        int audioFrameNum = 0;
        int dataFrameNum = 0;

        // 假设一秒钟15帧，那么两帧间隔就是(1000/15)毫秒
        int interVal = 1000 / frameRate;
        // 发送完一帧后sleep的时间，不能完全等于(1000/frameRate)，不然会卡顿，
        // 要更小一些，这里取八分之一
        interVal /= 8;
        Java2DFrameConverter converter = new Java2DFrameConverter();

        // 构造测试字幕
        String[] test = {"世上无难事", "只怕有心人", "只要思想不滑坡", "办法总比困难多", "长江后浪推前浪", "前浪死在沙滩上"};
        // 为连续的50帧设置同一个测试字幕文本
        ArrayList<String> testStr = new ArrayList<>();
        for (int i = 0; i < 300; i++) {
            testStr.add(test[i / 50]);
        }
        int i = 0;

        // 持续从视频源取帧
        while (null != (frame = grabber.grab())) {
            videoTS = 1000 * (System.currentTimeMillis() - startTime);

            // 时间戳
            recorder.setTimestamp(videoTS);

            // 有图像，就把视频帧加一
            if (null != frame.image) {
                videoFrameNum++;
//
//                IplImage iplImage = Java2DFrameUtils.toIplImage(frame);
//                BufferedImage buffImg = Java2DFrameUtils.toBufferedImage(iplImage);
//                Graphics2D graphics = buffImg.createGraphics();
//                graphics.setColor(Color.BLUE);
//                graphics.setFont(new Font("微软雅黑", Font.PLAIN, 12));
//                graphics.drawString(LocalDateTime.now().toString(), iplImage.width() / 2, iplImage.height() - 50);
//                graphics.dispose();
//                frame = Java2DFrameUtils.toFrame(buffImg);

                BufferedImage bufferedImage = converter.getBufferedImage(frame);
                // 对图片进行文本合入
                bufferedImage = addSubtitle(bufferedImage, testStr.get(i++ % 300));
                // 视频帧赋值，写入输出流
                frame.image = converter.getFrame(bufferedImage).image;
            }

            // 有声音，就把音频帧加一
            if (null != frame.samples) {
                audioFrameNum++;
            }

            // 有数据，就把数据帧加一
            if (null != frame.data) {
                dataFrameNum++;
            }

            // 取出的每一帧，都推送到SRS
            recorder.record(frame);

            // 停顿一下再推送
            Thread.sleep(interVal);
        }

        log.info("推送完成，视频帧[{}]，音频帧[{}]，数据帧[{}]，耗时[{}]秒", videoFrameNum, audioFrameNum, dataFrameNum, (System.currentTimeMillis() - startTime) / 1000);

        // 关闭帧录制器
        recorder.close();
        // 关闭帧抓取器
        grabber.close();
    }

    public static void main(String[] args) throws Exception {
        grabAndPush(MP4_FILE_PATH, SRS_PUSH_ADDRESS);
    }


    /**
     * 图片添加文本
     *
     * @param bufImg
     * @param subTitleContent
     * @return
     */
    private static BufferedImage addSubtitle(BufferedImage bufImg, String subTitleContent) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 添加字幕时的时间
        Font font = new Font("微软雅黑", Font.BOLD, 12);
        String timeContent = sdf.format(new Date());
        Graphics2D graphics = bufImg.createGraphics();
        FontMetrics fontMetrics = graphics.getFontMetrics(font);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
        //设置图片背景
        graphics.drawImage(bufImg, 0, 0, bufImg.getWidth(), bufImg.getHeight(), null);
        //设置左上方时间显示
        graphics.setColor(Color.orange);
        graphics.setFont(font);
        graphics.drawString(timeContent, 0, fontMetrics.getAscent());
        // 计算文字长度，计算居中的x点坐标
        int textWidth = fontMetrics.stringWidth(subTitleContent);
        int widthX = (bufImg.getWidth() - textWidth) / 2;
        graphics.setColor(Color.red);
        graphics.setFont(font);
        graphics.drawString(subTitleContent, widthX, bufImg.getHeight() - fontMetrics.getAscent());
        graphics.dispose();
        return bufImg;
    }
}