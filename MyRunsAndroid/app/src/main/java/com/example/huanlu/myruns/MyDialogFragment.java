package com.example.huanlu.myruns;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;

//MyRunsDialogFragment handles all the customized dialog boxes in our project.
//Differentiated by dialog id.
//
// Ref: http://developer.android.com/reference/android/app/DialogFragment.html

public class MyDialogFragment extends DialogFragment {

    // Different dialog IDs
    public static final int DIALOG_ID_ERROR = -1;
    public static final int DIALOG_ID_PHOTO_PICKER = 0;
    public static final int DIALOG_ID_DURATION = 1;
    public static final int DIALOG_ID_DISTANCE = 2;
    public static final int DIALOG_ID_CALORIES = 3;
    public static final int DIALOG_ID_HEART_RATE = 4;
    public static final int DIALOG_ID_COMMENT = 5;

    // For photo picker selection:
    public static final int ID_PHOTO_PICKER_FROM_CAMERA = 0;

    private static final String DIALOG_ID_KEY = "dialog_id";

    public static MyDialogFragment newInstance(int dialog_id) {
        MyDialogFragment frag = new MyDialogFragment();
        Bundle args = new Bundle();
        args.putInt(DIALOG_ID_KEY, dialog_id);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int dialog_id = getArguments().getInt(DIALOG_ID_KEY);

        final Activity parent = getActivity();

        AlertDialog.Builder builder;
        // Setup dialog appearance and onClick Listeners
        switch (dialog_id) {
            case DIALOG_ID_PHOTO_PICKER:
                // Build picture picker dialog for choosing from camera or gallery
                builder = new AlertDialog.Builder(parent);
                builder.setTitle(R.string.ui_profile_photo_picker_title);
                // Set up click listener, firing intents open camera
                DialogInterface.OnClickListener dlistener = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        // Item is ID_PHOTO_PICKER_FROM_CAMERA
                        // Call the onPhotoPickerItemSelected in the parent
                        // activity, i.e., ameraControlActivity in this case
                        ((ProfileActivity) parent)
                                .onPhotoPickerItemSelected(item);
                    }
                };
                // Set the item/s to display and create the dialog
                builder.setItems(R.array.ui_profile_photo_picker_items, dlistener);
                return builder.create();

            case DIALOG_ID_DURATION:
                final EditText editTextDuration = new EditText(parent);
                editTextDuration.setInputType(InputType.TYPE_CLASS_NUMBER
                        | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                builder = new AlertDialog.Builder(parent);
                builder.setTitle(R.string.ui_start_dialog_duration);
                builder.setView(editTextDuration);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String sDuration = editTextDuration.getText().toString();
                        if(!sDuration.isEmpty())
                            ((ListViewActivity) parent).setDuration(Integer.parseInt(sDuration));
                    }
                });
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                });
                return builder.create();

            case DIALOG_ID_DISTANCE:
                final EditText editTextDistance = new EditText(parent);
                editTextDistance.setInputType(InputType.TYPE_CLASS_NUMBER
                        | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                builder = new AlertDialog.Builder(parent);
                builder.setTitle(R.string.ui_start_dialog_distance);
                builder.setView(editTextDistance);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String sDistance = editTextDistance.getText().toString();
                        if(!sDistance.isEmpty())
                            ((ListViewActivity) parent).setDistance(Double.parseDouble(sDistance));
                    }
                });
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                });
                return builder.create();

            case DIALOG_ID_CALORIES:
                final EditText editTextCalories = new EditText(parent);
                editTextCalories.setInputType(InputType.TYPE_CLASS_NUMBER
                        | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                builder = new AlertDialog.Builder(parent);
                builder.setTitle(R.string.ui_start_dialog_calories);
                builder.setView(editTextCalories);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String sCalories = editTextCalories.getText().toString();
                        if(!sCalories.isEmpty())
                            ((ListViewActivity) parent).setCalories(Integer.parseInt(sCalories));
                    }
                });
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                });
                return builder.create();

            case DIALOG_ID_HEART_RATE:
                final EditText editTextHeartRate = new EditText(parent);
                editTextHeartRate.setInputType(InputType.TYPE_CLASS_NUMBER
                        | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                builder = new AlertDialog.Builder(parent);
                builder.setTitle(R.string.ui_start_dialog_heart_rate);
                builder.setView(editTextHeartRate);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String sHeartRate = editTextHeartRate.getText().toString();
                        if(!sHeartRate.isEmpty())
                            ((ListViewActivity) parent).setHeartRate(Integer.parseInt(sHeartRate));
                    }
                });
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                });
                return builder.create();

            case DIALOG_ID_COMMENT:
                final EditText editTextComment = new EditText(parent);
                editTextComment.setHint(R.string.ui_start_dialog_comment_hint);
                builder = new AlertDialog.Builder(parent);
                builder.setTitle(R.string.ui_start_dialog_comment);
                builder.setView(editTextComment);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String sComment = editTextComment.getText().toString();
                        if(!sComment.isEmpty())
                            ((ListViewActivity) parent).setComment(sComment);
                    }
                });
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                });
                return builder.create();

            default:
                return null;
        }
    }
}

