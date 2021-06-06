package com.example.do_not_play_with_me;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class QuizFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "QUIZ_FRAGMENT_LOG";

    private NavController navController;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private String currentUserId;

    private String quizName;
    private String quizId;

    private TextView quizTitle;
    private Button optionOneBtn;
    private Button optionTwoBtn;
    private Button optionThreeBtn;
    private Button nextBtn;
    private ImageButton closeBtn;
    private TextView questionFeedback;
    private TextView questionText;
    private TextView questionTime;
    private ProgressBar questionProgress;
    private TextView questionNumber;


    private List<QuestionsModel> allQuestionsList = new ArrayList<>();
    private List<QuestionsModel> questionsToAnswer = new ArrayList<>();
    //seçilen soruları tutacak ki o da 10 soru
    //db deki örneğin 20 sorudan 10 tanesi seçilip buna atılır
    private long totalQuestionsToAnswer = 0L;
    private CountDownTimer countDownTimer;

    private boolean canAnswer = false; // zaman dolduğunda kullanıcıdan cevap almamak için
    private int currentQuestion = 0; // mevcut soru numarası

    private int correctAnswers = 0;
    private int wrongAnswers = 0;
    private int notAnswered = 0;

    public QuizFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_quiz, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        firebaseAuth = FirebaseAuth.getInstance();

        //Get User ID
        if(firebaseAuth.getCurrentUser() != null){
            currentUserId = firebaseAuth.getCurrentUser().getUid();
        } else {
            //Go Back to Home Page
        }

        firebaseFirestore = FirebaseFirestore.getInstance();

        quizTitle = view.findViewById(R.id.quiz_title);
        optionOneBtn = view.findViewById(R.id.quiz_option_one);
        optionTwoBtn = view.findViewById(R.id.quiz_option_two);
        optionThreeBtn = view.findViewById(R.id.quiz_option_three);
        nextBtn = view.findViewById(R.id.quiz_next_btn);
        questionFeedback = view.findViewById(R.id.quiz_question_feedback);
        questionText = view.findViewById(R.id.quiz_question);
        questionTime = view.findViewById(R.id.quiz_question_time);
        questionProgress = view.findViewById(R.id.quiz_question_progress);
        questionNumber = view.findViewById(R.id.quiz_question_number);

        //get quizId
        quizId = QuizFragmentArgs.fromBundle(getArguments()).getQuizId();
        quizName = QuizFragmentArgs.fromBundle(getArguments()).getQuizName();
        totalQuestionsToAnswer = QuizFragmentArgs.fromBundle(getArguments()).getTotalQuestions();

        //Get all questions from the quiz
        firebaseFirestore.collection("QuizList")
                .document(quizId).collection("Questions").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            allQuestionsList = task.getResult().toObjects(QuestionsModel.class);
                            // Log.d(TAG,"Questions List: " + allQuestionsList.get(0).getQuestion());
                            pickQuestions();
                            loadUI();
                        } else {
                            //error getting questions
                            quizTitle.setText("Error: " + task.getException().getMessage());
                        }
                    }
                });
        //   Set Button Click Listeners
        optionOneBtn.setOnClickListener(this);
        optionTwoBtn.setOnClickListener(this);
        optionThreeBtn.setOnClickListener(this);

        nextBtn.setOnClickListener(this);

    }

    private void loadUI() {
        //Quiz Data Loaded, Load the UI
        quizTitle.setText(quizName);
        questionText.setText("Load First Question");

        //Enable Options
        enableOptions(); //to enable answer button

        //Load First Question
        loadQuestion(1);

    }

    private void loadQuestion(int questionNum) {
        //Set Question Number
        questionNumber.setText(questionNum + "");

        //Load Question Text
        questionText.setText(questionsToAnswer.get(questionNum - 1).getQuestion());

        //Load Options
        optionOneBtn.setText(questionsToAnswer.get(questionNum - 1).getOption_a());
        optionTwoBtn.setText(questionsToAnswer.get(questionNum - 1).getOption_b());
        optionThreeBtn.setText(questionsToAnswer.get(questionNum - 1).getOption_c());

        //Question Loaded, Set Can Answer
        canAnswer = true;
        currentQuestion = questionNum;

        // Start Question Timer
        startTimer(questionNum);
    }

    private void startTimer(int questionNumber) {

        //Set Timer Text
        final Long timeToAnswer = questionsToAnswer.get(questionNumber - 1).getTimer(); // sorunun süresini getiriyoruz
        questionTime.setText(timeToAnswer.toString());

        //Show Timer ProgressBar
        questionProgress.setVisibility(View.VISIBLE);

        //Start CountDown
        countDownTimer = new CountDownTimer(timeToAnswer * 1000, 10) {
            ;// bu değerler milisaniye cinsinde

            @Override
            public void onTick(long millisUntilFinished) { // bu metotlar CountDownTimer ın metotları zorunlu olarak implement edilir
                // kalan zamanı hesaplar ve questionTime a assign ederiz bu zamanı
                //Update Time
                questionTime.setText(millisUntilFinished / 1000 + "");

                //Progress in percent
                Long percent = millisUntilFinished / (timeToAnswer * 10);
                questionProgress.setProgress(percent.intValue());
            }

            @Override
            public void onFinish() {
                //Time Up, Cannot Answer Question Anymore
                canAnswer = false;

                questionFeedback.setText("Time Up! No answer was submitted.");
                questionFeedback.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null));
                notAnswered++;
                showNextBtn();
            }
        };

        countDownTimer.start();
    }

    private void enableOptions() {
        //Show All Option Buttons
        optionOneBtn.setVisibility(View.VISIBLE);
        optionTwoBtn.setVisibility(View.VISIBLE);
        optionThreeBtn.setVisibility(View.VISIBLE);

        //Enable Option Buttons
        optionOneBtn.setEnabled(true);
        optionTwoBtn.setEnabled(true);
        optionThreeBtn.setEnabled(true);

        //Hide Feedback and next Button
        questionFeedback.setVisibility(View.INVISIBLE);
        nextBtn.setVisibility(View.INVISIBLE);
        nextBtn.setEnabled(false);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.quiz_option_one:
                verifyAnswer(optionOneBtn);
                break;
            case R.id.quiz_option_two:
                verifyAnswer(optionTwoBtn);
                break;
            case R.id.quiz_option_three:
                verifyAnswer(optionThreeBtn);
                break;
            case R.id.quiz_next_btn:
                if (currentQuestion == totalQuestionsToAnswer) {
                    //Load Results
                    submitResults();
                } else {
                    currentQuestion++;
                    loadQuestion(currentQuestion);
                    resetOptions();
                }
                break;
        }
    }

    private void verifyAnswer(Button selectedAnswerBtn) {
        //Check Answer
        if (canAnswer) {
            //Set Answer Btn Text Color to Black
            selectedAnswerBtn.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorDark, null));

            if (questionsToAnswer.get(currentQuestion - 1).getAnswer().equals(selectedAnswerBtn.getText())) {
                //Correct Answer
                correctAnswers++;
                Log.d(TAG, "Correct Answer!");
                selectedAnswerBtn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.correct_answer_btn_bg, null));

                //Set Feedback Text
                questionFeedback.setText("Correct Answer");
                questionFeedback.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null));
            } else {
                //Wrong Answer
                wrongAnswers++;

                Log.d(TAG, "Wrong Answer!");
                selectedAnswerBtn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.wrong_answer_btn_bg, null));

                //Set Feedback Text
                questionFeedback.setText("Wrong Answer \n \n Correct Answer : " + questionsToAnswer.get(currentQuestion - 1).getAnswer());
                questionFeedback.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent, null));
            }
            //Set Can answer to false
            canAnswer = false;

            //Stop The Timer
            countDownTimer.cancel();

            //Show Next Button
            showNextBtn();
        }
    }

    private void submitResults() {
        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("correct", correctAnswers);
        resultMap.put("wrong", wrongAnswers);
        resultMap.put("unanswered", notAnswered);

        firebaseFirestore.collection("QuizList")
                .document(quizId).collection("Results")
                .document(currentUserId).set(resultMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //Go To Results Page
                   QuizFragmentDirections.ActionQuizFragmentToResultFragment actionQuizFragmentToResultFragment = QuizFragmentDirections.actionQuizFragmentToResultFragment();
                    actionQuizFragmentToResultFragment.setQuizId(quizId);
                    navController.navigate(actionQuizFragmentToResultFragment);
                } else {
                    //Show Error
                    quizTitle.setText(task.getException().getMessage());
                }
            }
        });
    }

    private void resetOptions() {
        optionOneBtn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.outline_light_btn_bg, null));
        optionTwoBtn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.outline_light_btn_bg, null));
        optionThreeBtn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.outline_light_btn_bg, null));

        optionOneBtn.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorLightText, null));
        optionTwoBtn.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorLightText, null));
        optionThreeBtn.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorLightText, null));

        questionFeedback.setVisibility(View.INVISIBLE);
        nextBtn.setVisibility(View.INVISIBLE);
        nextBtn.setEnabled(false);
    }


    private void showNextBtn() {
        if (currentQuestion == totalQuestionsToAnswer) { //bunlar question numaraları
            nextBtn.setText("Submit Results");
        }
        questionFeedback.setVisibility(View.VISIBLE);
        nextBtn.setVisibility(View.VISIBLE);
        nextBtn.setEnabled(true);
    }


    private void pickQuestions() {
        for (int i = 0; i < totalQuestionsToAnswer; i++) {
            int randomNum = getRandomInteger(0, allQuestionsList.size());
            questionsToAnswer.add(allQuestionsList.get(randomNum));//bu total picked questions array i
            allQuestionsList.remove(randomNum); // aynı soruyu bir daha getirmemek için seçtiğimizi tüm sorular listesinden siliyoruz
            Log.d("QUESTIONS LOG: ", "Question: " + i + questionsToAnswer.get(i).getQuestion());

        }

    }

    public static int getRandomInteger(int minimum, int maximum) {
        return ((int) (Math.random() * (maximum - minimum))) + minimum; // herzaman bu iki sayı arasında random bi sayı atar
    }
}
