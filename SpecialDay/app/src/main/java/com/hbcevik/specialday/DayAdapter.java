package com.hbcevik.specialday;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hbcevik.specialday.databinding.RecycleBinding;

import java.util.ArrayList;

public class DayAdapter extends RecyclerView.Adapter<DayAdapter.DayHolder> {
    ArrayList<Day> dayArrayList;

    public DayAdapter(ArrayList<Day>dayArrayList){
        this.dayArrayList=dayArrayList;
    }
    @NonNull
    @Override
    public DayHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecycleBinding recycleBinding = RecycleBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new DayHolder(recycleBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull DayHolder holder, int position) {
    holder.binding.recycleViewTextView.setText(dayArrayList.get(position).event);
    holder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(holder.itemView.getContext(),MainActivity2.class);
            intent.putExtra("info","old");
            intent.putExtra("dayId",dayArrayList.get(position).id);
            holder.itemView.getContext().startActivity(intent);
        }
    });
    }

    @Override
    public int getItemCount() {
        return dayArrayList.size();
    }

    public class DayHolder extends RecyclerView.ViewHolder{
        private RecycleBinding binding;
        public DayHolder(RecycleBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
