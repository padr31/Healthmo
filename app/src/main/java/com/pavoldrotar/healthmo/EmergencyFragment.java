package com.pavoldrotar.healthmo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class EmergencyFragment extends Fragment {

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */

    private static final String ARG_SECTION_NUMBER = "section_number";
    private Integer previousBpm;
    private boolean isDialogDisplayed = false;

    public EmergencyFragment() {

    }

    private void setDialogDisplayedStatus(boolean isDisplayed) {
        isDialogDisplayed = isDisplayed;
    }

    public void showAlertDialog(Integer bpm) {
        AlertDialog dialog = new AlertDialog.Builder(this.getContext())
                .setTitle("Do you feel ok?")
                .setMessage("Shall we call your trusted contact?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("EmergencyFragment", "calling to trustee, bpm:" +  bpm);
                        sendEmergencyAlert();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            private static final int AUTO_DISMISS_MILLIS = 10000;
            @Override
            public void onShow(final DialogInterface dialog) {
                setDialogDisplayedStatus(true);
                final Button defaultButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                final CharSequence positiveButtonText = defaultButton.getText();
                new CountDownTimer(AUTO_DISMISS_MILLIS, 100) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        defaultButton.setText(String.format(
                                Locale.getDefault(), "%s (%d)",
                                positiveButtonText,
                                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) + 1 //add one so it never displays zero
                        ));
                    }
                    @Override
                    public void onFinish() {
                        if (((AlertDialog) dialog).isShowing()) {
                            dialog.dismiss();
                            setDialogDisplayedStatus(false);
                        }
                    }
                }.start();
            }
        });
        dialog.show();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_emergency, container, false);

        Handler handler =new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                handler.postDelayed(this, 3000);
                int bpm = ((HealthActivity)getActivity()).getBPM();
                Log.i("bpm", String.valueOf(bpm));
                TextView textView = (TextView) rootView.findViewById(R.id.section_label);
                textView.setText("" + bpm);
                //todo send intent to service
                Intent i = new Intent(getContext(), PostService.class);
                i.putExtra("bpm", bpm);
                i.putExtra("event", "bpm");
                getContext().startService(i);

                if (bpm > 120 || bpm < 40) {
                    if (previousBpm != null && previousBpm != bpm) {
                        showAlertDialog(bpm);
                    }
                }
                previousBpm = bpm;
            }
        };
        handler.postDelayed(r, 0);

        //handle buttons
        Button btnCallEmergency = (Button) rootView.findViewById(R.id.btn_call_emergency);
        btnCallEmergency.setOnClickListener(e -> {
            sendEmergencyAlert();
        });


        return rootView;
    }


    private void sendEmergencyAlert() {
        EditText phone = (EditText) this.getView().getRootView().findViewById(R.id.txt_phone);
        Intent i = new Intent(getContext(), PostService.class);
        i.putExtra("event", "CALL_EMERGENCY");
        i.putExtra("to_phone_number", phone.getText().toString());
        i.putExtra("from_phone_number", "+37253918121");
        getContext().startService(i);
    }
}
