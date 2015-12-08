package com.example.huanlu.myruns;

import android.app.DialogFragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.app.Activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


public class ProfileActivity extends Activity {

    private static final String TAG = "CS65";
    public static final String PREFS_MYRUNS = "MyPrefs";

    public static final int REQUEST_CODE_TAKE_FROM_CAMERA = 0;
    public static final int REQUEST_CODE_TAKE_FROM_GALLERY = 1;
    public static final int REQUEST_CODE_CROP_PHOTO = 2;

    private static final String IMAGE_UNSPECIFIED = "image/*";
    private static final String URI_INSTANCE_STATE_KEY = "saved_uri";

    private Uri mImageCaptureUri;
    private ImageView mImageView;
    private boolean isTakenFromCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mImageView = (ImageView) findViewById(R.id.imagePhoto);

        if (savedInstanceState != null) {
            mImageCaptureUri = savedInstanceState
                    .getParcelable(URI_INSTANCE_STATE_KEY);
        }

        loadUserData();
    }

    /*// lifecycle calls this method to store current photo
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState() called");
        outState.putParcelable(URI_INSTANCE_STATE_KEY, mImageCaptureUri);
    }*/

    public void onChangePhotoClicked(View v) {
        displayDialog(MyDialogFragment.DIALOG_ID_PHOTO_PICKER);
    }

    public void displayDialog(int id) {
        DialogFragment fragment = MyDialogFragment.newInstance(id);
        fragment.show(getFragmentManager(),
                getString(R.string.dialog_fragment_tag_photo_picker));
    }

    public void onPhotoPickerItemSelected(int item) {
        Intent intent;

        switch (item) {

            case REQUEST_CODE_TAKE_FROM_CAMERA:
                // Take photo from camera，
                // Construct an intent with action
                // MediaStore.ACTION_IMAGE_CAPTURE
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Construct temporary image path and name to save the taken
                // photo
                mImageCaptureUri = Uri.fromFile(new File(Environment
                        .getExternalStorageDirectory(), "tmp_"
                        + String.valueOf(System.currentTimeMillis()) + ".jpg"));
                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                        mImageCaptureUri);
                intent.putExtra("return-data", true);
                try {
                    // Start a camera capturing activity
                    // REQUEST_CODE_TAKE_FROM_CAMERA is an integer tag you
                    // defined to identify the activity in onActivityResult()
                    // when it returns
                    startActivityForResult(intent, REQUEST_CODE_TAKE_FROM_CAMERA);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
                isTakenFromCamera = true;
                break;

            case REQUEST_CODE_TAKE_FROM_GALLERY:
                intent = new Intent((String) null, null);
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(intent, REQUEST_CODE_TAKE_FROM_GALLERY);
                break;

            default:
                return;
        }

    }

    public void onSaveClicked(View v) {

        // Save all information from the screen into a "shared preferences"
        // using private helper function

        saveUserData();

        Toast.makeText(getApplicationContext(),
                getString(R.string.save_message), Toast.LENGTH_SHORT).show();
        this.finish();
    }

    public void onCancelClicked(View v) {
        Toast.makeText(getApplicationContext(),
                getString(R.string.cancel_message), Toast.LENGTH_SHORT).show();
        this.finish();
    }

    // Handle data after activity returns.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;

        switch (requestCode) {
            case REQUEST_CODE_TAKE_FROM_CAMERA:
                // Send image taken from camera for cropping
                cropImage();
                break;

            case REQUEST_CODE_TAKE_FROM_GALLERY:
                // Send image taken from gallery for cropping
                mImageCaptureUri = data.getData();
                cropImage();
                break;

            case REQUEST_CODE_CROP_PHOTO:
                // Update image view after image crop
                Bundle extras = data.getExtras();
                // Set the picture image in UI
                if (extras != null) {
                    mImageView.setImageBitmap((Bitmap) extras.getParcelable("data"));
                }

                //save the cropped photo
                mImageView.buildDrawingCache();
                Bitmap bmap = mImageView.getDrawingCache();
                try {
                    FileOutputStream fos = openFileOutput(
                            getString(R.string.profile_photo_file_name), MODE_PRIVATE);
                    bmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.flush();
                    fos.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }

                // Delete temporary image taken by camera after crop.
                if (isTakenFromCamera) {
                    File f = new File(mImageCaptureUri.getPath());
                    if (f.exists())
                        f.delete();
                }

                break;
        }
    }


    // ****************** private helper functions ***************************//

    // load the user data from shared preferences if there is no data make sure
    // that we set it to something reasonable
    private void loadUserData() {

        // We can also use log.d to print to the LogCat

        Log.d(TAG, "loadUserData()");

        // Load and update all profile views

        // Load profile photo from internal storage

        try {
            FileInputStream fis = openFileInput(getString(R.string.profile_photo_file_name));

            Bitmap bmap = BitmapFactory.decodeStream(fis);
            mImageView.setImageBitmap(bmap);
            fis.close();
        } catch (IOException e) {
            // Default profile photo if no photo saved before.
            mImageView.setImageResource(R.drawable.dartmouth_logo);
        }

        // Get the shared preferences - create or retrieve the activity
        // preference object

        String mKey = getString(R.string.preference_name);
        SharedPreferences mPrefs = getSharedPreferences(mKey, MODE_PRIVATE);

        // Load the user name

        mKey = getString(R.string.preference_key_profile_name);
        String mValue = mPrefs.getString(mKey, " ");
        ((EditText) findViewById(R.id.editName)).setText(mValue);

        // Load the user email

        mKey = getString(R.string.preference_key_profile_email);
        mValue = mPrefs.getString(mKey, " ");
        ((EditText) findViewById(R.id.editEmail)).setText(mValue);

        // Load the user phone

        mKey = getString(R.string.preference_key_profile_phone);
        mValue = mPrefs.getString(mKey, " ");
        ((EditText) findViewById(R.id.editPhone)).setText(mValue);

        // Please Load gender info and set radio box

        mKey = getString(R.string.preference_key_profile_gender);

        int mIntValue = mPrefs.getInt(mKey, -1);
        // In case there isn't one saved before:
        if (mIntValue >= 0) {
            // Find the radio button that should be checked.
            RadioButton radioBtn = (RadioButton) ((RadioGroup) findViewById(R.id.radioGender))
                    .getChildAt(mIntValue);
            // Check the button.
            radioBtn.setChecked(true);
            //Toast.makeText(getApplicationContext(),
            //        "number of the radioButton is : " + mIntValue,
            //        Toast.LENGTH_SHORT).show();
        }

        // Load the user class

        mKey = getString(R.string.preference_key_profile_class);
        mValue = mPrefs.getString(mKey, " ");
        ((EditText) findViewById(R.id.editClass)).setText(mValue);

        // Load the user major

        mKey = getString(R.string.preference_key_profile_major);
        mValue = mPrefs.getString(mKey, " ");
        ((EditText) findViewById(R.id.editMajor)).setText(mValue);

    }

    // load the user data from shared preferences if there is no data make sure
    // that we set it to something reasonable
    private void saveUserData() {

        Log.d(TAG, "saveUserData()");

        // Commit all the changes into preference file
        // Save profile image into internal storage.
        //ImageView mImageView = (ImageView) findViewById(R.id.imagePhoto);
        mImageView.buildDrawingCache();
        Bitmap bmap = mImageView.getDrawingCache();
        try {
            FileOutputStream fos = openFileOutput(
                    getString(R.string.profile_photo_file_name), MODE_PRIVATE);
            bmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        // Getting the shared preferences editor

        String mKey = getString(R.string.preference_name);
        SharedPreferences mPrefs = getSharedPreferences(mKey, MODE_PRIVATE);

        SharedPreferences.Editor mEditor = mPrefs.edit();
        mEditor.clear();

        // Save name information

        mKey = getString(R.string.preference_key_profile_name);
        String mValue = (String) ((EditText) findViewById(R.id.editName))
                .getText().toString();
        mEditor.putString(mKey, mValue);

        // Save email information

        mKey = getString(R.string.preference_key_profile_email);
        mValue = (String) ((EditText) findViewById(R.id.editEmail))
                .getText().toString();
        mEditor.putString(mKey, mValue);

        // Save phone information

        mKey = getString(R.string.preference_key_profile_phone);
        mValue = (String) ((EditText) findViewById(R.id.editPhone))
                .getText().toString();
        mEditor.putString(mKey, mValue);

        // Read which index the radio is checked.

        // edit this out and use as a debug example
        // interesting bug because you try and write an int to a string

        mKey = getString(R.string.preference_key_profile_gender);

        RadioGroup mRadioGroup = (RadioGroup) findViewById(R.id.radioGender);
        int mIntValue = mRadioGroup.indexOfChild(findViewById(mRadioGroup
                .getCheckedRadioButtonId()));
        mEditor.putInt(mKey, mIntValue);

        // Save class information

        mKey = getString(R.string.preference_key_profile_class);
        mValue = (String) ((EditText) findViewById(R.id.editClass))
                .getText().toString();
        mEditor.putString(mKey, mValue);

        // Save major information

        mKey = getString(R.string.preference_key_profile_major);
        mValue = (String) ((EditText) findViewById(R.id.editMajor))
                .getText().toString();
        mEditor.putString(mKey, mValue);

        // Commit all the changes into the shared preference
        mEditor.commit();

        //Toast.makeText(getApplicationContext(), "saved name: " + mValue,
        //        Toast.LENGTH_SHORT).show();

    }

    // Crop and resize the image for profile
    private void cropImage() {
        // Use existing crop activity.
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(mImageCaptureUri, IMAGE_UNSPECIFIED);

        // Specify image size
        intent.putExtra("outputX", 100);
        intent.putExtra("outputY", 100);

        // Specify aspect ratio, 1:1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", true);
        // REQUEST_CODE_CROP_PHOTO is an integer tag you defined to
        // identify the activity in onActivityResult() when it returns
        startActivityForResult(intent, REQUEST_CODE_CROP_PHOTO);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}



