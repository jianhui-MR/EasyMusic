package com.bobby.musiczone.Activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.bobby.musiczone.R;

import butterknife.BindView;

/**
 * 推荐歌单Activity
 */
public class RecommendSongListActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    public Toolbar toolbar;
    @BindView(R.id.recyclerView)
    public RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_song_list);
        setSupportActionBar(toolbar);
    }

}
