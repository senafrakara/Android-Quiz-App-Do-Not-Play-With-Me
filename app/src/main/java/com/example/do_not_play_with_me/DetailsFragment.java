package com.example.do_not_play_with_me;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.List;

public class DetailsFragment extends Fragment implements View.OnClickListener {
    private NavController navController;
    private QuizListViewModel quizListViewModel; //to reetrieve data
    private int position;

    private ImageView details_image;
    private TextView details_title;
    private TextView details_description;
    private TextView details_difficulty;
    private TextView details_question_num;

    private Button details_start_btn;

    public DetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        position = DetailsFragmentArgs.fromBundle(getArguments()).getPosition(); //list fragmenttan gelen actionda argümanı geçirdik
        //argüman detail fragment ın, position argümanı, getPosition ile onu getiriyoruz

        details_image = view.findViewById(R.id.details_image);
        details_title = view.findViewById(R.id.details_title);
        details_description = view.findViewById(R.id.details_desc);
        details_difficulty = view.findViewById(R.id.details_difficulty_text);
        details_question_num = view.findViewById(R.id.details_questions_text);

        details_start_btn = view.findViewById(R.id.details_start_btn);
        details_start_btn.setOnClickListener(this);

    }

    @Override
    public void onActivityCreated(@Nullable  Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        quizListViewModel = new ViewModelProvider(getActivity()).get(QuizListViewModel.class);

        quizListViewModel.getQuizListModelData().observe(getViewLifecycleOwner(), new Observer<List<QuizListModel>>() {

            @Override
            public void onChanged(List<QuizListModel> quizListModelList) {

                Glide.with(getContext())
                        .load(quizListModelList.get(position).getImage())
                        .centerCrop()
                        .placeholder(R.drawable.placeholder_image)
                        .into(details_image);
                details_title.setText(quizListModelList.get(position).getName());
                details_description.setText(quizListModelList.get(position).getDescription());
                details_difficulty.setText(quizListModelList.get(position).getLevel());
                details_question_num.setText(quizListModelList.get(position).getQuestions() + "");
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.details_start_btn:
                DetailsFragmentDirections.ActionDetailsFragmentToQuizFragment actionDetailsFragmentToQuizFragment = DetailsFragmentDirections.actionDetailsFragmentToQuizFragment();
                actionDetailsFragmentToQuizFragment.setPosition(position); //position ı onclicklistener dan aldık argümanı pass ediyoruz burada
                navController.navigate(actionDetailsFragmentToQuizFragment);
                break;
        }
    }
}
