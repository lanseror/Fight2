package com.fight2.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

import com.fight2.GameActivity;
import com.fight2.constant.MusicEnum;

public class F2MusicManager {
    private final boolean disable = false;
    private static F2MusicManager INSTANCE = new F2MusicManager();
    private final Map<MusicEnum, Music> datas = new HashMap<MusicEnum, Music>();
    private Music currentMusic;
    private MusicEnum currentMusicEnum;
    private GameActivity activity;
    private boolean isPause = false;

    private F2MusicManager() {
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

    public void pause() {
        for (final Entry<MusicEnum, Music> entry : datas.entrySet()) {
            final Music music = entry.getValue();
            if (music != null && !music.isReleased()) {
                if (music != currentMusic) {
                    music.stop();
                    music.release();
                }
            }
        }
        datas.clear();
        if (currentMusic != null && !currentMusic.isReleased() && currentMusic.isPlaying()) {
            currentMusic.pause();
            datas.put(currentMusicEnum, currentMusic);
            isPause = true;
        } else if (currentMusic != null && !currentMusic.isReleased()) {
            currentMusic.stop();
            currentMusic.release();
        }
    }

    public void resume() {
        if (currentMusic != null && !currentMusic.isReleased() && isPause) {
            currentMusic.resume();
        } else {
            isPause = false;
        }
    }

    public void prepare(final GameActivity activity) throws IOException {
        this.activity = activity;
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
            // oldMusic.pause();
            // oldMusic.seekTo(0);
            oldMusic.stop();
            oldMusic.release();
        }
    }

    public void playMusic(final MusicEnum musicEnum, final boolean looping) {
        if (disable) {
            return;
        }
        currentMusic = datas.get(musicEnum);
        if (currentMusic == null || currentMusic.isReleased()) {
            MusicFactory.setAssetBasePath("music/");
            try {
                final Music music = MusicFactory.createMusicFromAsset(activity.getMusicManager(), activity, musicEnum.getUrl());
                music.setOnCompletionListener(new OnCompletionListener() {
                    @Override
                    public void onCompletion(final MediaPlayer mp) {
                        music.stop();
                    }
                });
                currentMusic = music;
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
            datas.put(musicEnum, currentMusic);
        }
        currentMusicEnum = musicEnum;
        // if (oldMusic != null && !oldMusic.isReleased()) {
        // oldMusic.pause();
        // oldMusic.seekTo(0);
        // }
        currentMusic.setLooping(looping);
        currentMusic.play();
    }
}
