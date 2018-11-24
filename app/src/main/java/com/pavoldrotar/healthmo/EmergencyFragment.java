package com.pavoldrotar.healthmo;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        View rootView = inflater.inflate(R.layout.fragment_health, container, false);

        Handler handler =new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                handler.postDelayed(this, 3000);
                int bpm = ((HealthActivity)getActivity()).getBPM();
                TextView textView = (TextView) rootView.findViewById(R.id.section_label);
                textView.setText("" + bpm);
                //todo send intent to service
                Intent postIntent = new Intent();
                postIntent.setAction("PostIntent");
                postIntent.putExtra("bpm", bpm);


            }
        };
        handler.postDelayed(r, 0);
        return rootView;
    }
}
