package com.example.learningplatform;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.learn.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class QuizManageAdapter extends RecyclerView.Adapter<QuizManageAdapter.MyViewHolder> {
    public LinearLayout v;
    public Context ctx;
    private JSONArray mDataset;

    // Provide a suitable constructor (depends on the kind of dataset)
    public QuizManageAdapter(Context context, JSONArray myDataset) {
        super();
        mDataset = myDataset;
        ctx = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public QuizManageAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {
        // create a new view
        v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.quiz_recycler, parent, false);
        QuizManageAdapter.MyViewHolder vh = new QuizManageAdapter.MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final QuizManageAdapter.MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        try {
            JSONObject currentObj = mDataset.getJSONObject(position);
            holder.id.setText(currentObj.getString("id"));
            holder.question.setText(currentObj.getString("question"));
            holder.options.setText(currentObj.getString("options"));
            holder.answer.setText(currentObj.getString("answer"));
            holder.lnlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent  = new Intent(ctx,UpdateQuizActivity.class);

                    try {
                        intent.putExtra("id",currentObj.getString("id"));
                        intent.putExtra("module",currentObj.getString("module"));
                        intent.putExtra("question",currentObj.getString("question"));
                        intent.putExtra("options",currentObj.getString("options"));
                        intent.putExtra("answer",currentObj.getString("answer"));
                        ctx.startActivity(intent);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                }
            });

        } catch (JSONException ex) {

        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView id,question,answer,options;
        public LinearLayout lnlayout;

        public MyViewHolder(LinearLayout lny) {
            super(lny);
            lnlayout = lny.findViewById(R.id.lnyLayout);
            id = lny.findViewById(R.id.tvId);
            question = lny.findViewById(R.id.tvQuestion);
            answer = lny.findViewById(R.id.tvAnswer);
            options = lny.findViewById(R.id.tvOptions);

        }
    }
}