package com.fight2.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.music.MusicManager;

import com.fight2.GameActivity;
import com.fight2.constant.MusicEnum;

public class F2MusicManager {
    private final boolean disable = false;
    private static F2MusicManager INSTANCE = new F2MusicManager();
    private final Map<MusicEnum, Music> datas = new HashMap<MusicEnum, Music>();
    private Music currentMusic;

    private F2MusicManager() {
    }

    public void prepare(final GameActivity activity) throws IOException {
        MusicFactory.setAssetBasePath("sound/");
        final MusicManager musicManager = activity.getMusicManager();

        for (final MusicEnum musicEnum : MusicEnum.values()) {
            final Music music = MusicFactory.createMusicFromAsset(musicManager, activity, musicEnum.getUrl());
            datas.put(musicEnum, music);
        }

    }

    public static F2MusicManager getInstance() {
        return INSTANCE;
    }

    public void playMusic(final MusicEnum musicEnum) {
        playMusic(musicEnum, false);
    }

    public void stopMusic() {
        if (disable) {
            return;
        }
        final Music oldMusic = currentMusic;
        if (oldMusic != null && !oldMusic.isReleased()) {
            oldMusic.pause();
            oldMusic.seekTo(0);
        }
    }

    public void playMusic(final MusicEnum musicEnum, final boolean looping) {
        if (disable) {
            return;
        }
        final Music oldMusic = currentMusic;
        currentMusic = datas.get(musicEnum);
        if (oldMusic != null && !oldMusic.isReleased()) {
            oldMusic.pause();
            oldMusic.seekTo(0);
        }
        currentMusic.setLooping(looping);
        currentMusic.play();

    }
}
