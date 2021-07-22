package com.kamimi.lcalendar.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kamimi.lcalendar.databinding.FragmentDashboardBinding;
import com.kamimi.lcalendar.obj.DiaryPreview;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);

        RecyclerView diaryList = binding.diaryList;
        diaryList.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        diaryList.setLayoutManager(mLayoutManager);
        DiaryListAdapter adapter = new DiaryListAdapter(getContext(), diaryList);
        diaryList.setAdapter(adapter);

        adapter.mPreviews.add(new DiaryPreview("2021-7-22", "这是测试预览数据哟"));

        binding.diaryEditorShade.setOnClickListener(v -> binding.diaryEditor.setVisibility(View.GONE));
        binding.diaryEditorPanel.setOnClickListener(v -> {});
        binding.diarySubmitButton.setOnClickListener(v -> Toast.makeText(getContext(), "Hello World", Toast.LENGTH_SHORT).show());

        return binding.getRoot();
    }

}