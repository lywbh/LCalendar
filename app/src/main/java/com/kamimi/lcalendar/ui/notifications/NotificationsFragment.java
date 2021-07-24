package com.kamimi.lcalendar.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.kamimi.lcalendar.databinding.FragmentNotificationsBinding;
import com.kamimi.lcalendar.utils.FontLoader;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);

        NotificationsViewModel notificationsViewModel = new ViewModelProvider(this).get(NotificationsViewModel.class);
        notificationsViewModel.getText().observe(getViewLifecycleOwner(), binding.textNotifications::setText);

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        binding.textNotifications.setTypeface(FontLoader.ldzsFont);
    }

}