package com.xiao.wisdom.bed.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.xiao.wisdom.bed.R;

/**
 * Created by Administrator on 2018/7/4.
 */

public class SoundPlayUtils {
    // SoundPool对象
    public static SoundPool mSoundPlayer = new SoundPool(10,
            AudioManager.STREAM_SYSTEM, 5);
    public static SoundPlayUtils soundPlayUtils;
    // 上下文
    static Context mContext;

    /**
     * 初始化
     *
     * @param context
     */
    public static SoundPlayUtils init(Context context) {
        if (soundPlayUtils == null) {
            soundPlayUtils = new SoundPlayUtils();
        }
        // 初始化声音
        mContext = context;
        mSoundPlayer.load(mContext,R.raw.raw_di , 1);// 1
        return soundPlayUtils;
    }

    /**
     * 播放声音
     */
    public static void play() {
        mSoundPlayer.play(1, 1, 1, 0, 0, 1);
    }
}
