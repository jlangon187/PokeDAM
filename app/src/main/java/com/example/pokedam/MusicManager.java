package com.example.pokedam;

import android.content.Context;
import android.media.MediaPlayer;

public class MusicManager {

    private static MediaPlayer mediaPlayer;

    // Volumen normal y volumen bajo (durante video)
    private static final float VOLUME_NORMAL = 1.0f;
    private static final float VOLUME_LOW = 0.15f; // 15% del volumen

    public static void start(Context context) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, R.raw.pokemon);
            mediaPlayer.setLooping(true);
            mediaPlayer.setVolume(VOLUME_NORMAL, VOLUME_NORMAL); // Aseguramos volumen al inicio
            mediaPlayer.start();
        } else if (!mediaPlayer.isPlaying()) {
            mediaPlayer.setVolume(VOLUME_NORMAL, VOLUME_NORMAL);
            mediaPlayer.start();
        }
    }

    public static void lowerVolume() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.setVolume(VOLUME_LOW, VOLUME_LOW);
        }
    }

    public static void restoreVolume() {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(VOLUME_NORMAL, VOLUME_NORMAL);
        }
    }

    public static void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}