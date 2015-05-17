package de.sopamo.uni.sleepminder;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

import de.sopamo.uni.sleepminder.recorders.AudioRecorder;

public class AudioView extends View {
    Paint paint;
    ArrayList<Double> points = null;
    ArrayList<Double[]> points2 = null;
    public static AudioView instance = null;
    public static float lux = 0;

    public AudioView(Context context) {
        super(context);
        init();
    }

    public AudioView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AudioView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(1);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(80);
        points = new ArrayList<>();
        points2 = new ArrayList<>();
        instance = this;

        AudioRecorder recorder = new AudioRecorder();
        recorder.run();
    }

    public void addPoint(Double point) {
        if(points.size() > 500) {
            points.remove(0);
        }
        points.add(point);
    }

    public void addPoint2(Double x, Double y) {
        if(points2.size() > 100) {
            points2.remove(0);
        }
        Double[] p = new Double[2];
        p[0] = x;
        p[1] = y;
        points2.add(p);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        //for(int i = 0;i<points.size();i++) {
            //canvas.drawCircle(100+i*2, (int)(points.get(i)+600), 2, paint);
        //}

        for(int i = 0;i<points2.size();i++) {
            Double[] p = points2.get(i);
            canvas.drawCircle((float)(500 + p[0]*100),(float)(500+p[1]*100), 2, paint);
        }
        if(points2.size() > 0) {
            Double[] curr = points2.get(points2.size() - 1);
            canvas.drawText("RLH: " + curr[0]*100, 100f, 200f, paint);
            canvas.drawText("VAR: " + curr[1]*100, 100f, 300f, paint);
            canvas.drawText("RMS: " + lux, 100f, 400f, paint);
        }

        AudioRecorder recorder = new AudioRecorder();
        recorder.run();

    }
}
