package de.sopamo.uni.sleepminder.lib.detection;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class NoiseModel {
    private List<Double> RMS;
    private List<Double> RLH;
    private List<Double> VAR;

    private int snore = 0;
    private int movement = 0;

    public NoiseModel() {
        RMS = new ArrayList<>();
        RLH = new ArrayList<>();
        VAR = new ArrayList<>();
    }

    public void addRMS(Double rms) {
        if(RMS.size() >= 100) {
            RMS.remove(0);
        }
        RMS.add(rms);
    }
    public void addRLH(Double rlh) {
        if(RLH.size() >= 100) {
            RLH.remove(0);
        }
        RLH.add(rlh);
    }
    public void addVAR(Double var) {
        if(VAR.size() >= 100) {
            VAR.remove(0);
        }
        VAR.add(var);
    }

    public double getNormalizedRMS() {
        if(RMS.size() <= 1) return 0d;

        return (RMS.get(RMS.size()-1) - mean(RMS)) / std(RMS);
    }

    public double getNormalizedRLH() {
        if(RLH.size() <= 1) return 0d;

        return (RLH.get(RLH.size()-1) - mean(RLH)) / std(RLH);
    }

    public double getNormalizedVAR() {
        if(VAR.size() <= 1) return 0d;
        return (VAR.get(VAR.size()-1) - mean(VAR)) / std(VAR);
    }

    public double getLastRMS() {
        if(RMS.size() <= 1) return 0d;
        return RMS.get(RMS.size()-1);
    }
    public double getLastVAR() {
        if(VAR.size() <= 1) return 0d;
        return VAR.get(VAR.size()-1);
    }
    public double getLastRLH() {
        if(RLH.size() <= 1) return 0d;
        return RLH.get(RLH.size()-1);
    }

    /**
     * This detects which event occured in the current frame
     */
    public void calculateFrame() {
        /*if(getNormalizedVAR() > 1) { // Filter noise
            if(getNormalizedRLH() > 1) {
                snore++;
            } else {
                if(getNormalizedRMS() > 0.5) {
                    movement++;
                }
            }
        }*/
        if(getLastRLH() > 10) {
            if(getNormalizedVAR() > 2) {
                snore++;
                Log.e("event","snore");
            }
        } else {
            if(getLastRMS() > 15 && getNormalizedVAR() > 0.5d && (getLastRLH() > 1d || getLastRLH() < -1d)) {
                movement++;
                Log.e("event","movement");
            }
        }
    }

    private double mean(List<Double> list) {
        double sum = 0;
        for (int i = 0; i < list.size(); i++) {
            sum += list.get(i);
        }
        return sum / list.size();
    }

    private double std(List<Double> list) {
        double mean = mean(list);
        double var = 0;
        for(int i = 0; i < list.size(); i++) {
            var += Math.pow(list.get(i) - mean,2);
        }
        return Math.sqrt(var / list.size());
    }

    public int getEvent() {
        if(snore > 5) {
            return 1;
        } else {
            if(movement > 1) {
                return 2;
            }
        }
        return 0;
    }

    public int getIntensity() {
        if(getEvent() == 1) {
            return snore;
        } else if(getEvent() == 2) {
            return movement;
        }
        return 0;
    }

    public void resetEvents() {
        snore = 0;
        movement = 0;
    }

}
