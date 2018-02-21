package com.badasspsycho.beautymusic.ui.activities;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.badasspsycho.beautymusic.R;
import com.badasspsycho.beautymusic.controllers.BroadcastHandler;
import com.badasspsycho.beautymusic.controllers.listeners.PlayerUpdater;
import com.badasspsycho.beautymusic.controllers.manager.NotiManager;
import com.badasspsycho.beautymusic.controllers.manager.PlayerManager;
import com.badasspsycho.beautymusic.controllers.manager.SongManager;
import com.badasspsycho.beautymusic.model.entities.Album;
import com.badasspsycho.beautymusic.model.entities.Artist;
import com.badasspsycho.beautymusic.model.entities.Song;
import com.badasspsycho.beautymusic.model.state.player.NormalState;
import com.badasspsycho.beautymusic.model.state.player.RepeatingState;
import com.badasspsycho.beautymusic.model.state.player.ShuffleRepeatState;
import com.badasspsycho.beautymusic.model.state.player.ShufflingState;
import com.badasspsycho.beautymusic.model.state.player.State;
import com.badasspsycho.beautymusic.model.state.view.ChangeFragmentListener;
import com.badasspsycho.beautymusic.model.state.view.ChangeSongListListener;
import com.badasspsycho.beautymusic.model.state.view.ShowAlbumDetailListener;
import com.badasspsycho.beautymusic.model.state.view.ShowArtistDetailListener;
import com.badasspsycho.beautymusic.ui.adapters.ObjectAdapter;
import com.badasspsycho.beautymusic.ui.adapters.SongAdapter;
import com.badasspsycho.beautymusic.ui.custom.RecyclerItemClickListener;
import com.badasspsycho.beautymusic.ui.fragments.AlbumFragment;
import com.badasspsycho.beautymusic.ui.fragments.ArtistFragment;
import com.badasspsycho.beautymusic.ui.fragments.SongFragment;
import com.badasspsycho.beautymusic.utils.Constants;
import com.badasspsycho.beautymusic.utils.MusicLoader;
import com.google.gson.Gson;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.triggertrap.seekarc.SeekArc;
import java.util.ArrayList;
import java.util.Collections;
import org.json.JSONArray;
import org.json.JSONException;

