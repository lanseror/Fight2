package com.fight2.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.music.MusicManager;

import com.fight2.GameActivity;
import com.fight2.constant.SoundEnum;

public class F2SoundManager {
    private final boolean disable = false;
    private static F2SoundManager INSTANCE = new F2SoundManager();
    private final Map<SoundEnum, Music> datas = new HashMap<SoundEnum, Music>();
    private Music currentSound;

    private F2SoundManager() {
    }

    public void prepare(final GameActivity activity) throws IOException {
        MusicFactory.setAssetBasePath("sound/");
        final MusicManager musicManager = activity.getMusicManager();

        for (final SoundEnum soundEnum : SoundEnum.values()) {
            final Music music = MusicFactory.createMusicFromAsset(musicManager, activity, soundEnum.getUrl());
            datas.put(soundEnum, music);
        }

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
        currentSound.setLooping(looping);
        currentSound.play();

    }
}
