package il.ac.tau.team3.shareaprayer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.Facebook.DialogListener;

public class FacebookConnector {
	private Facebook facebook = new Facebook("187659291286244");
	private boolean facebookConnected = false;
	private final String desc_footer = "<br>For futher details: download http://share-a-prayer.googlecode.com/files/ShareAPrayer.apk";
	
	private final String FACEBOOK_STARTUP_KEY = "FACEBOOK_STARTUP";
	private final String FACEBOOK_CONFIGURED_KEY = "FACEBOOK_CONFIGURED";
	
	private boolean facebook_connectStatup = false;
	private boolean facebook_configured = false;
	
	private Activity activity;
	
	private SharedPreferences settings;
	
	public void setConnectOnStartup(boolean startup)	{
		facebook_connectStatup = startup;
		SharedPreferences.Editor edit = settings.edit();
		edit.putBoolean(FACEBOOK_STARTUP_KEY, facebook_connectStatup);
		edit.commit();
	}
	
	public void connect()	{
			facebook.authorize(activity, new String[]{"publish_stream"/*,"read_stream","offline_access"*/},0, new DialogListener() {
        	
			public void onCancel() {
				// TODO Auto-generated method stub
				
			}

			public void onComplete(Bundle values) {
				// TODO Auto-generated method stub
				facebookConnected = true;
				facebook_configured = true;
				SharedPreferences.Editor edit = settings.edit();
				edit.putBoolean(FACEBOOK_CONFIGURED_KEY, facebook_configured);
				edit.commit();
				
				publishOnFacebook("Started using Share-A-Prayer",
						"Welcome to Share-A-Prayer. <br>" + "This application will help to find the closest minyan for the next pray.");
				
				
				
			}

			public void onError(DialogError e) {
				// TODO Auto-generated method stub
				
			}

			public void onFacebookError(FacebookError e) {
				// TODO Auto-generated method stub
				
			}

        });

	}

    public FacebookConnector(Activity a)	{
    	activity = a;
    	
    	settings = a.getSharedPreferences("ShareAPrayer", 0);
    	
    	facebook_connectStatup = settings.getBoolean(FACEBOOK_STARTUP_KEY, false);
    	facebook_configured = settings.getBoolean(FACEBOOK_CONFIGURED_KEY, false);
    	
    	if ((facebook_connectStatup) && (facebook_configured))	{
    		connect();
    	}
    		
      }

	
	
	public void publishOnFacebook(final String headline, final String description)	{
		if ((!facebookConnected) || (!facebook_connectStatup) || (!facebook_configured))	{
			return;
		}
		
		AsyncFacebookRunner fbrunner = new AsyncFacebookRunner(facebook);
		Bundle params = new Bundle();
		params.putString("message", headline);
		params.putString("description", desc_footer);
		params.putString("picture", "http://share-a-prayer.appspot.com/logo.png");
		params.putString("icon", "http://share-a-prayer.appspot.com/logo.png");
		params.putString("application", "Share-A-Prayer");
		params.putString("link", "http://www.facebook.com/apps/application.php?id=187659291286244");
		
		params.putString("caption", description);
		params.putString("access_token", facebook.getAccessToken());
		
		fbrunner.request("me/feed", params, "POST", new RequestListener() {

			public void onComplete(String response, Object state) {
				Log.e("Share-A-Prayer", "Facebook post response: " + response);
				
			}

			public void onFacebookError(FacebookError e, Object state) {
				// TODO Auto-generated method stub
				
			}

			public void onFileNotFoundException(
					FileNotFoundException e, Object state) {
				// TODO Auto-generated method stub
				
			}

			public void onIOException(IOException e, Object state) {
				// TODO Auto-generated method stub
				
			}

			public void onMalformedURLException(
					MalformedURLException e, Object state) {
				// TODO Auto-generated method stub
				
			}
			
		}, null);
	};
	
    

    public void autherizeCallback(int requestCode, int resultCode, Intent data)	{
		facebook.authorizeCallback(requestCode, resultCode, data);
	}
	
	
}