public class MainActivity extends AppCompatActivity
        implements SlidingUpPanelLayout.PanelSlideListener, ChangeSongListListener, PlayerUpdater,
        ShowAlbumDetailListener, ShowArtistDetailListener, ChangeFragmentListener {

    public static PlayerManager playerManager;

    private SlidingUpPanelLayout mSlidingUpPanelLayout;
    private CoordinatorLayout layoutMainContent;
    private CoordinatorLayout layoutDetail;
    private CoordinatorLayout layoutReorder;

    private SongAdapter mSongAdapter;
    private SharedPreferences preferences;
    private ArrayList<Song> originSongList;

    // Main view
    private MenuItem miChangeView;
    private TabLayout tabLayout;
    private SearchView mSearchMain;

    private FragmentTransaction fragmentTransaction;
    private SongManager mSongManager;
    private SongFragment songFragment;
    private ArtistFragment artistFragment;
    private AlbumFragment albumFragment;

    private ObjectAdapter mainSearchAdapter;

    // Detail view
    private CollapsingToolbarLayout collapsingToolbar;
    private Toolbar tbListDetail;
    private SearchView svDetailList;
    private ImageView ivCoverDetail;

    private SongAdapter mSongListDetailAdapter;

    // Player view
    private TextView tvPlayBarPlayingTitle;
    private TextView tvPlayBarPlayingArtist;
    private ImageView ivPlayBarPlayButton;
    private ProgressBar pbPlayBar;

    private LinearLayout layoutPlayerNavigator;
    private LinearLayout layoutPlayerController;
    private TextView tvPlayerPlayingTitle;
    private FloatingActionButton fabPlayerPlayButton;
    private ImageView ivShuffle;
    private ImageView ivRepeatOne;
    private TextView tvProgressTime;
    private SeekArc seekBarPlaying;

    private SongAdapter mSongSearchPlayListAdapter;

    // Reorder view
    private SearchView svSearchPlayingList;

    // Service
    ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            PlayerManager.SongBinder songBinder = (PlayerManager.SongBinder) iBinder;
            playerManager = songBinder.getPlayerService();
            playerManager.register(MainActivity.this);
            NotiManager manager = new NotiManager(MainActivity.this);
            playerManager.register(manager);
            ArrayList<Song> songList = getLastSongList();
            int lastIndex = getLastSongIndex();
            if (songList != null && songList.size() > 0) {
                preferences = getSharedPreferences(Constants.KEY_SHARED_FILE, MODE_PRIVATE);
                manager.setSong(songList.get(lastIndex));
                playerManager.setUpLastState(songList, lastIndex,
                        preferences.getInt(Constants.KEY_PLAY_MANAGER_PROGRESS, 0));
            }
            int lastPlayerManagerState = getLastPlayerManagerState();
            if (lastPlayerManagerState == Constants.PLAY_MANAGER_REPEAT) {
                playerManager.repeatOneHandle();
            } else if (lastPlayerManagerState == Constants.PLAY_MANAGER_SHUFFLE) {
                playerManager.shuffleHandle();
            } else if (lastPlayerManagerState == Constants.PLAY_MANAGER_SHUFFLE_REPEAT) {
                playerManager.repeatOneHandle();
                playerManager.shuffleHandle();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(MainActivity.this, PlayerManager.class);
        bindService(intent, mServiceConnection, BIND_AUTO_CREATE);

        /*Init*/
        mSongManager = new SongManager();
        songFragment = new SongFragment();
        artistFragment = new ArtistFragment();
        albumFragment = new AlbumFragment();

        // Main view
        mSlidingUpPanelLayout = findViewById(R.id.sliding_up_panel);
        mSlidingUpPanelLayout.addPanelSlideListener(this);
        layoutMainContent = findViewById(R.id.cool_main_content);
        layoutDetail = findViewById(R.id.cool_list_song_detail);
        final Toolbar toolbar = findViewById(R.id.main_content_toolbar);
        setSupportActionBar(toolbar);
        tabLayout = findViewById(R.id.main_content_tabs);
        RecyclerView rvListSearching = findViewById(R.id.rv_main_content_searching_suggestion);

        // Player view
        // Play bar
        tvPlayBarPlayingTitle = findViewById(R.id.tv_play_bar_title);
        tvPlayBarPlayingArtist = findViewById(R.id.tv_play_bar_artist);
        ivPlayBarPlayButton = findViewById(R.id.iv_play_bar_play_pause);
        pbPlayBar = findViewById(R.id.pb_play_bar);

        // Main player
        layoutPlayerNavigator = findViewById(R.id.layout_player_navigator);
        layoutPlayerController = findViewById(R.id.layout_play_bar);
        RelativeLayout layoutPlayerNavigatorBack = findViewById(R.id.layout_player_navigator_back);
        RelativeLayout layoutPlayerNavigatorReorder =
                findViewById(R.id.layout_player_navigator_reorder);
        RecyclerView rvRecentSongList = findViewById(R.id.rv_player_recent_list);
        LinearLayout layoutPlayer = findViewById(R.id.layout_player);
        ImageView ivNextButton = findViewById(R.id.iv_player_next);
        ImageView ivPrevButton = findViewById(R.id.iv_player_prev);
        tvPlayerPlayingTitle = findViewById(R.id.tv_player_title);
        fabPlayerPlayButton = findViewById(R.id.fab_player_play_pause);
        ivShuffle = findViewById(R.id.iv_player_shuffle);
        ivRepeatOne = findViewById(R.id.iv_player_repeat);
        seekBarPlaying = findViewById(R.id.seekBarPlaying);
        tvProgressTime = findViewById(R.id.tv_player_progress_time);

        svSearchPlayingList = findViewById(R.id.sv_playing_list_suggestion);
        ((EditText) svSearchPlayingList.findViewById(
                android.support.v7.appcompat.R.id.search_src_text)).setTextColor(
                getResources().getColor(R.color.white));
        RecyclerView rvPlayerRecentSearchSuggestion =
                findViewById(R.id.rv_player_recent_search_suggestion);

        // Detail view
        collapsingToolbar = findViewById(R.id.detail_collapsing_toolbar);
        tbListDetail = findViewById(R.id.detail_toolbar);
        ivCoverDetail = findViewById(R.id.iv_detail_cover);
        RecyclerView rvListDetail = findViewById(R.id.rv_detail_song_list);

        // Reorder view
        RelativeLayout layoutReorderNavigator = findViewById(R.id.layout_reorder_navigator);
        RecyclerView rvReorderList = findViewById(R.id.rv_reorder_playing_list);
        layoutReorder = findViewById(R.id.cool_reorder_playing_list);
        /*End-Init*/

        /*Setup*/

        preferences = getSharedPreferences(Constants.KEY_SHARED_FILE, MODE_PRIVATE);
        originSongList = new ArrayList<>();

        // Main view
        mSongManager.register(songFragment);
        mSongManager.register(artistFragment);
        mSongManager.register(albumFragment);

        songFragment.setChangeSongListListener(this);
        albumFragment.setShowAlbumDetailListener(this);
        albumFragment.setChangeFragmentListener(this);
        artistFragment.setShowArtistDetailListener(this);
        artistFragment.setChangeFragmentListener(this);
        albumFragment.setGrid(preferences.getBoolean(Constants.KEY_FRAGMENT_ALBUM_STATUS, true));
        artistFragment.setGrid(preferences.getBoolean(Constants.KEY_FRAGMENT_ARTIST_STATUS, true));

        mSongAdapter = new SongAdapter(new ArrayList<>());
        mainSearchAdapter = new ObjectAdapter(new ArrayList<>());

        rvListSearching.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvListSearching.setAdapter(mainSearchAdapter);
        rvListSearching.addOnItemTouchListener(new RecyclerItemClickListener(this, rvListSearching,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Object object = mainSearchAdapter.getObjectList().get(position);
                        if (object instanceof Song) {
                            Song song = (Song) object;
                            ArrayList<Song> songs = new ArrayList<>();
                            songs.add(song);
                            changeSongList(songs, 0);
                        } else if (object instanceof Artist) {
                            Artist artist = (Artist) object;
                            displayArtistDetail(artist);
                        } else if (object instanceof Album) {
                            Album album = (Album) object;
                            displayAlbumDetail(album);
                        }
                        toolbar.collapseActionView();
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {
                    }
                }));

        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.layout_main_container, songFragment);
        fragmentTransaction.commit();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                if (tab.getPosition() == 0) {
                    fragmentTransaction.replace(R.id.layout_main_container, songFragment);
                    fragmentTransaction.commit();
                    if (miChangeView != null) {
                        miChangeView.setIcon(R.drawable.ic_view_list);
                    }
                } else if (tab.getPosition() == 1) {
                    fragmentTransaction.replace(R.id.layout_main_container, albumFragment);
                    fragmentTransaction.commit();
                    setAlbumActionIcon();
                } else if (tab.getPosition() == 2) {
                    fragmentTransaction.replace(R.id.layout_main_container, artistFragment);
                    fragmentTransaction.commit();
                    setArtistActionIcon();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        if (isPermissionGranted()) {
            fillData();
        } else {
            requestPermission();
        }

        // Player view
        mSongSearchPlayListAdapter = new SongAdapter(new ArrayList<>());
        rvPlayerRecentSearchSuggestion.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvPlayerRecentSearchSuggestion.setAdapter(mSongSearchPlayListAdapter);
        rvPlayerRecentSearchSuggestion.addOnItemTouchListener(
                new RecyclerItemClickListener(this, rvPlayerRecentSearchSuggestion,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Song s = mSongSearchPlayListAdapter.getSongList().get(position);
                                for (int i = 0; i < mSongAdapter.getSongList().size(); i++) {
                                    if (s.equals(mSongAdapter.getSongList().get(i))) {
                                        playerManager.updateSong(i);
                                        svSearchPlayingList.setIconified(true);
                                        svSearchPlayingList.onActionViewCollapsed();
                                        return;
                                    }
                                }
                            }

                            @Override
                            public void onItemLongClick(View view, int position) {
                            }
                        }));

        svSearchPlayingList.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    mSongSearchPlayListAdapter.setSongList(new ArrayList<>());
                    return false;
                }
                mSongSearchPlayListAdapter.setSongList(
                        MusicLoader.searchSong(mSongAdapter.getSongList(), newText));
                return false;
            }
        });

        ArrayList<Song> songList = getLastSongList();
        final int lastIndex = getLastSongIndex();
        if (songList != null && songList.size() > 0) {
            mSongAdapter.setSongList(songList);
            mSongAdapter.setCurrentSongIndex(lastIndex);
            mSongAdapter.notifyDataSetChanged();

            Song song = songList.get(lastIndex);
            tvPlayBarPlayingTitle.setText(song.getTitle().trim());
            tvPlayBarPlayingArtist.setText(song.getArtist().trim());
            tvPlayerPlayingTitle.setText(song.getTitle());
        }

        layoutPlayer.setOnClickListener(view -> {
        });

        rvRecentSongList.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvRecentSongList.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rvRecentSongList.setAdapter(mSongAdapter);
        rvRecentSongList.addOnItemTouchListener(
                new RecyclerItemClickListener(this, rvRecentSongList,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                playerManager.updateSong(position);
                            }

                            @Override
                            public void onItemLongClick(View view, int position) {
                            }
                        }));

        layoutPlayerNavigatorReorder.setOnClickListener(view -> displaySortingList());
        rvReorderList.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvReorderList.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rvReorderList.setAdapter(mSongAdapter);

        ItemTouchHelper.Callback callback = new ItemTouchHelper.Callback() {
            int currentIndex;
            Song currentSong;

            @Override
            public int getMovementFlags(RecyclerView recyclerView,
                    RecyclerView.ViewHolder viewHolder) {
                currentIndex = mSongAdapter.getCurrentSongIndex();
                currentSong = mSongAdapter.getSongList().get(currentIndex);
                return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG, ItemTouchHelper.DOWN
                        | ItemTouchHelper.UP
                        | ItemTouchHelper.START
                        | ItemTouchHelper.END);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                    RecyclerView.ViewHolder target) {
                Collections.swap(mSongAdapter.getSongList(), viewHolder.getAdapterPosition(),
                        target.getAdapterPosition());
                mSongAdapter.notifyItemMoved(viewHolder.getAdapterPosition(),
                        target.getAdapterPosition());

                for (int i = 0; i < mSongAdapter.getSongList().size(); i++) {
                    if (currentSong.equals(mSongAdapter.getSongList().get(i))) {
                        playerManager.setCurrentSongIndex(i);
                        playerManager.setSongList(mSongAdapter.getSongList());
                        mSongAdapter.setCurrentSongIndex(i);
                        break;
                    }
                }
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

            }
        };

        ItemTouchHelper ith = new ItemTouchHelper(callback);
        ith.attachToRecyclerView(rvReorderList);

        layoutPlayerNavigatorBack.setOnClickListener(view -> displayMain());

        ivNextButton.setOnClickListener(view -> {
            if (playerManager != null) {
                playerManager.next();
            }
        });

        ivPrevButton.setOnClickListener(view -> {
            if (playerManager != null) {
                playerManager.prev();
            }
        });

        ivPlayBarPlayButton.setOnClickListener(view -> {
            if (playerManager != null) {
                playerManager.playingStateHandle();
            }
        });

        fabPlayerPlayButton.setOnClickListener(view -> {
            if (playerManager != null) {
                playerManager.playingStateHandle();
            }
        });

        ivRepeatOne.setOnClickListener(view -> {
            if (playerManager != null) {
                playerManager.repeatOneHandle();
            }
        });

        ivShuffle.setOnClickListener(view -> {
            if (playerManager != null) {
                playerManager.shuffleHandle();
            }
        });

        layoutReorderNavigator.setOnClickListener(view -> {
            displayMain();
            mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        });

        seekBarPlaying.setOnSeekArcChangeListener(new SeekArc.OnSeekArcChangeListener() {
            @Override
            public void onProgressChanged(SeekArc seekArc, int i, boolean b) {
                if (b) {
                    playerManager.seek(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekArc seekArc) {

            }

            @Override
            public void onStopTrackingTouch(SeekArc seekArc) {

            }
        });

        // Detail view
        tbListDetail.inflateMenu(R.menu.menu_detail);
        tbListDetail.setNavigationIcon(R.drawable.ic_arrow_back);
        tbListDetail.setNavigationOnClickListener(view -> displayMain());
        mSongListDetailAdapter = new SongAdapter(new ArrayList<>());
        rvListDetail.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvListDetail.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rvListDetail.setAdapter(mSongListDetailAdapter);
        rvListDetail.addOnItemTouchListener(new RecyclerItemClickListener(this, rvListDetail,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Song s = mSongListDetailAdapter.getSongList().get(position);
                        for (int i = 0; i < originSongList.size(); i++) {
                            if (s.equals(originSongList.get(i))) {
                                changeSongList(originSongList, i);
                                return;
                            }
                        }
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {

                    }
                }));

        svDetailList = (SearchView) tbListDetail.getMenu()
                .findItem(R.id.action_search_detail)
                .getActionView();
        svDetailList.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mSongListDetailAdapter.setSongList(MusicLoader.searchSong(originSongList, newText));
                return false;
            }
        });

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        BroadcastHandler handler = new BroadcastHandler();
        registerReceiver(handler, intentFilter);
        AudioManager manager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (manager != null) {
            manager.registerMediaButtonEventReceiver(
                    new ComponentName(this, BroadcastHandler.class));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        miChangeView = menu.findItem(R.id.action_change_view);
        MenuItem miSearch = menu.findItem(R.id.action_search);
        mSearchMain = (SearchView) miSearch.getActionView();
        mSearchMain.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    mainSearchAdapter.setObjectList(new ArrayList<>());
                    return false;
                }

                ArrayList<Object> listObj = MusicLoader.searchAll(mSongManager.getSongList(),
                        mSongManager.getAlbumList(), mSongManager.getArtistList(), newText);
                if (!mSearchMain.isIconified()) mainSearchAdapter.setObjectList(listObj);
                return false;
            }
        });
        mSearchMain.setOnCloseListener(() -> {
            mainSearchAdapter.setObjectList(new ArrayList<>());
            return false;
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify component_play_music_screen parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_change_view) {
            switch (tabLayout.getSelectedTabPosition()) {
                case 1:
                    albumFragment.setGrid(!albumFragment.isGrid());
                    break;
                case 2:
                    artistFragment.setGrid(!artistFragment.isGrid());
                    break;
            }
            //            if (tabLayout.getSelectedTabPosition() == 1) {
            //                albumFragment.setGrid(!albumFragment.isGrid());
            //            } else if (tabLayout.getSelectedTabPosition() == 2) {
            //                artistFragment.setGrid(!artistFragment.isGrid());
            //            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * check read storage permission
     *
     * @return bool
     */
    public boolean isPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * request permission from user
     */
    public void requestPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[] {
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE
        }, Constants.KEY_REQUEST_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        if (isPermissionGranted()) {
            fillData();
        }
    }

    /**
     * get data
     */
    public void fillData() {
        try {
            MusicLoader MusicLoader = new MusicLoader(this);
            mSongManager.setSongList(MusicLoader.getSongs());
            mSongManager.setAlbumList(MusicLoader.getAlbums(mSongManager.getSongList()));
            mSongManager.setArtistList(MusicLoader.getArtists(mSongManager.getSongList()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * fade music controller and music navigator depend on slide offset
     */
    @Override
    public void onPanelSlide(View panel, float slideOffset) {
        layoutPlayerController.setAlpha(1 - slideOffset);
        layoutPlayerNavigator.setAlpha(slideOffset);
    }

    /**
     * display or hide music navigator and music controller depend on slide state
     */
    @Override
    public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState,
            SlidingUpPanelLayout.PanelState newState) {
        if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
            layoutPlayerNavigator.setVisibility(View.INVISIBLE);
            layoutPlayerController.setVisibility(View.VISIBLE);
        } else if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
            layoutPlayerController.setVisibility(View.INVISIBLE);
            layoutPlayerNavigator.setVisibility(View.VISIBLE);
        } else {
            layoutPlayerNavigator.setVisibility(View.VISIBLE);
            layoutPlayerController.setVisibility(View.VISIBLE);
        }
    }

    /**
     * update song for music controller and play manager
     */
    @Override
    public void changeSongList(ArrayList<Song> listSong, int index) {
        mSongAdapter.setSongList(listSong);
        playerManager.updateSongList(listSong, index);
    }

    /**
     * play manager notify about song changed
     */
    @Override
    public void updateSong(Song song, int index) {
        tvPlayBarPlayingTitle.setText(song.getTitle().trim());
        tvPlayBarPlayingArtist.setText(song.getArtist().trim());
        tvPlayerPlayingTitle.setText(song.getTitle());
        mSongAdapter.setCurrentSongIndex(index);
        mSongAdapter.notifyDataSetChanged();
        try {
            saveToSharedPref();
        } catch (Exception ignored) {
        }
    }

    /**
     * observable notify about playing or not playing state
     *
     * @param isPlaying playing state
     */
    @Override
    public void updatePlayingState(boolean isPlaying) {
        if (isPlaying) {
            ivPlayBarPlayButton.setBackground(
                    getResources().getDrawable(R.drawable.button_pause_white));
            fabPlayerPlayButton.setImageDrawable(
                    getResources().getDrawable(R.drawable.button_pause_white));
        } else {
            ivPlayBarPlayButton.setBackground(
                    getResources().getDrawable(R.drawable.button_play_white));
            fabPlayerPlayButton.setImageDrawable(
                    getResources().getDrawable(R.drawable.button_play_white));
        }
    }

    /**
     * observable notify about shuffle and repeat function changed
     *
     * @param state shuffle of repeat state
     */
    @Override
    public void updatePlayManagerState(State state) {
        if (state instanceof NormalState) {
            ivShuffle.setBackground(getResources().getDrawable(R.drawable.ic_shuffle_disable));
            ivRepeatOne.setBackground(getResources().getDrawable(R.drawable.ic_repeat_disable));
            return;
        }
        if (state instanceof ShufflingState) {
            ivShuffle.setBackground(getResources().getDrawable(R.drawable.ic_shuffle_enable));
            ivRepeatOne.setBackground(getResources().getDrawable(R.drawable.ic_repeat_disable));
            return;
        }
        if (state instanceof RepeatingState) {
            ivShuffle.setBackground(getResources().getDrawable(R.drawable.ic_shuffle_disable));
            ivRepeatOne.setBackground(getResources().getDrawable(R.drawable.ic_repeat_enable));
            return;
        }
        if (state instanceof ShuffleRepeatState) {
            ivShuffle.setBackground(getResources().getDrawable(R.drawable.ic_shuffle_enable));
            ivRepeatOne.setBackground(getResources().getDrawable(R.drawable.ic_repeat_enable));
        }
    }

    /**
     * observable notify about song progress changed
     *
     * @param progress current song progress
     */
    @Override
    public void updatePlayingProgress(int progress) {
        final int second = progress % 60;
        final int minute = progress / 60;
        final int hour = progress / 3600;
        final String sec = second / 10 != 0 ? second + "" : "0" + second;
        final String min = minute / 10 != 0 ? minute + "" : "0" + minute;
        final String hou = hour / 10 != 0 ? hour + "" : "0" + hour;
        runOnUiThread(() -> {
            if (hour == 0) {
                tvProgressTime.setText(String.format("%s:%s", min, sec));
            } else {
                tvProgressTime.setText(String.format("%s:%s:%s", hou, min, sec));
            }
        });
    }

    /**
     * observable notify about song progress changed
     *
     * @param progress current song progress
     * @param duration current song duration
     */
    @Override
    public void updateForProgressBar(final int progress, final int duration) {
        runOnUiThread(() -> {
            pbPlayBar.setMax(duration);
            pbPlayBar.setProgress(progress);
            seekBarPlaying.setProgress(progress * 100 / duration);
            saveProgress(progress);
        });
    }

    /**
     * observable notify about display list song of an artist
     *
     * @param artist selected artist
     */
    @Override
    public void displayArtistDetail(Artist artist) {
        displayListSongDetail(artist.getArtistName());
        if (artist.getArtistArt() != null) {
            ivCoverDetail.setImageBitmap(artist.getArtistArt());
        } else {
            ivCoverDetail.setImageDrawable(getResources().getDrawable(R.drawable.ic_headset_icon));
        }
        mSongListDetailAdapter.setSongList(artist.getSongList());
        originSongList = artist.getSongList();
    }

    /**
     * observable notify about display list song of an album
     *
     * @param album selected album
     */
    @Override
    public void displayAlbumDetail(Album album) {
        displayListSongDetail(album.getAlbumName());
        if (album.getAlbumArt() != null) {
            ivCoverDetail.setImageBitmap(album.getAlbumArt());
        } else {
            ivCoverDetail.setImageDrawable(
                    getResources().getDrawable(R.drawable.ic_album_white_24dp));
        }
        mSongListDetailAdapter.setSongList(album.getSongList());
        originSongList = album.getSongList();
    }

    /**
     * display song manager screen
     */
    public void displayMain() {
        layoutMainContent.setVisibility(View.VISIBLE);
        layoutDetail.setVisibility(View.INVISIBLE);
        layoutReorder.setVisibility(View.INVISIBLE);
        mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        mSongAdapter.setOrdering(false);
    }

    /**
     * display list song and description of album or artist
     *
     * @param description album name or artist name
     */
    public void displayListSongDetail(String description) {
        collapsingToolbar.setTitle(description);
        layoutMainContent.setVisibility(View.INVISIBLE);
        layoutDetail.setVisibility(View.VISIBLE);
        layoutReorder.setVisibility(View.INVISIBLE);
        mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        mSongAdapter.setOrdering(false);
        svDetailList.setQuery("", false);
        svDetailList.setIconified(true);
        tbListDetail.collapseActionView();
    }

    /**
     * display sorting playing list screen
     */
    public void displaySortingList() {
        layoutMainContent.setVisibility(View.INVISIBLE);
        layoutDetail.setVisibility(View.INVISIBLE);
        layoutReorder.setVisibility(View.VISIBLE);
        mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        mSongAdapter.setOrdering(true);
    }

    /**
     * correspond to switch between grid view and list view of fragment
     *
     * @param fragment album fragment or artist fragment
     */
    @Override
    public void changeFragment(Fragment fragment) {
        if (tabLayout.getSelectedTabPosition() == 0) {
            return;
        }
        if (fragment instanceof AlbumFragment) {
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.detach(albumFragment);
            fragmentTransaction.attach(albumFragment);
            fragmentTransaction.commit();
            setAlbumActionIcon();
        } else if (fragment instanceof ArtistFragment) {
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.detach(artistFragment);
            fragmentTransaction.attach(artistFragment);
            fragmentTransaction.commit();
            setArtistActionIcon();
        }
    }

    public void setAlbumActionIcon() {
        if (albumFragment.isGrid()) {
            miChangeView.setIcon(R.drawable.ic_view_list);
        } else {
            miChangeView.setIcon(R.drawable.ic_view_grid);
        }
    }

    public void setArtistActionIcon() {
        if (artistFragment.isGrid()) {
            miChangeView.setIcon(R.drawable.ic_view_list);
        } else {
            miChangeView.setIcon(R.drawable.ic_view_grid);
        }
    }

    /**
     * do not allow android system destroy activity when press back button
     */
    @Override
    public void onBackPressed() {
        if (layoutReorder.getVisibility() == View.VISIBLE
                || layoutDetail.getVisibility() == View.VISIBLE
                || mSlidingUpPanelLayout.getPanelState()
                == SlidingUpPanelLayout.PanelState.EXPANDED) {
            displayMain();
            return;
        }
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        saveToSharedPref();
        super.onSaveInstanceState(outState);
    }

    /**
     * save playing progress to shared preference
     *
     * @param progress current song progress
     */
    public void saveProgress(int progress) {
        preferences.edit().putInt(Constants.KEY_PLAY_MANAGER_PROGRESS, progress).apply();
    }

    /**
     * save playing list song and song to shared preference
     */
    public void saveToSharedPref() {
        SharedPreferences sharedPreferences =
                getSharedPreferences(Constants.KEY_SHARED_FILE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String jsonArr = new Gson().toJson(playerManager.getSongList());
        editor.putString(Constants.KEY_LAST_LIST_SONG, jsonArr);
        editor.putInt(Constants.KEY_LAST_INDEX, playerManager.getCurrentSongIndex());
        editor.putBoolean(Constants.KEY_FRAGMENT_ALBUM_STATUS, albumFragment.isGrid());
        editor.putBoolean(Constants.KEY_FRAGMENT_ARTIST_STATUS, artistFragment.isGrid());
        int playState = 0;
        if (playerManager.getState() instanceof ShufflingState) {
            playState = Constants.PLAY_MANAGER_SHUFFLE;
        } else if (playerManager.getState() instanceof RepeatingState) {
            playState = Constants.PLAY_MANAGER_REPEAT;
        } else if (playerManager.getState() instanceof ShuffleRepeatState) {
            playState = Constants.PLAY_MANAGER_SHUFFLE_REPEAT;
        }
        editor.putInt(Constants.KEY_PLAY_MANAGER_STATE, playState);
        editor.apply();
    }

    /**
     * get last playing state
     *
     * @return (shuffle, repeat...)
     */
    public int getLastPlayerManagerState() {
        SharedPreferences preferences =
                getSharedPreferences(Constants.KEY_SHARED_FILE, MODE_PRIVATE);
        return preferences.getInt(Constants.KEY_PLAY_MANAGER_STATE, 0);
    }

    /**
     * get last playing list song
     *
     * @return list song
     */
    public ArrayList<Song> getLastSongList() {
        SharedPreferences sharedPreferences =
                getSharedPreferences(Constants.KEY_SHARED_FILE, MODE_PRIVATE);
        String jsonArr = sharedPreferences.getString(Constants.KEY_LAST_LIST_SONG, null);
        if (jsonArr == null) {
            return null;
        }
        ArrayList<Song> songList = new ArrayList<>();
        Gson gson = new Gson();
        try {
            JSONArray jsonArray = new JSONArray(jsonArr);
            for (int i = 0; i < jsonArray.length(); i++) {
                Song s = gson.fromJson(jsonArray.getString(i), Song.class);
                songList.add(s);
            }
            return songList;
        } catch (JSONException e) {
            return null;
        }
    }

    /**
     * get last index of playing song
     *
     * @return last song index before turn of the app
     */
    public int getLastSongIndex() {
        SharedPreferences sharedPreferences =
                getSharedPreferences(Constants.KEY_SHARED_FILE, MODE_PRIVATE);
        return sharedPreferences.getInt(Constants.KEY_LAST_INDEX, 0);
    }
}
