/*
 * RenderPlayerService.java
 * Description:
 * Author: zxt
 */

package com.winjay.dlna.cast.dmr;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.winjay.dlna.cast.dmp.ExoPlayerActivity;
import com.winjay.dlna.cast.dmp.ImageDisplay;
import com.winjay.dlna.cast.util.Action;
import com.winjay.dlna.util.LogUtil;

public class RenderPlayerService extends Service {
	private static final String TAG = "RenderPlayerService";

	public IBinder onBind(Intent intent) {
		return null;
	}

	public void onStart(Intent intent, int startId) {
		//xgf fix bug null point
		if (null != intent) {
			super.onStart(intent, startId);
			String type = intent.getStringExtra("type");
			LogUtil.d(TAG, "type=" + type);
			Intent intent2;

			if (type.equals("audio")) {
				// new Thread(new RenderPlayerService.1(this,
				// intent.getStringExtra("playURI"),
				// intent.getStringExtra("name"))).start();
//				intent2 = new Intent(this, GPlayer.class);
				intent2 = new Intent(this, ExoPlayerActivity.class);
				intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent2.putExtra("name", intent.getStringExtra("name"));
				intent2.putExtra("playURI", intent.getStringExtra("playURI"));
				startActivity(intent2);
			} else if (type.equals("video")) {
//				intent2 = new Intent(this, GPlayer.class);
				intent2 = new Intent(this, ExoPlayerActivity.class);
				intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent2.putExtra("name", intent.getStringExtra("name"));
				intent2.putExtra("playURI", intent.getStringExtra("playURI"));
				startActivity(intent2);
			} else if (type.equals("image")) {
				intent2 = new Intent(this, ImageDisplay.class);
				intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent2.putExtra("name", intent.getStringExtra("name"));
				intent2.putExtra("playURI", intent.getStringExtra("playURI"));
				intent2.putExtra("isRender", true);
				startActivity(intent2);
			} else {
				intent2 = new Intent(Action.DMR);
				intent2.putExtra("playpath", intent.getStringExtra("playURI"));
				sendBroadcast(intent2);
			}
		}
	}
}
