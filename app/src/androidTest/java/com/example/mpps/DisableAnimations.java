package com.example.mpps;

import android.content.Context;
import android.provider.Settings;

public class DisableAnimations {

    public static void disableSystemAnimations(Context context) {
        setSystemAnimationsState(context, 0.0f);
    }

    public static void enableSystemAnimations(Context context) {
        setSystemAnimationsState(context, 1.0f);
    }

    private static void setSystemAnimationsState(Context context, float scale) {
        try {
            Settings.Global.putFloat(context.getContentResolver(),
                    Settings.Global.WINDOW_ANIMATION_SCALE, scale);
            Settings.Global.putFloat(context.getContentResolver(),
                    Settings.Global.TRANSITION_ANIMATION_SCALE, scale);
            Settings.Global.putFloat(context.getContentResolver(),
                    Settings.Global.ANIMATOR_DURATION_SCALE, scale);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
