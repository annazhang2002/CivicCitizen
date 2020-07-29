package com.example.votingapp.fragments.dialogFragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.votingapp.MethodLibrary;
import com.example.votingapp.R;
import com.example.votingapp.models.Action;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.io.File;

import static android.app.Activity.RESULT_OK;

public class ActionCompleteFragment extends DialogFragment {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 33;
    private static final String TAG = "ActionCompleteFragment";

    private DialogInterface.OnDismissListener onDismissListener;
    static PackageManager packageManager;
    Action action;
    ImageView ivClose;
    EditText etNotes;
    ImageView ivPreview;
    ImageView ivDelete;
    Button btnSave;
    Button btnPicture;
    File photoFile;
    private String photoFileName = "photo.jpg";
    private static Context context;
    ImageView ivShare;
    TextView tvMessage;

    public ActionCompleteFragment() {
        // Required empty public constructor
    }

    public static ActionCompleteFragment newInstance(Context context1, PackageManager packageManager1, Action action) {
        ActionCompleteFragment frag = new ActionCompleteFragment();
        Bundle args = new Bundle();
        args.putParcelable("action", Parcels.wrap(action));
        frag.setArguments(args);
        context = context1;
        packageManager = packageManager1;
        return frag;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_action_complete, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        action = Parcels.unwrap(getArguments().getParcelable("action"));

        setOnDismissListener(onDismissListener);

        ivClose = view.findViewById(R.id.ivClose);
        etNotes = view.findViewById(R.id.etCompose);
        ivPreview = view.findViewById(R.id.ivPreview);
        btnSave = view.findViewById(R.id.btnSave);
        btnPicture = view.findViewById(R.id.btnPicture);
        ivDelete = view.findViewById(R.id.ivDelete);
        ivShare = view.findViewById(R.id.ivShare);
        tvMessage = view.findViewById(R.id.tvMessage);

        tvMessage.setText("Great work! You just " + action.getName() + "! Thanks for being a Civic Citizen");

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MethodLibrary.shareContentHTML(context, "<p>Hey, I just " + action.getName() + "! Make sure you do before the election deadline!</p>");
            }
        });

        if (action.getImage() == null) {
            ivPreview.setVisibility(View.GONE);
            ivDelete.setVisibility(View.GONE);
        } else {
            Glide.with(getContext()).load(action.getImage().getUrl()).into(ivPreview);
            btnPicture.setText("Retake Picture");
        }
        etNotes.setText(action.getNotes());
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAction(action, etNotes.getText().toString(), photoFile);
            }
        });
        btnPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCamera(view);
            }
        });
        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photoFile = null;
                ivPreview.setVisibility(View.GONE);
                ivDelete.setVisibility(View.GONE);
                btnPicture.setText("Take Picture");
            }
        });
    }


    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onDismissListener != null) {
            onDismissListener.onDismiss(dialog);
        }
    }

    private void saveAction(Action action, String notes, File photoFile) {
        Log.i(TAG, "Saving action");

        action.setNotes(notes);
        if (photoFile != null) {
            action.setImage(new ParseFile(photoFile));
        } else {
            action.remove(Action.KEY_IMAGE);
        }
        action.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue saving the user" , e);
                    return;
                }

                Log.i(TAG, "User changes were saved!!");
                dismiss();
            }
        });
    }

    public void launchCamera(View view) {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.votingAppFBU.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(packageManager) != null) {
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
                ivPreview.setImageBitmap(takenImage);
                ivPreview.setVisibility(View.VISIBLE);
            } else { // Result was a failure
                Log.i(TAG, "Picture wasn't taken");
//                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }
}

