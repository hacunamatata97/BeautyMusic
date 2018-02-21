package com.badasspsycho.beautymusic.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.provider.MediaStore;
import com.badasspsycho.beautymusic.model.entities.Album;
import com.badasspsycho.beautymusic.model.entities.Artist;
import com.badasspsycho.beautymusic.model.entities.Song;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class MusicLoader {

    private Context mContext;

    public MusicLoader(Context context) {
        mContext = context;
    }

    private Cursor getData() {
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        String[] projection = new String[] {
                MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.DATA
        };

        return this.mContext.getContentResolver()
                .query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, null,
                        MediaStore.Audio.Media.TITLE);
    }

    private static String normalizeString(String s) {
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("").replaceAll("đ", "d").toLowerCase().trim();
    }

    public ArrayList<Song> getSongs() {
        ArrayList<Song> songs = new ArrayList<>();
        Cursor songCursor = getData();

        if (songCursor != null && songCursor.moveToFirst()) {
            do {
                long id = songCursor.getLong(0);
                String title = songCursor.getString(1);
                String album = songCursor.getString(2);
                String artist = songCursor.getString(3);
                long duration = songCursor.getLong(4);
                String uri = songCursor.getString(5);

                Song current = new Song(id, title, album, artist, duration, uri);
                songs.add(current);
            } while (songCursor.moveToNext());
        }

        return songs;
    }

    public ArrayList<Album> getAlbums(ArrayList<Song> songs) {
        ArrayList<Album> albums = new ArrayList<>();

        for (Song song : songs) {
            String name = song.getAlbum().trim();

            if (albums.size() == 0) {
                Album album = new Album(name);
                album.getSongList().add(song);
                albums.add(album);
                continue;
            }

            for (int i = 0; i < albums.size(); i++) {
                String albumName = albums.get(i).getAlbumName();

                if (albumName.equals(name)) {
                    albums.get(i).getSongList().add(song);
                    break;
                }

                if (i == albums.size() - 1) {
                    Album album = new Album(name);
                    album.getSongList().add(song);
                    albums.add(album);
                }
            }
        }

        return albums;
    }

    public ArrayList<Artist> getArtists(ArrayList<Song> songs) {
        ArrayList<Artist> artists = new ArrayList<>();

        for (Song song : songs) {
            String name = song.getArtist().trim();

            if (artists.size() == 0) {
                Artist artist = new Artist(name);
                artist.getSongList().add(song);
                artists.add(artist);
                continue;
            }

            for (int i = 0; i < artists.size(); i++) {
                String artistName = artists.get(i).getArtistName();

                if (artistName.equals(name)) {
                    artists.get(i).getSongList().add(song);
                    break;
                }

                if (i == artists.size() - 1) {
                    Artist artist = new Artist(name);
                    artist.getSongList().add(song);
                    artists.add(artist);
                }
            }
        }

        return artists;
    }

    public static Bitmap getArt(Song song) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();

        retriever.setDataSource(song.getUri());
        byte[] data = retriever.getEmbeddedPicture();
        if (data != null) {
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        }

        return bitmap;
    }

    public static ArrayList<Song> searchSong(ArrayList<Song> songs, String keyWord) {
        ArrayList<Song> results = new ArrayList<>();
        keyWord = normalizeString(keyWord);

        for (Song song : songs) {
            if (normalizeString(song.getTitle()).contains(keyWord)) {
                results.add(song);

                if (results.size() == 10) return results;
            }
        }

        return results;
    }

    public static ArrayList<Album> searchAlbum(ArrayList<Album> albums, String keyWord) {
        ArrayList<Album> results = new ArrayList<>();
        keyWord = normalizeString(keyWord);

        for (Album album : albums) {
            if (normalizeString(album.getAlbumName()).contains(keyWord)) {
                results.add(album);

                if (results.size() == 10) return results;
            }
        }
        return results;
    }

    public static ArrayList<Artist> searchArtist(ArrayList<Artist> artists, String keyWord) {
        ArrayList<Artist> results = new ArrayList<>();
        keyWord = normalizeString(keyWord);

        for (Artist artist : artists) {
            if (normalizeString(artist.getArtistName()).contains(keyWord)) {
                results.add(artist);

                if (results.size() == 10) return results;
            }
        }
        return results;
    }

    public static ArrayList<Object> searchAll(ArrayList<Song> songs, ArrayList<Album> albums,
            ArrayList<Artist> artists, String keyWord) {

        ArrayList<Song> songResults = searchSong(songs, keyWord);
        ArrayList<Album> albumResults = searchAlbum(albums, keyWord);
        ArrayList<Artist> artistResults = searchArtist(artists, keyWord);
        ArrayList<Object> allResults = new ArrayList<>();

        for (Song song : songResults) {
            allResults.add(song);
            if (allResults.size() == 10) return allResults;
        }

        for (Album album : albumResults) {
            allResults.add(album);
            if (allResults.size() == 10) return allResults;
        }

        for (Artist artist : artistResults) {
            allResults.add(artist);
            if (allResults.size() == 10) return allResults;
        }

        return allResults;
    }
}
