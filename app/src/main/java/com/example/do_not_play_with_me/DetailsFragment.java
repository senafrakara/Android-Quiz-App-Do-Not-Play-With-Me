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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.List;

public class DetailsFragment extends Fragment implements View.OnClickListener {
    private NavController navController;
    private QuizListViewModel quizListViewModel; //to reetrieve data
    private int position;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    private ImageView details_image;
    private TextView details_title;
    private TextView details_description;
    private TextView details_difficulty;
    private TextView details_question_num;
    private TextView detailsScore;


    private Button details_start_btn;
    private String quizId;
    private long totalQuestions = 0;

    private String quizName;

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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        position = DetailsFragmentArgs.fromBundle(getArguments()).getPosition(); //list fragmenttan gelen actionda argümanı geçirdik
        //argüman detail fragment ın, position argümanı, getPosition ile onu getiriyoruz

        details_image = view.findViewById(R.id.details_image);
        details_title = view.findViewById(R.id.details_title);
        details_description = view.findViewById(R.id.details_desc);
        details_difficulty = view.findViewById(R.id.details_difficulty_text);
        details_question_num = view.findViewById(R.id.details_questions_text);
        detailsScore = view.findViewById(R.id.details_score_text);

        details_start_btn = view.findViewById(R.id.details_start_btn);
        details_start_btn.setOnClickListener(this);

        //Load Previous Results
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
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

                //assign the value to quizId variable
                quizId = quizListModelList.get(position).getQuiz_id();
                totalQuestions = quizListModelList.get(position).getQuestions();
                quizName = quizListModelList.get(position).getName();

                //Load Results Data
                loadResultsData();

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.details_start_btn:
                DetailsFragmentDirections.ActionDetailsFragmentToQuizFragment actionDetailsFragmentToQuizFragment = DetailsFragmentDirections.actionDetailsFragmentToQuizFragment();
                actionDetailsFragmentToQuizFragment.setTotalQuestions(totalQuestions); //position ı onclicklistener dan aldık argümanı pass ediyoruz burada
                actionDetailsFragmentToQuizFragment.setQuizId(quizId);
                actionDetailsFragmentToQuizFragment.setQuizName(quizName);
                navController.navigate(actionDetailsFragmentToQuizFragment);
                break;
        }
    }

    private void loadResultsData() {
        firebaseFirestore.collection("QuizList")
                .document(quizId).collection("Results")
                .document(firebaseAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        //Get Result
                        Long correct = document.getLong("correct");
                        Long wrong = document.getLong("wrong");
                        Long missed = document.getLong("unanswered");

                        //Calculate Progress
                        Long total = correct + wrong + missed;
                        Long percent = (correct * 100) / total;

                        detailsScore.setText(percent + "%");
                    } else {
                        //Document Doesn't Exist, and result should stay N/A
                    }
                }
            }
        });
    }
}
