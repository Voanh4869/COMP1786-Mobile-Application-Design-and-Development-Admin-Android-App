package com.example.expensetrackerapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetrackerapp.R;
import com.example.expensetrackerapp.models.Project;

import java.util.List;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ViewHolder> {

    private List<Project> projectList;
    private OnProjectClickListener listener;

    public interface OnProjectClickListener {
        void onEditClick(Project project);
        void onDeleteClick(Project project);
        void onProjectClick(Project project);
    }

    public ProjectAdapter(List<Project> projectList, OnProjectClickListener listener) {
        this.projectList = projectList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_project, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Project project = projectList.get(position);

        holder.tvName.setText(project.getName());
        holder.tvDescription.setText(project.getDescription());
        holder.tvDates.setText("Dates: " + project.getStartDate() + " - " + project.getEndDate());
        holder.tvManager.setText("Manager: " + project.getManager());
        holder.tvBudget.setText("Budget: $" + project.getBudget());
        holder.tvStatus.setText("Status: " + project.getStatus());

        holder.itemView.setOnClickListener(v -> listener.onProjectClick(project));
        holder.btnEdit.setOnClickListener(v -> listener.onEditClick(project));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(project));
    }

    @Override
    public int getItemCount() {
        return projectList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDescription, tvDates, tvManager, tvBudget, tvStatus;
        ImageButton btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvDates = itemView.findViewById(R.id.tvDates);
            tvManager = itemView.findViewById(R.id.tvManager);
            tvBudget = itemView.findViewById(R.id.tvBudget);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}