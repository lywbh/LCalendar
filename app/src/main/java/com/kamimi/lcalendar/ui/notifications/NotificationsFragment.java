package com.kamimi.lcalendar.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.kamimi.lcalendar.databinding.FragmentNotificationsBinding;
import com.stone.pile.libs.PileLayout;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);

        PileLayout.Adapter adapter = new NotificationListAdapter();
        binding.pileLayout.setAdapter(adapter);

        return binding.getRoot();
    }

}