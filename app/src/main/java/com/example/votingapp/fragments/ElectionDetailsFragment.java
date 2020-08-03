package com.example.votingapp.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.votingapp.Network;
import com.example.votingapp.R;
import com.example.votingapp.ReminderBroadcast;
import com.example.votingapp.fragments.dialogFragments.ActionCompleteFragment;
import com.example.votingapp.models.Action;
import com.example.votingapp.models.Contest;
import com.example.votingapp.models.Election;
import com.example.votingapp.models.Location;
import com.example.votingapp.models.Rep;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.ALARM_SERVICE;
import static com.example.votingapp.MethodLibrary.API_DATE_FORMAT;
import static com.example.votingapp.MethodLibrary.getTodayActionDate;
import static com.example.votingapp.MethodLibrary.openUrl;
import static com.example.votingapp.activities.MainActivity.goUserProfile;
import static com.example.votingapp.activities.MainActivity.packageManager;

public class ElectionDetailsFragment extends Fragment {

    private static final String TAG = "ElectionDetailsActivity";
    public static FragmentManager fragmentManager;
    static Election election;
    static ProgressDialog pd;
    static HashMap<String, Action> allActions;
    static List<Location> locations;
    static List<Contest> contests;
    public static CheckBox[] cbDeadlines;
    ImageView ivOpenRegister;
    ImageView ivOpenAbsentee;
    TextView tvLocations;
    TextView tvContests;

    TextView tvElectionDay;
    static Context context;
    private String currentFragment = "locations";

    public ElectionDetailsFragment() {
        // Required empty public constructor
    }

