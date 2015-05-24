package de.sopamo.uni.sleepminder.recorders;

import android.app.Application;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import de.sopamo.uni.sleepminder.AudioView;
import de.sopamo.uni.sleepminder.MyApplication;

public class AudioRecorder extends Thread {
    private boolean stopped = false;
    private static AudioRecord recorder = null;
    private static int N = 0;

    @Override
    public void run() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
        short[]   buffer  = new short[1600];

        try { // ... initialise
            if(N == 0) {
                N = AudioRecord.getMinBufferSize(16000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
                if(N < 1600) {
                    N = 1600;
                }
                Log.e("foo",N+"");
                recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                        16000,
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT,
                        N);
            }
            recorder.startRecording();

            N = recorder.read(buffer, 0, buffer.length);
            process(buffer);
            recorder.stop();
        } catch(Throwable x) {
            Log.e("AudioRecorder", "Error reading voice audio", x);
        } finally {
            close();
        }
        Log.e("record", "done");
        if(!stopped) {
            this.run();
        }
    }

    private void process(short[] buffer) {

        MyApplication.noiseModel.addRLH(calculateRLH(buffer));
        MyApplication.noiseModel.addRMS(calculateRMS(buffer));
        MyApplication.noiseModel.addVAR(calculateVar(buffer));

        MyApplication.noiseModel.calculateFrame();

        //AudioView.instance.addPoint2(MyApplication.noiseModel.getNormalizedRLH(), MyApplication.noiseModel.getNormalizedVAR());
        //AudioView.lux = (float)(MyApplication.noiseModel.getNormalizedRMS());
        //AudioView.instance.invalidate();
    }

    private double calculateRMS(short[] buffer) {
        long sum = 0;
        for(int i=0;i<buffer.length;i++) {
            sum += Math.pow(buffer[i],2);
        }
        return Math.sqrt(sum / buffer.length);
    }

    private double calculateRMS(float[] buffer) {
        long sum = 0;
        for(int i=0;i<buffer.length;i++) {
            sum += Math.pow(buffer[i],2);
        }
        return Math.sqrt(sum / buffer.length);
    }

    private double calculateLowFreqRMS(short[] buffer) {
        float[] lowFreq = new float[buffer.length];

        lowFreq[0] = 0;

        float a = 0.25f;

        for(int i=1;i<buffer.length;i++) {
            lowFreq[i] = lowFreq[i-1] + a * (buffer[i] - lowFreq[i-1]);
        }

        return calculateRMS(buffer);
    }

    private double calculateHighFreqRMS(short[] buffer) {
        float[] highFreq = new float[buffer.length];

        highFreq[0] = 0;

        float a = 0.25f;

        for(int i=1;i<buffer.length;i++) {
            highFreq[i] = a * (highFreq[i-1] + buffer[i] - buffer[i-1]);
        }

        return calculateRMS(highFreq);
    }

    private double calculateRLH(short[] buffer) {
        double rmsh = calculateHighFreqRMS(buffer);
        double rmsl = calculateLowFreqRMS(buffer);
        if(rmsh == 0) return 0;
        if(rmsl == 0) return 0;
        return  rmsl / rmsh;
    }

    /**
     * Calculates the var of one frame
     *
     * @param buffer
     * @return
     */
    private double calculateVar(short[] buffer) {

        double mean = calculateMean(buffer);
        double var = 0;
        for(short s: buffer) {
            var += Math.pow(s - mean,2);
        }
        return var / buffer.length;
    }

    /**
     * Calculate the mean of one fram
     *
     * @param buffer
     * @return
     */
    private double calculateMean(short[] buffer) {
        double mean = 0;
        for(short s: buffer) {
            mean += s;
        }
        return mean / buffer.length;
    }

    public void close() {
        stopped = true;
    }

}