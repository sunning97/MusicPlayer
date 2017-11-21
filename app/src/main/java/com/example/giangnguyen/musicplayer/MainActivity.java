package com.example.giangnguyen.musicplayer;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private ImageView iv_repeat, iv_prev, iv_play, iv_next, iv_stop, iv_shuffle;
    private TextView tv_song_name, tv_artist, tv_order, tv_duration;
    private SeekBar sb_seekbar_song;

    NotificationCompat.Builder builder;
    private NotificationManager notifyMng;
    private final int NOTIFICATION_ID = 001;


    private static final int LEVEL_PLAY = 0;
    private static final int LEVEL_PAUSE = 1;
    private static final int LEVEL_SHUFFLE_ON = 1;
    private static final int LEVEL_SHUFFLE_OFF = 0;
    private static final int LEVEL_REPEAT_ONE = 2;
    private static final int LEVEL_REPEAT_OFF = 1;
    private static final int LEVEL_REPEAT_ALL = 0;

    private int levelPlay = LEVEL_PLAY;
    private int levelRepeat = LEVEL_REPEAT_OFF;
    private int levelShuffle = LEVEL_SHUFFLE_OFF;

    private static final String PREV_ACTION = "com.meo.PREV";
    private static final String PLAY_ACTION = "com.meo.PLAY";
    private static final String NEXT_ACTION = "com.meo.NEXT";

    private int progress;

    private MediaManager mediaManager;

    private ListView lv_list_song;
    private ArrayList<Song> listSong;
    private SongAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkAndRequestPerMission();
        addControll();
        addEvent();
    }

    @Override
    protected void onDestroy() {
        cancelNotification();
        builder.setOngoing(false);
        super.onDestroy();
    }

    private void addEvent() {
        lv_list_song.setOnItemClickListener(this);
        sb_seekbar_song.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progress = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaManager.seek(progress);
            }
        });

    }

    private void addControll() {
        iv_next = findViewById(R.id.iv_next);
        iv_repeat = findViewById(R.id.iv_repeat);
        iv_prev = findViewById(R.id.iv_prev);
        iv_play = findViewById(R.id.iv_play);
        iv_stop = findViewById(R.id.iv_stop);
        iv_shuffle = findViewById(R.id.iv_shuffle);

        iv_shuffle.setOnClickListener(this);
        iv_stop.setOnClickListener(this);
        iv_next.setOnClickListener(this);
        iv_play.setOnClickListener(this);
        iv_prev.setOnClickListener(this);
        iv_repeat.setOnClickListener(this);

        tv_artist = findViewById(R.id.tv_artist);
        tv_song_name = findViewById(R.id.tv_song_name);
        tv_duration = findViewById(R.id.tv_duration);
        tv_order = findViewById(R.id.tv_order);

        sb_seekbar_song = findViewById(R.id.sb_seekbar_song);

        lv_list_song = findViewById(R.id.lv_list_song);
        listSong = new ArrayList<>();
        mediaManager = new MediaManager(MainActivity.this);
        listSong = mediaManager.getListSong();
        adapter = new SongAdapter(MainActivity.this, R.layout.song_item_layout, listSong);
        lv_list_song.setAdapter(adapter);

        notifyMng = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_repeat:
                doRepeat();
                break;
            case R.id.iv_shuffle:
                doShuffle();
                break;
            case R.id.iv_stop:
                doStop();
                break;
            case R.id.iv_prev:
                doPrev();
                break;
            case R.id.iv_play:
                doPlay();
                break;
            case R.id.iv_next:
                doNext();
                break;
            default:
                break;
        }
    }

    private void doNext() {
        if (mediaManager.next()) {
            iv_play.setImageLevel(LEVEL_PAUSE);
            updateSong();
            createNotification(mediaManager.getcurrSong());
        }
    }

    private void doPrev() {
        if (mediaManager.back()) {
            iv_play.setImageLevel(LEVEL_PAUSE);
            updateSong();
            createNotification(mediaManager.getcurrSong());
        }
    }

    private void doStop() {
        mediaManager.stop();
        cancelNotification();
        iv_play.setImageLevel(LEVEL_PLAY);

    }

    private void doShuffle() {
//        if (levelShuffle == LEVEL_SHUFFLE_OFF) {
//            iv_shuffle.setImageLevel(LEVEL_SHUFFLE_ON);
//            levelShuffle = LEVEL_SHUFFLE_ON;
//        } else {
//            iv_shuffle.setImageLevel(LEVEL_SHUFFLE_OFF);
//            levelShuffle = LEVEL_SHUFFLE_OFF;
//        }
        if (mediaManager.isShuffle()) {
            mediaManager.setShuffle(false);
            iv_shuffle.setImageLevel(LEVEL_SHUFFLE_OFF);
            Toast.makeText(MainActivity.this, "Shuffle Off", Toast.LENGTH_SHORT).show();
        } else {
            mediaManager.setShuffle(true);
            iv_shuffle.setImageLevel(LEVEL_SHUFFLE_ON);
            Toast.makeText(MainActivity.this, "Shuffle On", Toast.LENGTH_SHORT).show();
        }
    }

    private void doRepeat() {
//        if (levelRepeat == LEVEL_REPEAT_OFF) {
//            iv_repeat.setImageLevel(LEVEL_REPEAT_ALL);
//            levelRepeat = LEVEL_REPEAT_ALL;
//        } else if (levelRepeat == LEVEL_REPEAT_ALL) {
//            iv_repeat.setImageLevel(LEVEL_REPEAT_ONE);
//            levelRepeat = LEVEL_REPEAT_ONE;
//        } else {
//            iv_repeat.setImageLevel(LEVEL_REPEAT_OFF);
//            levelRepeat = LEVEL_REPEAT_OFF;
//        }
        if (mediaManager.getRepeatMode() == MediaManager.REPEAT_OFF) {
            setRepeat(MediaManager.REPEAT_ONE);
            Toast.makeText(this, "Repeat One", Toast.LENGTH_SHORT).show();
        } else if (mediaManager.getRepeatMode() == MediaManager.REPEAT_ONE) {
            setRepeat(MediaManager.REPEAT_ALL);
            Toast.makeText(this, "Repeat All", Toast.LENGTH_SHORT).show();
        } else {
            setRepeat(MediaManager.REPEAT_OFF);
            Toast.makeText(this, "Repeat Off", Toast.LENGTH_SHORT).show();
        }
    }

    private void setRepeat(int repeatMode) {
        mediaManager.setRepeatMode(repeatMode);
        iv_repeat.setImageLevel(repeatMode);
    }

    private void doPlay() {

        if (mediaManager.play()) {
            iv_play.setImageLevel(LEVEL_PAUSE);
            updateSong();
            createNotification(mediaManager.getcurrSong());
        } else {
            iv_play.setImageLevel(LEVEL_PLAY);
        }
    }

    private void checkAndRequestPerMission() {
        String[] permissions = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE
        };
        List<String> listPermiisonNeeded = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                listPermiisonNeeded.add(permission);
            }
        }
        if (!listPermiisonNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermiisonNeeded.toArray(new String[listPermiisonNeeded.size()]), 1);
        }
    }

    public void updateSong() {
        Song song = mediaManager.getcurrSong();
        setInfoSong(song);
        new updateSeekbar().execute();
    }

    private void setInfoSong(Song s) {
        tv_song_name.setText(s.getName());
        tv_artist.setText(s.getArtist());
        tv_order.setText(mediaManager.getCurrIndex() + 1 + "/" + mediaManager.getListSong().size());

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        mediaManager.play(i);
        updateSong();
        createNotification(mediaManager.getcurrSong());
        iv_play.setImageLevel(LEVEL_PAUSE);
        levelPlay = LEVEL_PAUSE;
    }

    private Intent getNotificationIntent() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return intent;
    }

    public void createNotification(Song song) {
        Intent prevIntent = getNotificationIntent();
        prevIntent.setAction(PREV_ACTION);

        Intent playIntent = getNotificationIntent();
        playIntent.setAction(PLAY_ACTION);

        Intent nextIntent = getNotificationIntent();
        nextIntent.setAction(NEXT_ACTION);

        Intent resultIntent = new Intent(this,MainActivity.class);


        builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_music)
                .setContentTitle(song.getName())
                .setContentText(song.getArtist())
                .setOngoing(true)
                .addAction(R.drawable.ic_prev,
                        "",
                        PendingIntent.getActivities(this, 0, new Intent[]{prevIntent}, PendingIntent.FLAG_UPDATE_CURRENT))
                .addAction(R.drawable.ic_play,
                        "",
                        PendingIntent.getActivities(this, 0, new Intent[]{playIntent}, PendingIntent.FLAG_UPDATE_CURRENT))
                .addAction(R.drawable.ic_next,
                        "",
                        PendingIntent.getActivities(this, 0, new Intent[]{nextIntent}, PendingIntent.FLAG_UPDATE_CURRENT));
        notifyMng.notify(NOTIFICATION_ID, builder.build());

    }

    private void cancelNotification() {
        notifyMng.cancel(NOTIFICATION_ID);
    }

    private class updateSeekbar extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            while (mediaManager.isPlaying())
                try {
                    Thread.sleep(1000);
                    publishProgress();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            sb_seekbar_song.setMax(mediaManager.getcurrSong().getDuration());
            tv_duration.setText(mediaManager.getTimeText());
            sb_seekbar_song.setProgress(mediaManager.getCurrTime());
            super.onProgressUpdate(values);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        processIntentAction(intent);
    }

    private void processIntentAction(Intent intent) {
        if (intent.getAction() != null) {
            switch (intent.getAction()) {
                case PREV_ACTION:
                    doPrev();
                    break;
                case PLAY_ACTION:
                    doPlay();
                    break;
                case NEXT_ACTION:
                    doNext();
                    break;
            }
        }
    }

}
