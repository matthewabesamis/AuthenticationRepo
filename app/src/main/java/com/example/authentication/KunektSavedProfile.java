package com.example.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class KunektSavedProfile extends Fragment {
    Button editProfile;
    TextView outputAbout;
    TextView outputJob;
    TextView outputCompany;
    TextView outputSchool;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_saved_profile, container, false);
        editProfile = view.findViewById(R.id.button_edit_profile);
        outputAbout = view.findViewById(R.id.textView_about);
        outputJob = view.findViewById(R.id.textView_job);
        outputCompany = view.findViewById(R.id.textView_company);
        outputSchool = view.findViewById(R.id.textView_school);

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Kunekt_Main_Home kunekt_main_home = (Kunekt_Main_Home) getActivity();
                kunekt_main_home.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new KunektProfile()).commit();
            }
        });


        return  view;
    }

    public void setBio(String about, String jobTitle) {
        outputAbout.setText(about);
        outputJob.setText(jobTitle);
    }
}
