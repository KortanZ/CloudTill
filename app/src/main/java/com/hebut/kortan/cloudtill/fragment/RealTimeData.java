package com.hebut.kortan.cloudtill.fragment;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.ArcProgress;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.hebut.kortan.cloudtill.R;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RealTimeData.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RealTimeData#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RealTimeData extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private  OnFragmentMessageDeliverer mDeliverer;

    public RealTimeData() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RealTimeData.
     */
    // TODO: Rename and change types and number of parameters
    public static RealTimeData newInstance(String param1, String param2) {
        RealTimeData fragment = new RealTimeData();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_real_time_data, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Button uploadButton = (Button) getActivity().findViewById(R.id.uploadData);
        Button refreshButton = (Button) getActivity().findViewById(R.id.refreshData);
        TextView originText = (TextView) getActivity().findViewById(R.id.originData);
        TextView parsedText = (TextView) getActivity().findViewById(R.id.parsedData);
        ArcProgress teProgress = (ArcProgress) getActivity().findViewById(R.id.te_progress);
        ArcProgress hrProgress = (ArcProgress) getActivity().findViewById(R.id.hr_progress);
        List<TextView> viewList = new ArrayList<>();
        List<ArcProgress> progresseList = new ArrayList<>();
        viewList.add(originText);
        viewList.add(parsedText);
        progresseList.add(teProgress);
        progresseList.add(hrProgress);
        sendView(viewList, progresseList);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDeliverer != null) {
                    mDeliverer.onFragmentMessage();
                }
            }
        });
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDeliverer != null) {
                    mDeliverer.doSth();
                }
            }
        });
    }

    public void sendView(List<TextView> v, List<ArcProgress> arc) {
        if (mListener != null) {
            mListener.onFragmentInteraction(v, arc);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

        if (context instanceof OnFragmentMessageDeliverer) {
            mDeliverer = (OnFragmentMessageDeliverer) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(List<TextView> v, List<ArcProgress> arc);
    }

    public interface OnFragmentMessageDeliverer {
        void onFragmentMessage();
        void doSth();
    }

}
