package com.fight2.input.touch.detector;

import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.SurfaceScrollDetector;

public class F2ScrollDetector extends SurfaceScrollDetector {
    private TouchEvent sceneTouchEvent;

    public F2ScrollDetector(final IScrollDetectorListener pScrollDetectorListener) {
        super(pScrollDetectorListener);
    }

    public TouchEvent getSceneTouchEvent() {
        return sceneTouchEvent;
    }

    public void setSceneTouchEvent(final TouchEvent sceneTouchEvent) {
        this.sceneTouchEvent = sceneTouchEvent;
    }

}
