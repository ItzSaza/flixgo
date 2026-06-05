package com.example.flixgo;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<Movie> movieList;

    public MovieAdapter(List<Movie> movieList) {
        this.movieList = movieList;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movieList.get(position);
        holder.tvTitle.setText(movie.getTitle());
        holder.tvReleaseDate.setText(movie.getReleaseDate());
        holder.tvRating.setText(String.valueOf(movie.getRating()));

        String poster = movie.getPoster();
        if (poster != null && !poster.isEmpty()) {
            String posterUrl = "https://image.tmdb.org/t/p/w500" + poster;
            Glide.with(holder.itemView.getContext())
                    .load(posterUrl)
                    .placeholder(R.drawable.applogo)
                    .error(R.drawable.applogo)
                    .into(holder.imgPoster);
        } else {
            holder.imgPoster.setImageResource(R.drawable.applogo);
        }

        // Handle navigation to details
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), MovieDetailsActivity.class);
            intent.putExtra("movie", movie);
            v.getContext().startActivity(intent);
        });

        // Show/Handle delete button only in FavoritesActivity
        if (holder.itemView.getContext() instanceof FavoritesActivity) {
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnDelete.setOnClickListener(v -> {
                int currentPos = holder.getAdapterPosition();
                if (currentPos == RecyclerView.NO_POSITION) return;



                // Firestore removal (optional sync)
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                firestore.collection("favorites")
                        .whereEqualTo("title", movie.getTitle())
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            queryDocumentSnapshots.getDocuments().forEach(doc -> doc.getReference().delete());
                        });

                movieList.remove(currentPos);
                notifyItemRemoved(currentPos);
                notifyItemRangeChanged(currentPos, movieList.size());
                
                Toast.makeText(v.getContext(), "Removed from Favorites", Toast.LENGTH_SHORT).show();
            });
        } else {
            holder.btnDelete.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    static class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPoster;
        TextView tvTitle, tvReleaseDate, tvRating;
        ImageButton btnDelete;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPoster = itemView.findViewById(R.id.imgPoster);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvReleaseDate = itemView.findViewById(R.id.tvReleaseDate);
            tvRating = itemView.findViewById(R.id.tvRating);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
