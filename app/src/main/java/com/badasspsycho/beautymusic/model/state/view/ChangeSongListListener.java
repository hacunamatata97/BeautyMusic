package com.badasspsycho.beautymusic.model.state.view;

import com.badasspsycho.beautymusic.model.entities.Song;
import java.util.ArrayList;

public interface ChangeSongListListener {
    void changeSongList(ArrayList<Song> songList, int songIndex);
}
