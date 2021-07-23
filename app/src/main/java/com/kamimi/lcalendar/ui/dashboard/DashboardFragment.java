package com.kamimi.lcalendar.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kamimi.lcalendar.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);

        binding.diaryList.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        binding.diaryList.setLayoutManager(mLayoutManager);
        DiaryListAdapter adapter = new DiaryListAdapter(getContext(), binding.diaryList, binding.diaryDetail);
        binding.diaryList.setAdapter(adapter);

        binding.diaryDetail.setOnClickListener(v -> {
            // 屏蔽掉弹出层的点击事件
        });

        // 展示日记列表内容
        adapter.reloadDiaryList();

        return binding.getRoot();
    }

}