package com.example.do_not_play_with_me;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class QuizListAdapter extends RecyclerView.Adapter<QuizListAdapter.QuizViewHolder> {

    private List<QuizListModel> quizListModelList;
    private OnQuizListItemClicked onQuizListItemClicked;

    public QuizListAdapter(OnQuizListItemClicked onQuizListItemClicked){
        this.onQuizListItemClicked= onQuizListItemClicked;
    }

    public void setQuizListModelList(List<QuizListModel> quizListModelList) {
        this.quizListModelList = quizListModelList;
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_list_item, parent, false);
        return new QuizViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {

        String image_url = quizListModelList.get(position).getImage();
        Glide
                .with(holder.itemView.getContext())
                .load(image_url)
                .centerCrop()
                .placeholder(R.drawable.placeholder_image)
                .into(holder.listImageView);

        holder.listTitle.setText(quizListModelList.get(position).getName());

        String list_description = quizListModelList.get(position).getDescription();
        if(list_description.length() > 150){

            list_description = list_description.substring(0,150).concat("...");
        }
        holder.listDesc.setText(list_description);

        holder.listLevel.setText(quizListModelList.get(position).getLevel());



    }

    @Override
    public int getItemCount() {
       if(quizListModelList == null) //ilk etapta boş olabilir o zaman null döner error verir
       {
           return 0;
       } else {
           return quizListModelList.size(); //recycler view daki item sayısını getirir bu
       }
    }

    public class QuizViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener { //view da present edilen itemları initialize ediyoruz
        private ImageView listImageView;
        private TextView listTitle;
        private TextView listDesc;
        private TextView listLevel;
        private TextView listBtn;

        public QuizViewHolder(@NonNull  View itemView) {
            super(itemView);
            listImageView = itemView.findViewById(R.id.list_image);
            listTitle = itemView.findViewById(R.id.list_title);
            listDesc = itemView.findViewById(R.id.list_desc);
            listLevel = itemView.findViewById(R.id.list_difficulty);
            listBtn = itemView.findViewById(R.id.list_btn);
            listBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onQuizListItemClicked.onItemClicked(getAdapterPosition());
        }
    }

    public interface OnQuizListItemClicked{
        void onItemClicked(int position);
    }
}
