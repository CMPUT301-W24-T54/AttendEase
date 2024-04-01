package com.example.attendease;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AttendeeAdapter extends RecyclerView.Adapter<AttendeeAdapter.AttendeeViewHolder> {

    private List<Attendee> attendeeList;
    private Context context;
    private OnItemClickListener onItemClickListener;
    private final Database database = Database.getInstance();
    private CollectionReference attendeesRef;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public AttendeeAdapter(Context context, List<Attendee> attendeeList) {
        this.context = context;
        this.attendeeList = attendeeList;
    }

    @NonNull
    @Override
    public AttendeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.attendee_card, parent, false);
        return new AttendeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendeeViewHolder holder, int position) {
        Attendee attendee = attendeeList.get(position);
        holder.bind(attendee);

        holder.trashButton.setOnClickListener(view -> {
            deleteAttendee(position);
        });

        holder.itemView.setOnClickListener(view -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(view, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return attendeeList.size();
    }

    public void setAttendeeList(List<Attendee> attendeeList) {
        this.attendeeList = attendeeList;
        notifyDataSetChanged();
    }

    private void deleteAttendee(int position) {
        String attendeeId = attendeeList.get(position).getDeviceID(); //Unsure?
        attendeesRef.document(attendeeId).delete().addOnSuccessListener(aVoid -> {
            attendeeList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, attendeeList.size());
        }).addOnFailureListener(e -> {
            Log.e("AttendeeAdapter", "Failed to delete attendee: " + e.getMessage());
        });
    }

    static class AttendeeViewHolder extends RecyclerView.ViewHolder {

        ImageView profilePhotoImageView;
        TextView usernameTextView;
        ImageView trashButton;

        AttendeeViewHolder(View itemView) {
            super(itemView);
            profilePhotoImageView = itemView.findViewById(R.id.imageView);
            usernameTextView = itemView.findViewById(R.id.textView14);
            trashButton = itemView.findViewById(R.id.trash);
        }

        void bind(Attendee attendee) {
            Glide.with(itemView.getContext()).load(attendee.getImage()).into(profilePhotoImageView);
            usernameTextView.setText(attendee.getName());
        }
    }
}
