package com.example.weuller.peladaesporteclube;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class BottomMapFragment extends Fragment {

    TextView txtTitle, txtType, txtIsPublic, txtInUse;
    private Button btnVote;

    public BottomMapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_bottom_map, container, false);

        txtType = (TextView) view.findViewById(R.id.bottom_map_txtType);
        txtInUse = (TextView) view.findViewById(R.id.bottom_map_txtInUse);
        txtTitle = (TextView) view.findViewById(R.id.bottom_map_txtTitle);
        txtIsPublic = (TextView) view.findViewById(R.id.bottom_map_txtIsPublic);


        btnVote = (Button) view.findViewById(R.id.bottom_map_btnVote);

        btnVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

        return inflater.inflate(R.layout.fragment_bottom_map, container, false);
    }

}
