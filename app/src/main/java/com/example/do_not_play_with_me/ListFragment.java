package com.example.do_not_play_with_me;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ListFragment extends Fragment implements QuizListAdapter.OnQuizListItemClicked {

    private NavController navController;
    private RecyclerView recyclerView;
    private QuizListViewModel quizListViewModel;

    private QuizListAdapter adapter;
    private ProgressBar list_progress_bar;

    private Animation fadeIn;
    private Animation fadeOut;


    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        recyclerView = view.findViewById(R.id.list_view);
        list_progress_bar = view.findViewById(R.id.list_progress);
        adapter = new QuizListAdapter(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true); //her item için fixed size ı olmalı
        recyclerView.setAdapter(adapter);

        fadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        fadeOut = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        quizListViewModel = new ViewModelProvider(getActivity()).get(QuizListViewModel.class); //ViewModel class a böyle erişebiliriz direkt olarak erişmeyiz
        //burada live data ya sahibiz artık

        quizListViewModel.getQuizListModelData().observe(getViewLifecycleOwner(), new Observer<List<QuizListModel>>() {
            @Override
            public void onChanged(List<QuizListModel> quizListModels) {
                //load Recycler view
                recyclerView.startAnimation(fadeIn);
                list_progress_bar.startAnimation(fadeOut);

                adapter.setQuizListModelList(quizListModels);
                adapter.notifyDataSetChanged(); //adapter ü notify etmek için data değişti diye
            }
        });
    }

    @Override
    public void onItemClicked(int position) {
        ListFragmentDirections.ActionListFragmentToDetailsFragment actionListFragmentToDetailsFragment = ListFragmentDirections.actionListFragmentToDetailsFragment();
        actionListFragmentToDetailsFragment.setPosition(position); //position ı onclicklistener dan aldık argümanı pass ediyoruz burada

        navController.navigate(actionListFragmentToDetailsFragment);

    }
}
