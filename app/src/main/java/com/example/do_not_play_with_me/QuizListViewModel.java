package com.example.do_not_play_with_me;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class QuizListViewModel extends ViewModel implements FirebaseRepository.OnFirestoreTaskComplete {

    private MutableLiveData<List<QuizListModel>> quizListModelData = new MutableLiveData<>();
    private FirebaseRepository firebaseRepository = new FirebaseRepository(this);

    public LiveData<List<QuizListModel>> getQuizListModelData() {
        return quizListModelData;
    }


    public QuizListViewModel()
    {
        firebaseRepository.getQuizData();
    }
    @Override
    public void quizListDataAdded(List<QuizListModel> quizListModelList) {
        // gelen dataşaro quizListModelList listesine ekleyeceğimqu
        quizListModelData.setValue(quizListModelList);


    }

    @Override
    public void onError(Exception e) {

    }
}
