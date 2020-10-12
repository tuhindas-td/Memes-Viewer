package com.tuhindas.memesviewer;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.snackbar.Snackbar;
import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Intent.ACTION_SEND;

public class MainActivity extends AppCompatActivity {

    ConstraintLayout constraintLayout;
    ProgressBar progressBar;
    ImageView memeImage;
    String url = "https://meme-api.herokuapp.com/gimme";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        constraintLayout = findViewById(R.id.constraintLayout);
        progressBar = findViewById(R.id.progressBar);
        memeImage = findViewById(R.id.memeImage);

        loadMeme();
    }

    private void loadMeme() {

        progressBar.setVisibility(View.VISIBLE);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String imageUrl = new JSONObject(response).getString("url");
                    Glide.with(MainActivity.this).load(imageUrl).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }
                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    }).into(memeImage);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this,"Something went wrong", Toast.LENGTH_SHORT).show();
                Snackbar.make(constraintLayout, "No connection", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Retry", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                loadMeme();
                            }
                        }).setActionTextColor(getResources().getColor(R.color.colorAccent)).show();
            }
        });
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    public void shareMeme(View view) {
        Intent intent = new Intent(ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "Hey, Checkout this cool meme");
        Intent chooser = Intent.createChooser(intent, "Share this meme using");
        startActivity(chooser);
    }

    public void nextMeme(View view) {
        loadMeme();
    }
}