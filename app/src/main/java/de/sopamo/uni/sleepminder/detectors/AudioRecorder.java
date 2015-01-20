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
                N = AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);

                recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                        8000,
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
        for(short b: buffer) {
            AudioView.instance.addPoint((double) (b/10));
        }
        AudioView.instance.invalidate();
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