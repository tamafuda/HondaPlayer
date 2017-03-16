package jp.co.honda.music.model;

import android.database.Cursor;
import android.provider.MediaStore;

/**
 * @Author: Hoang Vu
 * @Date: 2017/02/28
 */

public class Song {
	
	private long id;
	private String title;
	private String artist;
	private String albumName;
	private String thumbUrl;
	private String size;
	private String lyric;
	private String data;
	private String duration;
	private String type;

	//get Album_ID
	private long albumID;

	//implement Artist_ID
	private long artistID;
	public Song(long songID, String songTitle, String songArtist){
		id=songID;
		title=songTitle;
		artist=songArtist;
	}

	public Song(Cursor songCursor) {
		int songId = songCursor.getColumnIndex(MediaStore.Audio.Media._ID);
		int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
		int songAlbum = songCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
		int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
		int songDuration = songCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
		int songSize = songCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
		int songType = songCursor.getColumnIndex(MediaStore.Audio.Media.MIME_TYPE);
		int songData = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
		int albumId = songCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
		//implement artist id
		int artistId = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID);

		this.id = songCursor.getLong(songId);
		this.title = songCursor.getString(songTitle);
		this.artist = songCursor.getString(songArtist);
		this.albumName = songCursor.getString(songAlbum);
		this.duration = songCursor.getString(songDuration);
		this.size = songCursor.getString(songSize);
		this.type = songCursor.getString(songType);
		this.data = songCursor.getString(songData);
		this.albumID = songCursor.getLong(albumId);
		this.artistID = songCursor.getLong(artistId);
	}

	public long getID(){return id;}
	public String getTitle(){return title;}
	public String getArtist(){return artist;}

}
