package com.example.attendease;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.attendease.R;
import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder> {

    static final int TYPE_LARGE = 0;
    static final int TYPE_SMALL = 1;

    private List<Profile> profileList;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public ProfileAdapter(Context context, List<Profile> profileList) {
        this.context = context;
        this.profileList = profileList;
    }

    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_card_small, parent, false);
        return new ProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder holder, int position) {
        Profile profile = profileList.get(position);
        holder.bind(profile);

        holder.itemView.setOnClickListener(view -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(view, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return profileList.size();
    }

    public void setProfileList(List<Profile> profileList) {
        this.profileList = profileList;
    }

    static class ProfileViewHolder extends RecyclerView.ViewHolder {

        ImageView profilePhotoImageView;
        TextView usernameTextView;

        ProfileViewHolder(View itemView) {
            super(itemView);
            profilePhotoImageView = itemView.findViewById(R.id.imageView);
            usernameTextView = itemView.findViewById(R.id.textView14);
        }

        void bind(Profile profile) {
            // Load profile photo into the ImageView (profilePhotoImageView)
            // Set username in the TextView (usernameTextView)
        }
    }
}
