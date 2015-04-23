package jatx.testtaskandroid;

// my test task job

// main part
// start: 20:50 
// pause: 21:20
// resume: 21:30
// pause: 22:00
// resume: 7:10
// pause: 7:50
// resume: 8:05
// pause: 8:40
// resume: 8:50
// main part done: 9:25
// main part total time: 2h 50min

// AdMob
// start: 9:30
// AdMob done: 9:45
// AdMob total time: 15 min

// PlayServices Rating
// start: 11:00
// pause: 11:35
// resume: 11:40
// pause: 12:05
// resume: 12:15
// PlayServices Rating done: 13:30
// PlayServices Rating total time: 2h 15min

// total test task time: 5h 20min

//import jatx.utils.Debug;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.Scanner;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.plus.Plus;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKSdkListener;
import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import android.support.v7.app.ActionBarActivity;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements 
			GoogleApiClient.ConnectionCallbacks, 
			GoogleApiClient.OnConnectionFailedListener{
	private static final String[] scope = {"wall"};
	
	private static final int REQUEST_RESOLUTION = 579;
	private static final int REQUEST_SHOW_SCORE = 975;
	
	private static final int ACTION_SEND_SCORE = 2311;
	private static final int ACTION_SHOW_SCORE = 2399;
	
	private LinearLayout mWholeLayout;
	private TextView mEngWordLabel;
	private RadioGroup mRusWordGroup;
	private RadioButton mRusWord1Button;
	private RadioButton mRusWord2Button;
	private RadioButton mRusWord3Button;
	private TextView mScoreLabel;
	private Button mVKShareButton;
	private Button mShowRatingButton;
	
	private String[] mEngWordsArray;
	private String[] mRusWords1Array;
	private String[] mRusWords2Array;
	private String[] mRusWords3Array;
	
	private int mWordsCount;
	private int mCurrent;
	private int mSelectedWordNum;
	private int mScore = 0;
	private int mGoogleAction = 0;
	
	private MainActivity self;

	private GoogleApiClient mGoogleApiClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		self = this;
		
		//Debug.setCustomExceptionHandler(getApplicationContext());
		
		AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
		
		mWholeLayout = (LinearLayout) findViewById(R.id.whole_layout);
		mEngWordLabel = (TextView) findViewById(R.id.end_word);
		mRusWordGroup = (RadioGroup) findViewById(R.id.rus_word_group);
		mRusWord1Button = (RadioButton) findViewById(R.id.rus_word_1);
		mRusWord2Button = (RadioButton) findViewById(R.id.rus_word_2);
		mRusWord3Button = (RadioButton) findViewById(R.id.rus_word_3);
		mScoreLabel = (TextView) findViewById(R.id.score_label);
		mVKShareButton = (Button) findViewById(R.id.vk_share_button);
		mShowRatingButton = (Button) findViewById(R.id.show_rating_button);
		
		mEngWordsArray = getResources().getStringArray(R.array.eng_words);
		mRusWords1Array = getResources().getStringArray(R.array.rus_words_1);
		mRusWords2Array = getResources().getStringArray(R.array.rus_words_2);
		mRusWords3Array = getResources().getStringArray(R.array.rus_words_3);
		
		mWordsCount = mEngWordsArray.length;
		
		if (mRusWords1Array.length!=mWordsCount||
				mRusWords2Array.length!=mWordsCount||
				mRusWords3Array.length!=mWordsCount) {
			Log.e("test task", "words count mismatch");
			finish();
		}
		
		setCurrentItem(0);
		setScore();
		
		mRusWord1Button.setOnClickListener(new RadioListener(1));
		mRusWord2Button.setOnClickListener(new RadioListener(2));
		mRusWord3Button.setOnClickListener(new RadioListener(3));
		
		/*mVKShareButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				 final String vkPackage = "com.vkontakte.android";
				 
				 String scoreStr = getString(R.string.string_vk_score);
				 scoreStr = scoreStr.replace("SSS", Integer.toString(mScore*10));
				 
				 List<Intent> targetedShareIntents = new ArrayList<Intent>();
				 Intent shareIntent = new Intent(Intent.ACTION_SEND);
				 shareIntent.setType("text/plain");
				 List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(shareIntent, 0);
				 
				 if (!resInfo.isEmpty()){
					for (ResolveInfo resolveInfo : resInfo) {
						 String packageName = resolveInfo.activityInfo.packageName;
						 
						 if (packageName.equals(vkPackage)) {
							 Intent targetedShareIntent = new Intent(Intent.ACTION_SEND);
				             targetedShareIntent.setType("text/plain");
							 targetedShareIntent.putExtra(Intent.EXTRA_TEXT, scoreStr);
							 targetedShareIntent.setPackage(packageName);
				             targetedShareIntents.add(targetedShareIntent);
						 }
					}
				 } else {
					 Toast.makeText(self, getString(R.string.toast_no_vk), Toast.LENGTH_LONG).show();
				 }
				 
				 if (targetedShareIntents.size()>0) {
		            Intent intent = new Intent(targetedShareIntents.remove(0));
		            startActivity(intent);
			    } else {
			    	Toast.makeText(self, getString(R.string.toast_no_vk), Toast.LENGTH_LONG).show();
			    }
			}
		});*/
		
		mVKShareButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				vkInitAndPost();
			}
		});
		
		mShowRatingButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mGoogleApiClient.isConnected()) {
					final String leaderBoardId = getString(R.string.leaderboard_id);
					final Intent intent = Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient, leaderBoardId);
					startActivityForResult(intent, REQUEST_SHOW_SCORE);
				} else {
					mGoogleAction = ACTION_SHOW_SCORE;
					mGoogleApiClient.connect();
				}
			}
		});
		
		mGoogleApiClient = new GoogleApiClient.Builder(this)
					.addConnectionCallbacks(this)
					.addOnConnectionFailedListener(this)
					.addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN)
					.addApi(Games.API).addScope(Games.SCOPE_GAMES)
					.build();
	}

	@Override 
	protected void onResume() { 
		Log.i("test task", "on resume");
		
		super.onResume(); 
		VKUIHelper.onResume(this); 
	} 

	@Override 
	protected void onDestroy() { 
		Log.i("test task", "on destroy");
		
		super.onDestroy(); 
		VKUIHelper.onDestroy(this); 
	} 
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		VKUIHelper.onActivityResult(this, requestCode, resultCode, intent); 
		
		if (requestCode==REQUEST_RESOLUTION&&resultCode==RESULT_OK) {
			mGoogleApiClient.connect();
		}
	}
	
	public void setResult(boolean result) {
		int color;
		
		if (result) {
			mScore++;
			color = getResources().getColor(R.color.green);
		} else {
			color = getResources().getColor(R.color.red);
		}
		
		switch (mSelectedWordNum) {
		case 1:
			mRusWord1Button.setBackgroundColor(color);
			break;
			
		case 2:
			mRusWord2Button.setBackgroundColor(color);
			break;
			
		case 3:
			mRusWord3Button.setBackgroundColor(color);
			break;
		}
		
		ObjectAnimator waiter1 = ObjectAnimator.ofFloat(mWholeLayout, "alpha", 1.0f, 1.0f);
		waiter1.setDuration(400);
		ObjectAnimator fadeOut = ObjectAnimator.ofFloat(mWholeLayout, "alpha",  1.0f, 0.0f);
		fadeOut.setDuration(1000);
		fadeOut.addListener(new AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) {}

			@Override
			public void onAnimationEnd(Animator animation) {
				setScore();
				next();
			}

			@Override
			public void onAnimationCancel(Animator animation) {}
			@Override
			public void onAnimationRepeat(Animator animation) {}
		});
		ObjectAnimator waiter2 = ObjectAnimator.ofFloat(mWholeLayout, "alpha", 0.0f, 0.0f);
		waiter2.setDuration(400);
		ObjectAnimator fadeIn = ObjectAnimator.ofFloat(mWholeLayout, "alpha", 0.0f, 1.0f);
		fadeIn.setDuration(1000);
		
		final AnimatorSet mAnimationSet = new AnimatorSet();

		mAnimationSet.play(fadeIn).after(waiter2).after(fadeOut).after(waiter1);
		mAnimationSet.start();
		
	}
	
	public void showErrorInternet() {
		Toast.makeText(this, 
				getString(R.string.toast_error_internet), 
				Toast.LENGTH_LONG).show();
	}
	
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Log.e("test task", result.toString());
		
		if (result.hasResolution()) {
			try {
				result.startResolutionForResult(this, REQUEST_RESOLUTION);
			} catch (SendIntentException e) {
				e.printStackTrace();
			}
		} else {
			Toast.makeText(this, getString(R.string.toast_error_google), Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onConnected(Bundle bundle) {
		if (mGoogleAction == ACTION_SEND_SCORE) {
			mGoogleAction = 0;
			
			final String leaderBoardId = getString(R.string.leaderboard_id);
			Games.Leaderboards.submitScore(mGoogleApiClient, leaderBoardId, mScore*10);
		} else if (mGoogleAction == ACTION_SHOW_SCORE) {
			mGoogleAction = 0;
			
			final String leaderBoardId = getString(R.string.leaderboard_id);
			final Intent intent = Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient, leaderBoardId);
			startActivityForResult(intent, REQUEST_SHOW_SCORE);
		}  
	}

	@Override
	public void onConnectionSuspended(int cause) {
		Log.i("test task", "onConnectionSuspended() called. Trying to reconnect.");
	    mGoogleApiClient.connect();
	}
	
	private void scoreToGoogleRating() {
		if (mGoogleApiClient.isConnected()) {
			final String leaderBoardId = getString(R.string.leaderboard_id);
			Games.Leaderboards.submitScore(mGoogleApiClient, leaderBoardId, mScore*10);
		} else {
			mGoogleAction = ACTION_SEND_SCORE;
			mGoogleApiClient.connect();
		}
	}
	
	private void setScore() {
		String scoreStr = getString(R.string.string_score);
		scoreStr = scoreStr.replace("SSS", Integer.toString(mScore*10));
		scoreStr = scoreStr.replace("TTT", Integer.toString(mWordsCount*10));
		
		mScoreLabel.setText(scoreStr);
	}
	
	private void setCurrentItem(int pos) {
		mCurrent = pos;
		
		mEngWordLabel.setText(mEngWordsArray[pos]);
		
		mRusWord1Button.setText(mRusWords1Array[pos]);
		mRusWord2Button.setText(mRusWords2Array[pos]);
		mRusWord3Button.setText(mRusWords3Array[pos]);
		
		mRusWord1Button.setChecked(false);
		mRusWord2Button.setChecked(false);
		mRusWord3Button.setChecked(false);
		
		mRusWord1Button.setBackgroundColor(getResources().getColor(R.color.white));
		mRusWord2Button.setBackgroundColor(getResources().getColor(R.color.white));
		mRusWord3Button.setBackgroundColor(getResources().getColor(R.color.white));
	}
	
	private void next() {
		int pos = mCurrent+1;
		if (pos<mWordsCount) {
			setCurrentItem(pos);
		} else {
			mEngWordLabel.setVisibility(View.GONE);
			mRusWordGroup.setVisibility(View.GONE);
			mVKShareButton.setVisibility(View.VISIBLE);
			mShowRatingButton.setVisibility(View.VISIBLE);
			scoreToGoogleRating();
		}
 	}
	
	private void vkInitAndPost() {
		VKSdkListener listener = new VKSdkListener(){
			@Override
			public void onAccessDenied(VKError error) {
				Log.i("listener", "access denied");
			}

			@Override
			public void onCaptchaError(VKError error) {
				Log.i("listener", "captcha error");
			}

			@Override
			public void onTokenExpired(VKAccessToken token) {
				Log.i("listener", "token expired");
			}
			
			@Override 
			public void onReceiveNewToken(VKAccessToken token) {
				Log.i("listener", "new token received");
				
				VKSdk.setAccessToken(token, true);
				
				VKSdk.wakeUpSession();
				
				if (VKSdk.isLoggedIn()) {
					Log.i("vk", "logged in");
					postToVKWall();
				} else {
					Log.i("vk", "logged out");
				}
			}
		};

		VKSdk.initialize(listener, "4889698");
		VKUIHelper.onCreate(this);
		
		VKSdk.authorize(scope);
	}
	
	private void postToVKWall() {
		VKParameters param = new VKParameters();
		String msg = getString(R.string.string_vk_score);
		msg = msg.replace("SSS", Integer.toString(mScore*10));
		param.put("message", msg);
		VKRequest request = new VKRequest("wall.post", param);
		request.executeWithListener(new VKRequest.VKRequestListener() {
			@Override
            public void onComplete(VKResponse response) {
				Toast.makeText(self, getString(R.string.toast_vk_success), Toast.LENGTH_LONG).show();
			}
			
			@Override
			public void onError(VKError e) {
				Log.e("vk error", e.toString());
			}
		});
	}
	
	private class RadioListener implements View.OnClickListener {
		private int mNum;
		
		RadioListener(int num) {
			mNum = num;
		}

		@Override
		public void onClick(View v) {
			mSelectedWordNum = mNum;
			
			String engWord = mEngWordsArray[mCurrent];
			String rusWord = "";
			
			switch (mNum) {
			case 1:
				rusWord = mRusWords1Array[mCurrent];
				break;
				
			case 2:
				rusWord = mRusWords2Array[mCurrent];
				break;
				
			case 3:
				rusWord = mRusWords3Array[mCurrent];
				break;
			}
			
			Log.i("test task", rusWord);
			
			GetWordTask gwt = new GetWordTask(self, engWord, rusWord);
			gwt.execute(null, null, null);
		}
	}
	
	private static class GetWordTask extends AsyncTask<Void,Void,Void> {
		private static final String server = "srv2.tabatsky.ru";
		private static final String apiUrl = 
							"http://srv2.tabatsky.ru/testtaskapi/translate?eng=";
		
		
		private boolean mConnectSuccess = false;
		private boolean mResult = false;
		
		private String mEngWord;
		private String mRusWord;
		
		private WeakReference<MainActivity> ref;
		
		public GetWordTask(MainActivity activity, String engWord, String rusWord) {
			ref = new WeakReference<MainActivity>(activity);
			mEngWord = engWord;
			mRusWord = rusWord;
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			mConnectSuccess = testConnect();
			
			if (mConnectSuccess) {
				String url = apiUrl + mEngWord;
				
				try {
					Scanner scanner;
					scanner = new Scanner(new URL(url).openStream(), "UTF-8");
					String urlContent = scanner.useDelimiter("\\A").next().trim();
					scanner.close();
					
					mResult = urlContent.equals(mRusWord);
				} catch (MalformedURLException e) {
					e.printStackTrace();
					mConnectSuccess = false;
				} catch (IOException e) {
					e.printStackTrace();
					mConnectSuccess = false;
				}
			}
			
			return null;
		}
		
		@Override 
		protected void onPostExecute(Void result) {
			final MainActivity activity = ref.get();
			
			if (activity==null) return;
			
			if (mConnectSuccess) {
				activity.setResult(mResult);
			} else {
				activity.showErrorInternet();
			}
		}
		
		private boolean testConnect() {			
			Socket s = new Socket();
			
			try {
				s.connect(new InetSocketAddress(server, 80), 5000);
				s.close();
			} catch (IOException e) {
				return false;
			}
			
			return true;
		}
	}
	
	
}
