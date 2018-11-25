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

public class SaunaFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sauna, container, false);

        //handle buttons
        Button btnEnterSauna = (Button)rootView.findViewById(R.id.btn_enter_sauna);
        btnEnterSauna.setOnClickListener(e -> {
            Intent i = new Intent(getContext(), PostService.class);
            i.putExtra("event", "ENTERED_SAUNA");
            getContext().startService(i);
        });

        Button btnExitSauna = (Button) rootView.findViewById(R.id.btn_exit_sauna);
        btnExitSauna.setOnClickListener(e -> {
            Intent i = new Intent(getContext(), PostService.class);
            i.putExtra("event", "LEFT_SAUNA");
            getContext().startService(i);
        });

        Button btnFeelGood = (Button) rootView.findViewById(R.id.btn_feel_good);
        btnFeelGood.setOnClickListener(e -> {
            Intent i = new Intent(getContext(), PostService.class);
            i.putExtra("event", "FEEL_GOOD");
            getContext().startService(i);
        });

        Button btnFeelBad = (Button) rootView.findViewById(R.id.btn_feel_bad);
        btnFeelBad.setOnClickListener(e -> {
            Intent i = new Intent(getContext(), PostService.class);
            i.putExtra("event", "FEEL_BAD");
            getContext().startService(i);
        });


        return rootView;
    }
}
