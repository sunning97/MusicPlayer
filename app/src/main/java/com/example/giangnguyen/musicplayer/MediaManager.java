package com.example.giangnguyen.musicplayer;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.SimpleFormatter;

/**
 * Created by Giang Nguyen on 14/11/2017.
 */

public class MediaManager implements MediaPlayer.OnCompletionListener {
    private static final int IDLE = 0;
    private static final int PLAYING = 1;
    private static final int PAUSE = 2;
    private static final int STOP = 3;
    private int state = IDLE;
    private int index = 0;

    private boolean isShuffle = false;

    public static final int REPEAT_ALL = 0;
    public static final int REPEAT_OFF = 1;
    public static final int REPEAT_ONE = 2;
    private int repeatMode = REPEAT_OFF;

    private MediaPlayer mediaPlayer;
    private Context context;
    private ArrayList<Song> listSong;

    public MediaManager(Context context) {
        this.context = context;
        initData();
    }

    public boolean isShuffle() {
        return isShuffle;
    }

    public void setShuffle(boolean shuffle) {
        isShuffle = shuffle;
    }

    public int getRepeatMode() {
        return repeatMode;
    }

    public void setRepeatMode(int repeatMode) {
        this.repeatMode = repeatMode;
    }

    private void initData() {
        mediaPlayer = new MediaPlayer();
        listSong = new ArrayList<>();

        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String projection[] = new String[]{
                MediaStore.Audio.AudioColumns.TITLE,
                MediaStore.Audio.AudioColumns.DATA,
                MediaStore.Audio.AudioColumns.ALBUM,
                MediaStore.Audio.AudioColumns.ARTIST,
                MediaStore.Audio.AudioColumns.DURATION
        };

        String where = MediaStore.Audio.AudioColumns.DISPLAY_NAME + " LIKE '%.mp3'";
        Cursor cursor = context.getContentResolver().query(songUri, projection, where, null, null);
        if (cursor == null) {
            return;
        }
        cursor.moveToFirst();

        int indexTitle = cursor.getColumnIndex(projection[0]);
        int indexData = cursor.getColumnIndex(projection[1]);
        int indexAlbum = cursor.getColumnIndex(projection[2]);
        int indexArtist = cursor.getColumnIndex(projection[3]);
        int indexDuration = cursor.getColumnIndex(projection[4]);

        String name, path, album, artist;
        int duration;

        while (!cursor.isAfterLast()) {
            name = cursor.getString(indexTitle);
            path = cursor.getString(indexData);
            album = cursor.getString(indexAlbum);
            artist = cursor.getString(indexArtist);
            duration = cursor.getInt(indexDuration);

            listSong.add(new Song(name, path, album, artist, duration));
            cursor.moveToNext();
        }
        cursor.close();


        String where1 = MediaStore.Audio.AudioColumns.DISPLAY_NAME + " LIKE '%.flac'";
        Cursor cursor1 = context.getContentResolver().query(songUri, projection, where1, null, null);
        if (cursor1 == null) {
            return;
        }
        cursor1.moveToFirst();

        int indexTitle1 = cursor1.getColumnIndex(projection[0]);
        int indexData1 = cursor1.getColumnIndex(projection[1]);
        int indexAlbum1 = cursor1.getColumnIndex(projection[2]);
        int indexArtist1 = cursor1.getColumnIndex(projection[3]);
        int indexDuration1 = cursor1.getColumnIndex(projection[4]);

        String name1, path1, album1, artist1;
        int duration1;

        while (!cursor1.isAfterLast()) {
            name1 = cursor1.getString(indexTitle);
            path1 = cursor1.getString(indexData);
            album1 = cursor1.getString(indexAlbum);
            artist1 = cursor1.getString(indexArtist);
            duration1 = cursor1.getInt(indexDuration);

            listSong.add(new Song(name1, path1, album1, artist1, duration1));
            cursor1.moveToNext();
        }
        cursor1.close();
    }

    public ArrayList<Song> getListSong() {
        return listSong;
    }

    public boolean play() {
        Song song = listSong.get(index);
        try {
            if (state == IDLE || state == STOP) {
                mediaPlayer.setDataSource(song.getPath());
                mediaPlayer.setOnCompletionListener(this);
                mediaPlayer.prepare();
                mediaPlayer.start();
                state = PLAYING;
                return true;
            } else if (state == PLAYING) {
                mediaPlayer.pause();
                state = PAUSE;
                return false;
            } else {
                mediaPlayer.start();
                state = PLAYING;
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void play(int pos) {
        index = pos;
        stop();
        play();
    }

    public void stop() {
        if (state == PLAYING || state == PAUSE) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            state = STOP;
        }
    }

    public boolean back() {
        if (index == 0) {
            index = listSong.size();
        }
        index--;
        stop();
        return play();
    }

    public boolean next() {
        if (isShuffle) {
            index = new Random().nextInt(listSong.size());
        } else {
            index = (index + 1) % listSong.size();
        }
        stop();
        return play();
    }

    public Song getcurrSong() {
        return listSong.get(index);
    }

    public int getCurrIndex() {
        return this.index;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        switch (repeatMode) {
            case REPEAT_ALL:
                index++;
                if (index == listSong.size()) {
                    index = 0;
                    stop();
                    play();
                }
                break;
            case REPEAT_OFF:
                if (index < (listSong.size() - 1)) {
                    index++;
                    stop();
                    play();
                }
                break;
            case REPEAT_ONE:
                stop();
                play();
                break;
        }
    }


    public boolean isPlaying() {
        return state == PLAYING || state == PAUSE;
    }

    public String getTimeText() {
        int currTime = mediaPlayer.getCurrentPosition();
        int totalTime = listSong.get(index).getDuration();
        SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");
        return dateFormat.format(currTime) + "/" + dateFormat.format(totalTime);
    }

    public int getCurrTime() {
        return mediaPlayer.getCurrentPosition();
    }

    public void seek(int progress) {
        mediaPlayer.seekTo(progress);
    }

    public String getTimeSong(int i) {
        int totalTime = listSong.get(i).getDuration();
        SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");
        return dateFormat.format(totalTime);
    }
}
