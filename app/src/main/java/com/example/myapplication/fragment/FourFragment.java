package com.example.myapplication.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.R;

import androidx.fragment.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class FourFragment extends Fragment {
    private Context context;

    public FourFragment(Context context) {
        this.context = context;
    }

    public FourFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_second, container, false);
        /*TextView textView = inflate.findViewById(R.id.lb_text);
        textView.setText("我是李白");
        Button button = inflate.findViewById(R.id.lb_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "我是李白", Toast.LENGTH_SHORT).show();
            }
        });*/
        return inflate;
    }

}
