package com.example.githubprofileviewer;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * Created by arushi on 18/03/18.
 */

public class SearchDialogFragment extends DialogFragment {

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks. */
    public interface SearchDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog, String username);
    }

    // Use this instance of the interface to deliver action events
    SearchDialogListener mListener;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_search_by_username, null);
        final EditText editUsername = dialogView.findViewById(R.id.edit_username);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(dialogView)
                // Add action buttons
                .setPositiveButton(R.string.search, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // search in Github
                        String username = editUsername.getText().toString();
                        mListener.onDialogPositiveClick(SearchDialogFragment.this
                                                        , username);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SearchDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    // Override the Fragment.onAttach() method to instantiate the SearchDialogListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity activity;

        if (context instanceof Activity) {
            activity = (Activity) context;
            try {
                mListener = (SearchDialogListener) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(context.toString()
                        + " must implement SearchDialogListener");
            }
        }
    }

    // Override the Activity.onAttach() method to instantiate the SearchDialogListener for older versions
    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            try {
                mListener = (SearchDialogListener) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString()
                        + " must implement SearchDialogListener");
            }
        }
    }

}
