package com.nicolappli.go4lunch.Controllers.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nicolappli.go4lunch.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PageFragment extends Fragment {

    public static final String KEY_POSITION = "position";
    public static final String KEY_COLOR = "color";


    public PageFragment() {
        // Required empty public constructor
    }

    public static PageFragment newInstance(int position, int color){
        PageFragment frag = new PageFragment();

        Bundle args = new Bundle();
        args.putInt(KEY_POSITION, position);
        args.putInt(KEY_COLOR, color);
        frag.setArguments(args);

        return(frag);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View result = inflater.inflate(R.layout.fragment_page, container, false);

        LinearLayout rootView = result.findViewById(R.id.fragment_page_rootview);
        TextView textView = result.findViewById(R.id.fragment_page_title);

        int position = getArguments().getInt(KEY_POSITION, -1);
        int color = getArguments().getInt(KEY_COLOR, -1);

        rootView.setBackgroundColor(color);
        textView.setText("Page numéro "+position);

        return result;
    }
}
