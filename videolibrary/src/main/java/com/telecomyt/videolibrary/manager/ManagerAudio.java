package com.telecomyt.videolibrary.manager;

import android.content.Context;
import android.media.AudioManager;

/**
 * Created by lbx on 2017/9/14.
 */

public class ManagerAudio {

    //打开扬声器
    public static void OpenSpeaker(Context context) {

        try {
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            audioManager.setMode(AudioManager.ROUTE_SPEAKER);
            int currVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
            if (!audioManager.isWiredHeadsetOn() && !audioManager.isSpeakerphoneOn()) {
                audioManager.setSpeakerphoneOn(true);
                audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                        audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL),
                        AudioManager.STREAM_VOICE_CALL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
