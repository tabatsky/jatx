package jatx.musictransmitter.android;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

//import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.support.v7.app.ActionBarActivity;

public class MusicEditorActivity extends ActionBarActivity {
	private TextView fileLabel;
	private EditText editTitle;
	private EditText editAlbum;
	private EditText editArtist;
	private EditText editNumber;
	private EditText editYear;
	private Button saveButton;
	private Button winToUtfButton;
	
	private AlertDialog exitDialog;

	private File f;
    
    private String artist;
	private String album;
	private String title;
	private String year;
	private String number;
	
	private boolean isWin = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_music_editor);
		
		Log.i("isWin", Boolean.valueOf(isWin).toString());
		
		fileLabel = (TextView) findViewById(R.id.file_label);
		editTitle = (EditText) findViewById(R.id.edit_title);
		editAlbum = (EditText) findViewById(R.id.edit_album);
		editArtist = (EditText) findViewById(R.id.edit_artist);
		editNumber = (EditText) findViewById(R.id.edit_number);
		editYear = (EditText) findViewById(R.id.edit_year);
		saveButton = (Button) findViewById(R.id.save_button);
		winToUtfButton = (Button) findViewById(R.id.win_to_utf_button);
		
		saveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				saveTags();
			}
		});
		
		winToUtfButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				winToUtf();
			}
		});
		
		Log.i("data", getIntent().getDataString());
		f = new File(getIntent().getData().getPath());
		Log.i("file", f.getName());
			
		//fileLabel.setText("Файл: " + f.getName());
		fileLabel.setText(getString(R.string.editor_file) + " " + f.getName());
		setTitle(getString(R.string.title_tag_editor));
			
		openTags();
	}
	
	@Override
	public void onBackPressed() {
		 Log.i("main activity", "on back pressed");
		 
		 exitDialog = new AlertDialog.Builder(this)
		 		.setTitle("Выход")
		 		.setMessage("Сохранить изменения?")
		 		.setPositiveButton("Да", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						saveTags();
						exitDialog.dismiss();
						Intent intent = new Intent();
						setResult(RESULT_OK, intent);
						finish();
					}
				})
				.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						exitDialog.dismiss();
						Intent intent = new Intent();
						setResult(RESULT_OK, intent);
						finish();
					}
				})
				.create();
		 
		 if (wasChanged()) {
			 exitDialog.show();
		 } else {
			 Intent intent = new Intent();
			 setResult(RESULT_OK, intent);
			 finish();
		 }
	}
	
	@Override
	public void onDestroy() {
		Log.i("activity", "on destroy");
		super.onDestroy();
	}
	
	private void winToUtf() {
		if (isWin) return;
		
		Log.i("method", "win to utf");
		
		String artist = editArtist.getText().toString();
		String album = editAlbum.getText().toString();
		String title = editTitle.getText().toString();
		
		try {
			byte[] artistBytes = artist.getBytes("ISO-8859-1");
			byte[] albumBytes = album.getBytes("ISO-8859-1");
			byte[] titleBytes = title.getBytes("ISO-8859-1");
			
			artist = new String(artistBytes, "WINDOWS-1251");
			album = new String(albumBytes, "WINDOWS-1251");
			title = new String(titleBytes, "WINDOWS-1251");
			
			isWin = true;
		} catch (UnsupportedEncodingException e) {
			Log.i("error", "unsupported encoding");
		}
		
		editArtist.setText(artist);
		editAlbum.setText(album);
		editTitle.setText(title);
	}
	
	private boolean wasChanged() {
		String artist = editArtist.getText().toString();
		String album = editAlbum.getText().toString();
		String title = editTitle.getText().toString();
		String year = editYear.getText().toString();
		String number = editNumber.getText().toString();
		
		boolean noChanges = 
				this.artist.equals(artist)
				&& this.album.equals(album)
				&& this.title.equals(title)
				&& this.year.equals(year)
				&& this.number.equals(number);
		
		return !noChanges;
	}
	
	private void saveTags() {
		try {
			MP3File mp3f = new MP3File(f);
			
			Tag tag = mp3f.createDefaultTag();
			artist = editArtist.getText().toString();
			album = editAlbum.getText().toString();
			title = editTitle.getText().toString();
			year = editYear.getText().toString();
			number = editNumber.getText().toString();
			
			tag.setField(FieldKey.ARTIST, artist);
			tag.setField(FieldKey.ALBUM_ARTIST, artist);
			tag.setField(FieldKey.ALBUM, album);
			tag.setField(FieldKey.TITLE, title);
			tag.setField(FieldKey.YEAR, correctNumber(year));
			tag.setField(FieldKey.TRACK, correctNumber(number));
			tag.setField(FieldKey.COMMENT, "tag created with jatx music tag editor");
			
			mp3f.setTag(tag);
			mp3f.save(f);
			Log.i("info", "tag saved");
		} catch (IOException e) {
			Log.i("exception", "io");
		} catch (TagException e) {
			Log.i("exception", "tag");
		} catch (ReadOnlyFileException e) {
			Log.i("exception", "read only");
		} catch (InvalidAudioFrameException e) {
			Log.i("exception", "invalid audio frame");
		} 
	}
	
	private void openTags() {
		try {
			MP3File mp3f = new MP3File(f);
			
			Tag tag = mp3f.getTagOrCreateDefault();
			artist = tag.getFirst(FieldKey.ARTIST);
			album = tag.getFirst(FieldKey.ALBUM);
			title = tag.getFirst(FieldKey.TITLE);
			year = tag.getFirst(FieldKey.YEAR);
			number = tag.getFirst(FieldKey.TRACK);
			
			editArtist.setText(artist);
			editAlbum.setText(album);
			editTitle.setText(title);
			editYear.setText(year);
			editNumber.setText(number);
			
			Log.i("open artist", artist);
			Log.i("open album", album);
			Log.i("open title", title);
			
			Log.i("info", "tag opened");
		} catch (IOException e) {
			Log.i("exception", "io");
		} catch (TagException e) {
			Log.i("exception", "tag");
		} catch (ReadOnlyFileException e) {
			Log.i("exception", "read only");
		} catch (InvalidAudioFrameException e) {
			Log.i("exception", "invalid audio frame");
		}
	}
	
	public String correctNumber(String num) {
		try {
			Integer.parseInt(num);
			return num;
		} catch (NumberFormatException e) {
			return "0";
		}
	}
}
