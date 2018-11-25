package com.pavoldrotar.healthmo;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class EmergencyFragment extends Fragment {

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */

    private static final String ARG_SECTION_NUMBER = "section_number";

    public EmergencyFragment() {

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
            }
        };
        handler.postDelayed(r, 0);

        //handle buttons

        Button btnCallEmergency = (Button) rootView.findViewById(R.id.btn_call_emergency);
        btnCallEmergency.setOnClickListener(e -> {
            Intent i = new Intent(getContext(), PostService.class);
            i.putExtra("event", "CALL_EMERGENCY");
            getContext().startService(i);
        });


        return rootView;
    }
}
