package com.fight2.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;

import com.fight2.GameActivity;
import com.fight2.constant.SoundEnum;

public class F2SoundManager {
    private final boolean disable = false;
    private static F2SoundManager INSTANCE = new F2SoundManager();
    private final Map<SoundEnum, Music> datas = new HashMap<SoundEnum, Music>();
    private Music currentSound;
    private GameActivity activity;

    private F2SoundManager() {
    }

    public void prepare(final GameActivity activity) throws IOException {
        this.activity = activity;
    }

    public void destroy() {
        for (final Music music : datas.values()) {
            if (music != null && !music.isReleased()) {
                music.stop();
                music.release();
            }
        }
        datas.clear();
    }

    public static F2SoundManager getInstance() {
        return INSTANCE;
    }

    public void play(final SoundEnum soundEnum) {
        play(soundEnum, false);
    }

    public void stop() {
        if (disable) {
            return;
        }
        final Music oldMusic = currentSound;
        if (oldMusic != null && !oldMusic.isReleased()) {
            oldMusic.pause();
            oldMusic.seekTo(0);
        }
    }

    public void play(final SoundEnum soundEnum, final boolean looping) {
        if (disable) {
            return;
        }
        currentSound = datas.get(soundEnum);
        if (currentSound == null) {
            MusicFactory.setAssetBasePath("sound/");
            try {
                currentSound = MusicFactory.createMusicFromAsset(activity.getMusicManager(), activity, soundEnum.getUrl());
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
            datas.put(soundEnum, currentSound);
        }
        currentSound.setLooping(looping);
        currentSound.play();
    }
}
