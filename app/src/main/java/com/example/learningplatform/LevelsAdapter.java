package com.example.learningplatform;


import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.learn.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LevelsAdapter extends RecyclerView.Adapter<LevelsAdapter.MyViewHolder> {
    public LinearLayout v;
    public Context ctx;
    private JSONArray mDataset;

    // Provide a suitable constructor (depends on the kind of dataset)
    public LevelsAdapter(Context context, JSONArray myDataset) {
        super();
        mDataset = myDataset;
        ctx = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public LevelsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
        // create a new view
        v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_levels, parent, false);
        LevelsAdapter.MyViewHolder vh = new LevelsAdapter.MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final LevelsAdapter.MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        try {
            JSONObject currentObj = mDataset.getJSONObject(position);
            holder.id.setText(currentObj.getString("id"));
            holder.name.setText(currentObj.getString("name"));

            if(currentObj.getString("icon").startsWith("http"))
                Glide.with(ctx).load(currentObj.getString("icon")).into(holder.icon);
            else
                Glide.with(ctx).load(Utils.host+"/images/"+currentObj.getString("icon")).into(holder.icon);
            holder.lnlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ctx,ModulesActivity.class);
                    try {
                        intent.putExtra("id",currentObj.getString("id"));
                        intent.putExtra("name",currentObj.getString("name"));
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
        public TextView id,name;
        public ImageView icon;
        public LinearLayout lnlayout;

        public MyViewHolder(LinearLayout lny) {
            super(lny);
            lnlayout = lny.findViewById(R.id.lnyLayout);
            id = lny.findViewById(R.id.tvId);
            name = lny.findViewById(R.id.tvLevel);
            icon = lny.findViewById(R.id.icon);

        }
    }
}