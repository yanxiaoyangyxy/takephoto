package com.xiaoyang.takephoto;

import java.io.File;
import java.io.FileOutputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoyang.takephoto.circularview.RoundImageView;

/**
 * 设置头像
 * 
 * @author Lisper
 * @date 2015-4-7 上午11:25:57
 * @version V1.0
 */
public class SettingPhotoActivity extends Activity {
	private final int REQUEST_CODE_PHOTO = 2000;
	private final int REQUEST_CODE_CAMERA = 2001;
	private final int REQUEST_CODE_CROP = 2002;
	private String facePath = System.currentTimeMillis() + ".png";
	private TextView tv_getfrom_camera;
	private TextView tv_getfrom_photo;
	private TextView tv_getfrom_cancel;
	private Bitmap photo;
	private RelativeLayout layout_photo;
	private LinearLayout mLayout_operation;
	private Animation mAnimation_bgAlpha;
	private Animation mAnimation_bottomIn;
	private String from;
	public static String downLoad = "";
	public static String cache = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_photo);
		from = getIntent().getStringExtra("from");
		tv_getfrom_camera = (TextView) findViewById(R.id.tv_getfrom_camera);
		tv_getfrom_photo = (TextView) findViewById(R.id.tv_getfrom_photo);
		tv_getfrom_cancel = (TextView) findViewById(R.id.tv_getfrom_cancel);

		layout_photo = (RelativeLayout) findViewById(R.id.layout_photo);
		mLayout_operation = (LinearLayout) findViewById(R.id.mLayout_operation);
		tv_getfrom_camera.setOnClickListener(new OnPhotoByCamaraListener());
		tv_getfrom_photo.setOnClickListener(new OnPhotoByAlbumListener());
		tv_getfrom_cancel.setOnClickListener(new OnPhotoCancelListener());
		layout_photo.setOnClickListener(new onLayoutClick());
	}

	@Override
	protected void onResume() {
		super.onResume();
	//	layout_photo.startAnimation(mAnimation_bgAlpha);

	}

	private void getImageFromPhoto() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(intent, REQUEST_CODE_PHOTO);
	}

	private void getImageFromCamera() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// 下面这句指定调用相机拍照后的照片存储的路径
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(downLoad + facePath)));
		startActivityForResult(intent, REQUEST_CODE_CAMERA);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_CODE_PHOTO: {
				if (data != null) {
					startPhotoZoom(data.getData(), from);
				}
			}
				break;
			case REQUEST_CODE_CAMERA: {
				if (downLoad != null) {
					File face = new File(downLoad + facePath);
					if (face.exists()) {
						startPhotoZoom(Uri.fromFile(face), from);
					}
				} else {
					File face = new File(getCacheDir() + facePath);
					if (face.exists()) {
						startPhotoZoom(Uri.fromFile(face), from);
					}
				}
			}
				break;
			case REQUEST_CODE_CROP:
				if (data != null) {
					saveFaceImage(data);
					if (downLoad == null) {
						setResult(RESULT_OK, new Intent().putExtra("face", getCacheDir() + facePath));
					} else {
						setResult(RESULT_OK, new Intent().putExtra("face", downLoad + facePath));
					}
				} else {
					setResult(RESULT_OK, new Intent().putExtra("face", ""));
				}
				this.finish();
				break;
			default:
				break;
			}
		}
	}

	public void saveFaceImage(Intent picdata) {
		Bundle extras = picdata.getExtras();
		if (extras != null) {
			photo = extras.getParcelable("data");
			File face = null;
			if (downLoad == null) {
				face = new File(getCacheDir() + facePath);
				if (face.exists()) {
					startPhotoZoom(Uri.fromFile(face), from);
				}
			} else {
				face = new File(downLoad + facePath);
				if (face.exists()) {
					face.delete();
				}
			}
			try {
				FileOutputStream out = new FileOutputStream(face);
				// 不压缩
				photo.compress(Bitmap.CompressFormat.PNG, 100, out);
				out.flush();
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void startPhotoZoom(Uri uri, String from) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		if (from.equals("touxiang")) {
			intent.putExtra("aspectX", 240);
			intent.putExtra("aspectY", 240);
		}
		if (from.equals("beijing")) {
			DisplayMetrics dm = getResources().getDisplayMetrics();
			intent.putExtra("aspectX", dm.widthPixels);
			intent.putExtra("aspectY", dm.widthPixels / 2);
		}

		intent.putExtra("outputX", 240);
		intent.putExtra("outputY", 240);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, REQUEST_CODE_CROP);
	}

	/** 事件监听---------------------------------------- */
	private class OnPhotoByCamaraListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			getImageFromCamera();
		}
	}

	private class OnPhotoByAlbumListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			getImageFromPhoto();
		}
	}

	private class OnPhotoCancelListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			setResult(RESULT_CANCELED);
			SettingPhotoActivity.this.finish();
		}
	}

	private class onLayoutClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			SettingPhotoActivity.this.finish();

		}

	}
}
