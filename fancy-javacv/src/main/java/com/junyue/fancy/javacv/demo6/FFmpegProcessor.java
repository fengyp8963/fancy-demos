package com.junyue.fancy.javacv.demo6;

import lombok.extern.slf4j.Slf4j;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.*;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author fengyp
 */
@Slf4j
public class FFmpegProcessor {

    /**
     * 这个方法的url地址都必须是一样的类型 同为post
     */
    public static void convertMediaToM3u8ByHttp(InputStream inputStream, String m3u8Url, String infoUrl) throws IOException {

        avutil.av_log_set_level(avutil.AV_LOG_INFO);
        FFmpegLogCallback.set();

        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputStream)) {
            grabber.start();
            try (FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(m3u8Url, grabber.getImageWidth(), grabber.getImageHeight(), grabber.getAudioChannels())) {
                recorder.setFormat("hls");
                recorder.setOption("hls_time", "5");
                recorder.setOption("codec", "copy");//进行音视频的流复制（即直接拷贝）而无需重新编码
                recorder.setOption("hls_list_size", "0");
                recorder.setOption("hls_flags", "delete_segments");
                recorder.setOption("hls_delete_threshold", "1");
                recorder.setOption("hls_segment_type", "mpegts");
                recorder.setOption("hls_segment_filename", "http://localhost:8080/upload/enc-%d.ts");
                recorder.setOption("hls_key_info_file", infoUrl);

                recorder.setOption("method", "POST");
                //recorder.setFrameRate(25);
                recorder.setFrameRate(grabber.getFrameRate());
                recorder.setGopSize(2 * 25);
                recorder.setVideoQuality(1.0);
                //recorder.setVideoBitrate(10 * 1024);
                recorder.setVideoBitrate(grabber.getVideoBitrate());
                recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
                recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);
                recorder.start();

                Frame frame;
                while ((frame = grabber.grabImage()) != null) {
                    try {
                        recorder.record(frame);
                    } catch (FrameRecorder.Exception e) {
                        log.error("Error when recording frame to media file: ", e);
                    }
                }

                log.info("MP4 to M3U8 conversion completed.");
            }
        }
    }

}
