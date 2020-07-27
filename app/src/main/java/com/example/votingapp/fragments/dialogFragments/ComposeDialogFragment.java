package com.example.votingapp.fragments.dialogFragments;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.votingapp.MethodLibrary;
import com.example.votingapp.R;
import com.example.votingapp.models.Rep;

import org.parceler.Parcels;

public class ComposeDialogFragment extends DialogFragment {

    public static final String TAG = "ComposeDialogFragment";
    private static Context context1;
    static PackageManager packageManager1;

    EditText etCompose;
    EditText etSubject;
    Button btnSubmit;
    ImageView ivClose;
    Rep rep;

    public ComposeDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static ComposeDialogFragment newInstance(Context context, PackageManager packageManager) {
        ComposeDialogFragment frag = new ComposeDialogFragment();
        context1 = context;
        packageManager1 = packageManager;
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
        rep = Parcels.unwrap(getArguments().getParcelable(Rep.class.getSimpleName()));
        return inflater.inflate(R.layout.fragment_compose_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        etCompose = view.findViewById(R.id.etCompose);
        etSubject = view.findViewById(R.id.etSubject);
        btnSubmit = view.findViewById(R.id.btnSave);
        ivClose = view.findViewById(R.id.ivClose);

        // Show soft keyboard automatically and request focus to field
        etSubject.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MethodLibrary.sendEmail(rep.getEmail(), etSubject.getText().toString(), etCompose.getText().toString(), packageManager1, context1);
            }
        });
    }

}
