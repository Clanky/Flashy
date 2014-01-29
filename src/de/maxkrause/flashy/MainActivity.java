package de.maxkrause.flashy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

public class MainActivity extends Activity {

	ImageButton btnSwitch;
	ImageButton btnSwitch1;
	ImageView imageView;

	private Camera camera;
	private boolean isFlashOn;
	private boolean hasFlash;
	private boolean isOnscreenOn;
	Parameters params;
	MediaPlayer mp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// flash switch button
		btnSwitch = (ImageButton) findViewById(R.id.btnSwitch);
		btnSwitch1 = (ImageButton) findViewById(R.id.btnSwitch1);
		imageView = (ImageView) findViewById(R.id.imageView1);

		/*
		 * First check if device is supporting flashlight or not
		 */
		hasFlash = getApplicationContext().getPackageManager()
				.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

		if (!hasFlash) {
			// device doesn't support flash
			// Show alert message and close the application
			AlertDialog alert = new AlertDialog.Builder(MainActivity.this)
					.create();
			alert.setTitle("Error");
			alert.setMessage("Sorry, but your device does not support flashlights!");
			alert.setButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// closing the application
					finish();
				}
			});
			alert.show();
			return;
		}

		// get the camera
		getCamera();
		
		// displaying button image
		toggleButtonImage();
		
		/*
		 * Switch button click event to toggle flash on/off
		 */
		
		/*
		 * Switch button click event to toggle flash on/off
		 */
		btnSwitch1.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if (!isOnscreenOn) {
					// turn on onscreen light
					turnOnOnscreen();
				}
			}
			});
		
		imageView.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if (isOnscreenOn) {
					// turn off onscreen light
					turnOffOnscreen();
				}
			}
			});
		
		btnSwitch.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
			if(!isOnscreenOn){
				if (isFlashOn) {
					// turn off flash
					turnOffFlash();
				}else{
					turnOnFlash();
				}
			}
			}
			
			
		});
		
		
	}

	/*
	 * Get the camera
	 */
	private void getCamera() {
		if (camera == null) {
			try {
				camera = Camera.open();
				params = camera.getParameters();
			} catch (RuntimeException e) {
				Log.e("Failed to get your camera: ", e.getMessage());
			}
		}
	}

	/*
	 * Turning On flash
	 */
	private void turnOnFlash() {
		if (!isFlashOn) {
			if (camera == null || params == null) {
				return;
			}
			// play sound
			playSound();
			
			params = camera.getParameters();
			params.setFlashMode(Parameters.FLASH_MODE_TORCH);
			camera.setParameters(params);
			camera.startPreview();
			isFlashOn = true;
			
			// changing button/switch image
			toggleButtonImage();
		}

	}

	/*
	 * Turning Off flash
	 */
	private void turnOffFlash() {
		if (isFlashOn) {
			if (camera == null || params == null) {
				return;
			}
			// play sound
			playSound();
			
			params = camera.getParameters();
			params.setFlashMode(Parameters.FLASH_MODE_OFF);
			camera.setParameters(params);
			camera.stopPreview();
			isFlashOn = false;
			
			// changing button/switch image
			toggleButtonImage();
		}
	}
	
	/*
	 * * Turning on the onscreen flashlight
	 */
	private void turnOnOnscreen() {
		if (!isOnscreenOn) {
			playSound();
			
			imageView.setVisibility(imageView.VISIBLE);
			isOnscreenOn = true;
		}
	}
	
	/*
	 * * Turning of the onscreen flashlight
	 */
	private void turnOffOnscreen() {
		if (isOnscreenOn) {
			playSound();
			
			imageView.setVisibility(imageView.INVISIBLE);
			isOnscreenOn = false;
		}
	}
	
	/*
	 * Playing sound
	 * will play button toggle sound on flash on / off
	 * */
	private void playSound(){
		if(isFlashOn || isOnscreenOn ){
			mp = MediaPlayer.create(MainActivity.this, R.raw.light_switch_off);
		}else{
			mp = MediaPlayer.create(MainActivity.this, R.raw.light_switch_on);
		}
		mp.setOnCompletionListener(new OnCompletionListener() {

            public void onCompletion(MediaPlayer mp) {
                // TODO Auto-generated method stub
                mp.release();
            }
        }); 
		mp.start();
	}
	
	/*
	 * Toggle switch button images
	 * changing image states to on / off
	 * */
	private void toggleButtonImage(){
		if(isFlashOn){
			btnSwitch.setImageResource(R.drawable.btn_switch_on);
		}else{
			btnSwitch.setImageResource(R.drawable.btn_switch_off);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
		
		// on pause turn off the flash
		turnOffFlash();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		// on resume turn on the flash
		if(hasFlash)
			turnOnFlash();
	}

	@Override
	protected void onStart() {
		super.onStart();
		
		// on starting the app get the camera params
		getCamera();
	}

	@Override
	protected void onStop() {
		super.onStop();
		
		// on stop release the camera
		if (camera != null) {
			camera.release();
			camera = null;
		}
	}

}
