package com.utildev.arch.architecturecomponents.presentation.fragment.room;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.utildev.arch.architecturecomponents.R;
import com.utildev.arch.architecturecomponents.data.room.model.Github;
import com.utildev.arch.architecturecomponents.data.room.model.UserEntity;
import com.utildev.arch.architecturecomponents.databinding.FragmentRoomBinding;
import com.utildev.arch.architecturecomponents.presentation.BaseAdapter;
import com.utildev.arch.architecturecomponents.presentation.fragment.BaseFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class RoomFragment extends BaseFragment implements BaseAdapter.AdapterListener, View.OnClickListener {
    private FragmentRoomBinding binding;
    private RoomViewModel viewModel;

    private List<UserEntity> userList;
    private BaseAdapter<UserEntity> adapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_room, container, false);
        View view = binding.getRoot();
        viewModel = ViewModelProviders.of(this).get(RoomViewModel.class);
        binding.setViewModel(viewModel);
        init();
        registerLiveData();
        return view;
    }

    private void init() {
        userList = new ArrayList<>();
        LinearLayoutManager layoutManager = new GridLayoutManager(getContext(), 1, LinearLayoutManager.VERTICAL, false);
        adapter = new BaseAdapter<>(binding.fragRoomIncludeList.viewListRvContent, null, R.layout.item_room);
        adapter.setAdapterListener(this);
        binding.fragRoomIncludeList.viewListRvContent.setLayoutManager(layoutManager);
        binding.fragRoomIncludeList.viewListRvContent.setAdapter(adapter);

        binding.fragRoomIncludeList.viewListSrLayout.setOnRefreshListener(() -> {
            userList.clear();
            adapter.set(userList);
            viewModel.getAllUser();
            binding.fragRoomIncludeList.viewListSrLayout.setRefreshing(false);
        });

        viewModel.getAllUser();

        viewModel.getTotalUserCount();

        binding.fragRoomFab.setOnClickListener(this);
    }

    private void registerLiveData() {
        viewModel.getUserLiveData().observe(this, this::userEntityListener);
        viewModel.getUserCount().observe(this, integer ->
                binding.fragRoomTvCount.setText(String.valueOf(integer)));
    }

    private void userEntityListener(List<UserEntity> userEntities) {
        if (userEntities != null) {
            userList = userEntities;
            adapter.set(userList);
            if (userEntities.size() == 0) {
                binding.fragRoomTvCount.setText("Empty!");
            }
        }
    }

    @Override
    public void onItemClick(Object object) {
        if (object instanceof UserEntity) {
            viewModel.deleteUser((UserEntity) object);
        }
    }

    @Override
    public boolean onItemLongClick(Object object) {
        if (object instanceof UserEntity) {
            viewModel.deleteUser((UserEntity) object);
        }
        return false;
    }

    @Override
    public void onLoadMore() {
    }

    @Override
    public void onClick(View view) {
        viewModel.insertUser(new UserEntity(0, "Architecture Components", "Android Development"));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Github github) {
        Toast.makeText(getContext(), "room event", Toast.LENGTH_SHORT).show();
    }
}
