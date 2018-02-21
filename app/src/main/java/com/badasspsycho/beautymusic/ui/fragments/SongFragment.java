package com.badasspsycho.beautymusic.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.badasspsycho.beautymusic.R;
import com.badasspsycho.beautymusic.controllers.listeners.MusicUpdater;
import com.badasspsycho.beautymusic.model.entities.Album;
import com.badasspsycho.beautymusic.model.entities.Artist;
import com.badasspsycho.beautymusic.model.entities.Song;
import com.badasspsycho.beautymusic.model.state.view.ChangeSongListListener;
import com.badasspsycho.beautymusic.ui.activities.MainActivity;
import com.badasspsycho.beautymusic.ui.adapters.SongAdapter;
import com.badasspsycho.beautymusic.ui.custom.RecyclerItemClickListener;
import com.badasspsycho.beautymusic.utils.MusicLoader;
import java.util.ArrayList;

public class SongFragment extends Fragment implements MusicUpdater {

    private ArrayList<Song> songList;
    private SongAdapter adapter;
    private ChangeSongListListener mChangeSongListListener;

    public SongFragment() {
        songList = new ArrayList<>();
        adapter = new SongAdapter(songList);
    }

    public void setChangeSongListListener(ChangeSongListListener changeSongListListener) {
        mChangeSongListListener = changeSongListListener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_songs, container, false);

        // RecyclerView
        RecyclerView rvSongList = view.findViewById(R.id.rc_song_list);
        rvSongList.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvSongList.setAdapter(adapter);

        rvSongList.addItemDecoration(
                new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        rvSongList.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), rvSongList,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (mChangeSongListListener != null) {
                            mChangeSongListListener.changeSongList(songList, position);
                        }
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {

                    }
                }));

        return view;
    }

    @Override
    public void updateListSong(ArrayList<Song> songList) {
        this.songList = songList;
        adapter.setSongList(this.songList);
    }

    @Override
    public void updateListAlbum(ArrayList<Album> albumList) {

    }

    @Override
    public void updateListArtist(ArrayList<Artist> artistList) {

    }
}