    public static ElectionDetailsFragment newInstance(Context context1, Election election1) {
        ElectionDetailsFragment fragment = new ElectionDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(Rep.class.getSimpleName(), Parcels.wrap(election1));
        context = context1;
        election = election1;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_election_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fragmentManager = getFragmentManager();
        createProgressDialog();
        pd.show();
        allActions = election.getActions();
        locations = new ArrayList<>();
        contests = new ArrayList<>();
        cbDeadlines = new CheckBox[3];
        cbDeadlines[0] = view.findViewById(R.id.cbRegisterVote);
        cbDeadlines[1] = view.findViewById(R.id.cbAbsentee);
        cbDeadlines[2] = view.findViewById(R.id.cbVote);
        tvElectionDay = view.findViewById(R.id.tvElectionDay);
        ivOpenRegister = view.findViewById(R.id.ivOpenRegister);
        ivOpenAbsentee = view.findViewById(R.id.ivOpenAbsentee);
        tvContests = view.findViewById(R.id.tvFriends);
        tvLocations = view.findViewById(R.id.tvTimeline);
        tvLocations.setBackgroundColor(getResources().getColor(R.color.whiteBlue));
        tvContests.setBackgroundColor(getResources().getColor(R.color.lightLightBlue));

        getActivity().setTitle(election.getName());
        tvElectionDay.setText(election.getSimpleElectionDay() + "");
        cbDeadlines[0].setText("Register to Vote (" + election.getRegisterDeadline() + ")");
        cbDeadlines[1].setText("Send in Absentee Ballot Application (" + election.getAbsenteeDeadline() + ")");
        cbDeadlines[2].setText("Vote!! (" + election.getVoteDeadline() + ")");
        setCheckboxes();

        // set click listeners for locations and contest toggle
        tvLocations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentFragment.equals("contests")) {
                    fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right).replace(R.id.flContainerDetails, LocationsFragment.newInstance(context, election, locations)).commit();
                    tvLocations.setBackgroundColor(getResources().getColor(R.color.whiteBlue));
                    tvContests.setBackgroundColor(getResources().getColor(R.color.lightLightBlue));
                    currentFragment = "locations";
                }
            }
        });
        tvContests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentFragment.equals("locations")) {
                    fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left).replace(R.id.flContainerDetails, ContestsFragment.newInstance(context, election, contests)).commit();
                    tvLocations.setBackgroundColor(getResources().getColor(R.color.lightLightBlue));
                    tvContests.setBackgroundColor(getResources().getColor(R.color.whiteBlue));
                    currentFragment = "contests";
                }
            }
        });

        // set notification if not voted yet
        if (!cbDeadlines[2].isChecked()) {
//            long miliSecsDate = milliseconds("2020-07-21");

            long miliSecsDate = milliseconds(election.getElectionReminderDate());
            scheduleNotification(miliSecsDate);
        }

        // when absentee deadline is held for a long time, it is striked
        cbDeadlines[1].setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Log.i(TAG, "onLongClick of absentee ballot");
                if (allActions.get("sent in absentee ballot").getStatus().equals("closed")) {
//                    cbDeadlines[1].setEnabled(true);
                    cbDeadlines[1].setChecked(true);
                    cbDeadlines[1].setPaintFlags(cbDeadlines[1].getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    allActions.get("sent in absentee ballot").setStatus("unfinished");
                    election.setActions(allActions);
                    Network.updateAction(allActions.get("sent in absentee ballot"));
                } else if (allActions.get("sent in absentee ballot").getStatus().equals("unfinished")) {
//                    cbDeadlines[1].setEnabled(false);
                    cbDeadlines[1].setChecked(false);
                    cbDeadlines[1].setPaintFlags(cbDeadlines[1].getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    allActions.get("sent in absentee ballot").setStatus("closed");
                    election.setActions(allActions);
                    Network.updateAction(allActions.get("sent in absentee ballot"));
                }
                return false;
            }
        });

        ivOpenRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openUrl("https://www.vote.org/voter-registration-deadlines/#" + Network.userState, context);
            }
        });
        ivOpenAbsentee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openUrl("https://www.vote.org/absentee-ballot-deadlines/#" + Network.userState, context);
            }
        });
        // set onchecklisteners for checkboxes
        for (int i = 0 ; i<cbDeadlines.length; i++) {
            final int finalI = i;
            cbDeadlines[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onCheckDeadline(finalI);
                }
            });
        }

        Network.getElectionDetails(election);
    }


    public void scheduleNotification(long millis) {
        Log.i(TAG, "notification scheduled");
        Intent intent = new Intent(context, ReminderBroadcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP,
                millis,
                pendingIntent);
    }

    public static long milliseconds(String date)
    {
        SimpleDateFormat sdf = new SimpleDateFormat(API_DATE_FORMAT);
        try
        {
            Date mDate = sdf.parse(date);
            long timeInMilliseconds = mDate.getTime();
            Log.i(TAG, "Date in milli :: " + timeInMilliseconds);
            return timeInMilliseconds;
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void setCheckboxes() {
        for (Map.Entry entry : allActions.entrySet()) {
            Action action = (Action) entry.getValue();
            Log.i(TAG, "here: Action: " + action.getName());
            // check if each action is completed or not
            for (int i = 0 ; i< Network.ACTION_NAMES.length; i++) {
                if (action.getName().equals(Network.ACTION_NAMES[i])) {
                    if (action.getStatus().equals("done")) {
                        cbDeadlines[i].setChecked(true);
                    } else if (action.getStatus().equals("closed")) {
//                            cbDeadlines[i].setEnabled(false);
                        cbDeadlines[i].setPaintFlags(cbDeadlines[i].getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    }else {
                        cbDeadlines[i].setChecked(false);
                    }
                    break;
                }
            }
        }
    }

    public void onCheckDeadline(Integer cbIndex) {
        if (allActions.get(Network.ACTION_NAMES[cbIndex]).getStatus().equals("closed")) {
            Toast.makeText(context, "This action was closed. To reopen it, hold the action name", Toast.LENGTH_LONG).show();
            cbDeadlines[cbIndex].setChecked(false);
        } else {
            if (cbDeadlines[cbIndex].isChecked()) {
                if (prevReqFulfilled(cbIndex)) {
                    Log.i(TAG, "button checked");
                    allActions.get(Network.ACTION_NAMES[cbIndex]).setStatus("done");
                    allActions.get(Network.ACTION_NAMES[cbIndex]).setDate(getTodayActionDate());
                    openCongratsFragment(allActions.get(Network.ACTION_NAMES[cbIndex]));
                } else {
                    Toast.makeText(context, "Sorry, you must complete the preceding action(s) before you are able to complete this actions", Toast.LENGTH_LONG).show();
                    cbDeadlines[cbIndex].setChecked(false);
                }
            } else {
                Log.i(TAG, "button unchecked");
//                allActions.get(Network.ACTION_NAMES[cbIndex]).setStatus("unfinished");
//                allActions.get(Network.ACTION_NAMES[cbIndex]).remove(Action.KEY_IMAGE);
//                allActions.get(Network.ACTION_NAMES[cbIndex]).setNotes("");
                cbDeadlines[cbIndex].setChecked(true);
                showConfirmDialog(cbIndex);
            }
            election.setActions(allActions);
            Network.updateAction(allActions.get(Network.ACTION_NAMES[cbIndex]));
        }
    }

    private void showConfirmDialog(final Integer cbIndex) {
        new MaterialAlertDialogBuilder(context)
                .setTitle("Delete action")
                .setMessage("Are you sure you want to delete this action?")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                        unCheck(cbIndex);
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton("No", null)
//                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void unCheck(Integer cbIndex) {
        cbDeadlines[cbIndex].setChecked(false);
        allActions.get(Network.ACTION_NAMES[cbIndex]).setStatus("unfinished");
        allActions.get(Network.ACTION_NAMES[cbIndex]).remove(Action.KEY_IMAGE);
        allActions.get(Network.ACTION_NAMES[cbIndex]).setNotes("");
        election.setActions(allActions);
        Network.updateAction(allActions.get(Network.ACTION_NAMES[cbIndex]));
    }

    private boolean prevReqFulfilled(Integer cbIndex) {
        for (int i =0 ; i< cbIndex; i++) {
            if (!cbDeadlines[i].isChecked()) {
                return false;
            }
        }
        return true;
    }

    public void openCongratsFragment(Action action) {
        ActionCompleteFragment actionCompleteFragment = ActionCompleteFragment.newInstance(context, packageManager, action);
        actionCompleteFragment.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                goUserProfile(ParseUser.getCurrentUser());
            }
        });
        actionCompleteFragment.show(getFragmentManager(), "fragment_compose");
    }

    public static void addLocations(JSONArray pollingLocations, String type) {
        try {
            locations.addAll(Location.fromJSON(pollingLocations, type));
            Log.i(TAG, String.valueOf(locations));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        fragmentManager.beginTransaction().replace(R.id.flContainerDetails, LocationsFragment.newInstance(context, election, locations)).commit();
        pd.hide();
    }

    public static void addContests(JSONArray array) {
        try {
            contests.addAll(Contest.fromJSON(array));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        fragmentManager.beginTransaction().replace(R.id.flContainerDetails, ContestsFragment.newInstance(context, election, contests)).commit();
    }

    public void createProgressDialog() {
        pd = new ProgressDialog(context);
        pd.setTitle("Loading...");
        pd.getWindow().setBackgroundDrawableResource(R.drawable.election_card);
        pd.setCancelable(false);
    }
}
