package de.sopamo.uni.sleepminder.lib.recorders;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.IOException;

import de.sopamo.uni.sleepminder.lib.DebugView;
import de.sopamo.uni.sleepminder.lib.detection.FeatureExtractor;
import de.sopamo.uni.sleepminder.lib.detection.NoiseModel;

public class AudioRecorder extends Thread {
    private boolean stopped = false;
    private static AudioRecord recorder = null;
    private static int N = 0;
    private NoiseModel noiseModel;
    private DebugView debugView;
    private short[] buffer;
    private FeatureExtractor featureExtractor;


    public AudioRecorder(NoiseModel noiseModel, DebugView debugView) {

        this.noiseModel = noiseModel;
        this.debugView = debugView;
        this.featureExtractor = new FeatureExtractor(noiseModel);
    }

    @Override
    public void run() {

        capture();

    }

    private void capture() {
        int i = 0;
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
        if(buffer == null) {
            buffer  = new short[1600];
        }

        if(N == 0 || (recorder == null || recorder.getState() != AudioRecord.STATE_INITIALIZED)) {
            N = AudioRecord.getMinBufferSize(16000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
            if(N < 1600) {
                N = 1600;
            }
            recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    16000,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    N);
        }
        recorder.startRecording();

        while(!this.stopped) {
            N = recorder.read(buffer, 0, buffer.length);

            process(buffer);
        }
        recorder.stop();
        recorder.release();

    }

    private void process(short[] buffer) {

        featureExtractor.update(buffer);

        if(debugView != null) {
            /*debugView.addPoint2(noiseModel.getNormalizedRLH(), noiseModel.getNormalizedVAR());
            debugView.setLux((float) (noiseModel.getNormalizedRMS()));*/
            debugView.addPoint2(noiseModel.getLastRLH(), noiseModel.getNormalizedVAR());
            debugView.setLux((float) (noiseModel.getLastRMS()));
            debugView.post(new Runnable() {
                @Override
                public void run() {
                    debugView.invalidate();
                }
            });

        }

    }


    public void close() {
        stopped = true;
    }

}