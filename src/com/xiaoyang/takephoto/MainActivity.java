package com.xiaoyang.takephoto;

import java.util.MissingFormatArgumentException;

import com.xiaoyang.takephoto.circularview.RoundImageView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends Activity {
	private RoundImageView mineHeadRoundImageView;
	private String facePath;
	private static final int REQUEST_CODE_PHOTO = 1000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mineHeadRoundImageView = (RoundImageView) findViewById(R.id.mine_head_image);
		mineHeadRoundImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, SettingPhotoActivity.class);
				startActivity(intent);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE_PHOTO:
			if (requestCode == Activity.RESULT_OK) {
				facePath = data.getStringExtra("face");

			}
			super.onActivityResult(requestCode, resultCode, data);
		}
	}
}
