package com.example.attendancestudent.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendancestudent.Auth.Model.ModelAttend;
import com.example.attendancestudent.R;
import com.joooonho.SelectableRoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class Adapter_All_Student extends RecyclerView.Adapter<Adapter_All_Student.StudentViewHolder> {

    private Context mContext;
    private List<ModelAttend> modelStudentList;
    private OnItemClickListener mListener;
    private int NumOfLecture = 0;

    public Adapter_All_Student(Context mContext, List<ModelAttend> modelStudentList, int numOfLecture) {
        this.mContext = mContext;
        this.modelStudentList = modelStudentList;
        NumOfLecture = numOfLecture;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_student, parent, false);
        return new StudentViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {

        ModelAttend student = modelStudentList.get(position);

        holder.studentName.setText(student.getStudentName());
        if (NumOfLecture != 0) {
            double sNum = student.getNumOfAttend();
            double pNum = NumOfLecture;
            double result = (sNum / pNum) * 100;
            int R = (int) result;
            holder.studentAttend.setText(R + " %");
        } else {
            holder.studentAttend.setText("");
        }

        if (student.getStudentImageUri().trim() != null) {
            Picasso.get().load(student.getStudentImageUri().trim()).into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return modelStudentList.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public interface OnItemClickListener {
        void onItem_Student_Click(int position);
    }

    public class StudentViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        public SelectableRoundedImageView imageView;
        public TextView studentName, studentAttend;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.itemImage);
            studentName = itemView.findViewById(R.id.itemStudentName);
            studentAttend = itemView.findViewById(R.id.itemStudentAttend);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onItem_Student_Click(position);
                }
            }
        }
    }
}
