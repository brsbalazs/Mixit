package hu.bme.aut.mixit.settings_activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;

import hu.bme.aut.mixit.R;

public class SettingsDialogFragment extends DialogFragment
{

    public static final String TAG = "SettingsDialog";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentActivity activity = getActivity();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {



        Dialog dialog =  new AlertDialog.Builder(requireContext())
                .setTitle("")
                .setView(getContentView())
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        onCancel(dialogInterface);
                    }
                })
                .create();



        return dialog;

    }


    private View getContentView() {


        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_settings, null);


        return contentView;


    }

}
