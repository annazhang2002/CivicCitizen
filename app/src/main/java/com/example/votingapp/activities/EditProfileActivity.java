package com.example.votingapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.votingapp.R;
import com.example.votingapp.models.User;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;

public class EditProfileActivity extends AppCompatActivity {
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 22;
    private static final String TAG = "EditProfileActivity";
    ImageView ivProfile;
    EditText etName;
    Button btnPic;
    EditText etAddress1;
    EditText etCity;
    EditText etState;
    EditText etZip;
    Button btnSave;
    File photoFile;
    ParseUser user;
    private String photoFileName = "photo.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        ivProfile = findViewById(R.id.ivImage);
        etName = findViewById(R.id.etName);
        btnPic = findViewById(R.id.btnPic);
        etAddress1 = findViewById(R.id.etAddress1);
        etCity = findViewById(R.id.etCity);
        etState = findViewById(R.id.etState);
        etZip = findViewById(R.id.etZip);
        btnSave = findViewById(R.id.btnSave);

        user = ParseUser.getCurrentUser();

        ParseFile profile = user.getParseFile(User.KEY_PROFILEPIC);
        if (profile != null) {
            Glide.with(this).load(profile.getUrl()).circleCrop().into(ivProfile);
        } else {
            Glide.with(this).load(R.drawable.default_profile).circleCrop().into(ivProfile);
        }
        etName.setText(user.getString(User.KEY_NAME));
        etAddress1.setText(user.getString(User.KEY_ADDRESS1));
        etCity.setText(user.getString(User.KEY_CITY));
        etState.setText(user.getString(User.KEY_STATE));
        etZip.setText(user.getString(User.KEY_ZIP));

        btnPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCamera(view);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveProfile(etAddress1.getText().toString(), etCity.getText().toString(),etState.getText().toString(),etZip.getText().toString(),etName.getText().toString(), photoFile);
            }
        });
    }

    private void saveProfile(String add1, String city, String state, String zip, String name, File photoFile) {
        Log.i(TAG, "Saving profile");
        user.put(User.KEY_NAME, name);
        user.put(User.KEY_ADDRESS1, add1);
        user.put(User.KEY_CITY, city);
        user.put(User.KEY_STATE, state);
        user.put(User.KEY_ZIP, zip);
        if (photoFile != null) {
            user.put(User.KEY_PROFILEPIC, new ParseFile(photoFile));
        }
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue saving the user" , e);
                    return;
                }

                Log.i(TAG, "User changes were saved!!");
//                pd.dismiss();
//                MainActivity.goUserProfile(user);
                goMainActivity();
            }
        });
    }

    public void goMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        this.startActivity(intent);
    }

    public void launchCamera(View view) {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(EditProfileActivity.this, "com.votingAppFBU.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                ivProfile.setImageBitmap(takenImage);
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }
}