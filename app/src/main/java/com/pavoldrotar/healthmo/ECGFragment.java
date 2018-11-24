package com.pavoldrotar.healthmo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;

import com.google.gson.Gson;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.movesense.mds.Mds;
import com.movesense.mds.MdsException;
import com.movesense.mds.MdsNotificationListener;
import com.movesense.mds.MdsSubscription;

import java.util.ArrayList;
import java.util.List;

public class ECGFragment extends Fragment {

    private static final int DEFAULT_SAMPLE_RATE = 125;
    static HealthActivity s_INSTANCE = null;
    private static final String LOG_TAG = ECGActivity.class.getSimpleName();

    public static final String SERIAL = "serial";
    String connectedSerial;

    private LineGraphSeries<DataPoint> mSeriesECG;
    private int mDataPointsAppended = 0;
    private MdsSubscription mECGSubscription;
    private MdsSubscription mHRSubscription;

    public static final String URI_EVENTLISTENER = "suunto://MDS/EventListener";
    public static final String SCHEME_PREFIX = "suunto://";

    public static final String URI_ECG_INFO = "/Meas/ECG/Info";
    public static final String URI_ECG_ROOT = "/Meas/ECG/";
    public static final String URI_MEAS_HR = "/Meas/HR";

    Switch mSwitchECGEnabled;
    Spinner mSpinnerSampleRates;

    private ArrayAdapter<String> mSpinnerAdapter;
    private final List<String> mSpinnerRates = new ArrayList<>();

    private Mds getMDS() {return MainActivity.mMds;}
    public ECGFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_ecg, container, false);

        // Find serial in opening intent
        Intent intent = this.getActivity().getIntent();
        connectedSerial = intent.getStringExtra(SERIAL);

        // Set ECG graph
        /*GraphView graph = (GraphView) rootView.findViewById(R.id.graphECG);
        mSeriesECG = new LineGraphSeries<DataPoint>();
        graph.addSeries(mSeriesECG);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(500);

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(-2000);
        graph.getViewport().setMaxY(2000);*/


        enableECGSubscription();

        return rootView;
    }

    private void enableECGSubscription() {
        // Make sure there is no subscription
        unsubscribeECG();

        // Build JSON doc that describes what resource and device to subscribe
        StringBuilder sb = new StringBuilder();
        int sampleRate = 125;
        final int GRAPH_WINDOW_WIDTH = sampleRate*3;
        String strContract = sb.append("{\"Uri\": \"").append(connectedSerial).append(URI_ECG_ROOT).append(sampleRate).append("\"}").toString();
        Log.d(LOG_TAG, strContract);
        // Clear graph
        mSeriesECG.resetData(new DataPoint[0]);
        final GraphView graph = (GraphView) getView().findViewById(R.id.graphECG);
        graph.getViewport().setMaxX(GRAPH_WINDOW_WIDTH);
        mDataPointsAppended = 0;

        mECGSubscription = getMDS().builder().build(this.getActivity()).subscribe(URI_EVENTLISTENER,
                strContract, new MdsNotificationListener() {
                    @Override
                    public void onNotification(String data) {
                        Log.d(LOG_TAG, "onNotification(): " + data);

                        ECGResponse ecgResponse = new Gson().fromJson(
                                data, ECGResponse.class);

                        if (ecgResponse != null) {
                            for (int sample : ecgResponse.body.samples) {
                                try {
                                    mSeriesECG.appendData(
                                            new DataPoint(mDataPointsAppended, sample), true,
                                            GRAPH_WINDOW_WIDTH);
                                } catch (IllegalArgumentException e) {
                                    Log.e(LOG_TAG, "GraphView error ", e);
                                }
                                mDataPointsAppended++;
                            }
                        }
                    }

                    @Override
                    public void onError(MdsException error) {
                        Log.e(LOG_TAG, "onError(): ", error);
                        unsubscribeECG();
                    }
                });

    }

    private void unsubscribeECG() {
        if (mECGSubscription != null) {
            mECGSubscription.unsubscribe();
            mECGSubscription = null;
        }
    }
}