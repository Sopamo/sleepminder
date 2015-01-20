package de.sopamo.uni.sleepminder;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import de.sopamo.uni.sleepminder.detectors.AudioRecorder;

public class AudioView extends View {
    Paint paint;
    ArrayList<Double> points = null;
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

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        for(int i = 0;i<points.size();i++) {
            canvas.drawCircle(100+i*2, (int)(points.get(i)+600), 2, paint);
        }

        canvas.drawText("Lux: " + lux,100f,200f,paint);


        AudioRecorder recorder = new AudioRecorder();
        recorder.run();

    }
}
