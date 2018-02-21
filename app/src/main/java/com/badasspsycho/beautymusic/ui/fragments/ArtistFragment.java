package com.badasspsycho.beautymusic.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
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
import com.badasspsycho.beautymusic.model.state.view.ChangeFragmentListener;
import com.badasspsycho.beautymusic.model.state.view.ShowArtistDetailListener;
import com.badasspsycho.beautymusic.ui.adapters.ArtistAdapter;
import com.badasspsycho.beautymusic.ui.custom.GridItemDecoration;
import com.badasspsycho.beautymusic.ui.custom.RecyclerItemClickListener;
import com.badasspsycho.beautymusic.utils.Constants;
import com.badasspsycho.beautymusic.utils.MusicLoader;
import java.util.ArrayList;

public class ArtistFragment extends Fragment implements MusicUpdater {

    private boolean isGrid = true;

    private ArrayList<Artist> artistList;
    private ArtistAdapter adapter;

    private ShowArtistDetailListener showArtistDetailListener;
    private ChangeFragmentListener changeFragmentListener;

    public ArtistFragment() {
        artistList = new ArrayList<>();
        adapter = new ArtistAdapter(artistList);
    }

    public boolean isGrid() {
        return isGrid;
    }

    public void setGrid(boolean grid) {
        isGrid = grid;
        if (changeFragmentListener != null) changeFragmentListener.changeFragment(this);
    }

    public void setShowArtistDetailListener(ShowArtistDetailListener showArtistDetailListener) {
        this.showArtistDetailListener = showArtistDetailListener;
    }

    public void setChangeFragmentListener(ChangeFragmentListener changeFragmentListener) {
        this.changeFragmentListener = changeFragmentListener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artists, container, false);

        RecyclerView rvAlbumList = view.findViewById(R.id.rc_artist_list);

        // Set adapter
        adapter.setGrid(isGrid);
        rvAlbumList.setAdapter(adapter);

        // Set Orientation and decorations
        if (isGrid) {
            GridLayoutManager gridLayoutManager =
                    new GridLayoutManager(getActivity(), Constants.GRID_COUNT);
            rvAlbumList.setLayoutManager(gridLayoutManager);
            rvAlbumList.addItemDecoration(
                    new GridItemDecoration(Constants.GRID_SIZE, Constants.GRID_COUNT));
        } else {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            rvAlbumList.setLayoutManager(linearLayoutManager);
            rvAlbumList.addItemDecoration(
                    new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        }

        // Set item touch listener
        rvAlbumList.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), rvAlbumList,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (showArtistDetailListener != null) {
                            showArtistDetailListener.displayArtistDetail(artistList.get(position));
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

    }

    @Override
    public void updateListAlbum(ArrayList<Album> albumList) {

    }

    @Override
    public void updateListArtist(ArrayList<Artist> artistList) {
        this.artistList = artistList;
        adapter.setArtistList(this.artistList);
    }
}
