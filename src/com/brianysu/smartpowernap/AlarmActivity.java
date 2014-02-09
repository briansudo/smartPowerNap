package com.brianysu.smartpowernap;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.brianysu.smartpowernap.other.SingleFragmentActivity;
import com.brianysu.smartpowernap.settings.SettingsActivity;


/** The main activity. */
public class AlarmActivity extends SingleFragmentActivity {
	
	/** The background color for the user. The format is "0xFFFFFF 0xFFFFFF".
	 * The first hex represents the background color, while the second one
	 * represents the button color. Initially set to default value. */
	private String mColors = "#33B5E5 #0099CC";

	
	private static final int RESULT_SETTINGS = 1;
	
	@Override
	protected Fragment createFragment() {
		return AlarmFragment.newInstance();
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
 
        case R.id.menu_settings:
            Intent y = new Intent(this, SettingsActivity.class);
            startActivityForResult(y, RESULT_SETTINGS);
            break;
        case R.id.menu_item_rate:
			Uri uri = Uri.parse("market://details?id=" + getPackageName());
			Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
			try {
				startActivity(goToMarket);
			} catch (ActivityNotFoundException e) {
				Toast.makeText(this, "Couldn't launch the market",
						Toast.LENGTH_LONG).show();
			}
			break;
        case R.id.menu_item_website:
			String url = "http://www.brianysu.com";
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(url));
			startActivity(i);
			break;
		case R.id.menu_item_report:
			Intent send = new Intent(Intent.ACTION_SENDTO);
			String uriText;

			String emailAddress = "briansudev@gmail.com";
			String subject = R.string.app_name + " Bug Report";
			String body = "Debug:";
			body += "\n OS Version: " + System.getProperty("os.version") + "("
					+ android.os.Build.VERSION.INCREMENTAL + ")";
			body += "\n OS API Level: " + android.os.Build.VERSION.SDK_INT;
			body += "\n Device: " + android.os.Build.DEVICE;
			body += "\n Model (and Product): " + android.os.Build.MODEL + " ("
					+ android.os.Build.PRODUCT + ")";
			body += "\n Screen Width: "
					+ getWindow().getWindowManager().getDefaultDisplay()
							.getWidth();
			body += "\n Screen Height: "
					+ getWindow().getWindowManager().getDefaultDisplay()
							.getHeight();
			body += "\n Hardware Keyboard Present: "
					+ (getResources().getConfiguration().keyboard != Configuration.KEYBOARD_NOKEYS);

			uriText = "mailto:" + emailAddress + "?subject=" + subject
					+ "&body=" + body;

			uriText = uriText.replace(" ", "%20");
			Uri emalUri = Uri.parse(uriText);

			send.setData(emalUri);
			startActivity(Intent.createChooser(send, "Send mail..."));
			break;
        }
        return true;
    }
	
}
