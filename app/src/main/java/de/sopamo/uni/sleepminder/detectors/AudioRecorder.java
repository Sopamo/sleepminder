package de.sopamo.uni.sleepminder.detectors;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.util.Arrays;

import de.sopamo.uni.sleepminder.AudioView;

public class AudioRecorder extends Thread {
    private boolean stopped = false;
    private static AudioRecord recorder = null;
    private static int N = 0;

    @Override
    public void run() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
        short[][]   buffers  = new short[256][160];
        int         ix       = 0;

        try { // ... initialise

            if(N == 0) {
                N = AudioRecord.getMinBufferSize(16000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);

                recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                        16000,
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT,
                        N * 10);
            }
            recorder.startRecording();

            short[] buffer = buffers[ix++ % buffers.length];

            N = recorder.read(buffer, 0, buffer.length);

            process(buffer);
            recorder.stop();
        } catch(Throwable x) {
            Log.e("AudioRecorder", "Error reading voice audio", x);
        } finally {
            close();
        }
        Log.e("record","done");
    }

    private void process(short[] buffer) {
        AudioView.instance.addPoint(calculateRMS(buffer));
        AudioView.instance.invalidate();
    }

    private double calculateRMS(short[] buffer) {
        long sum = 0;
        for(int i=0;i<buffer.length;i++) {
            sum += buffer[i] * buffer[i];
        }
        return Math.sqrt(sum/buffer.length);
    }

    public double mean(short[] m) {
        double sum = 0;
        for (short aM : m) {
            sum += aM;
        }
        return sum / m.length;
    }

    private void close() {
        stopped = true;
    }

}