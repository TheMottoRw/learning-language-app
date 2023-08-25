package com.example.learningplatform;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.learn.R;

import java.util.ArrayList;

public class ProfileAdapter extends ArrayAdapter<ModuleModel> {
    private Context ctx;

    public ProfileAdapter(@NonNull Context context, ArrayList<ModuleModel> moduleModelArrayList) {
        super(context, 0, moduleModelArrayList);
        ctx = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listitemView = convertView;
        if (listitemView == null) {
            // Layout Inflater inflates each item to be displayed in GridView.
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.card_profile_item, parent, false);
        }

        ModuleModel moduleModel = getItem(position);
        TextView moduleName = listitemView.findViewById(R.id.moduleName);
        ImageView moduleIcon = listitemView.findViewById(R.id.moduleIcon);
        TextView moduleLockIcon = listitemView.findViewById(R.id.moduleLockIcon);
        TextView moduleUnlockIcon = listitemView.findViewById(R.id.moduleUnlockIcon);
        TextView modulePoints = listitemView.findViewById(R.id.modulePoints);

        moduleName.setText(moduleModel.getModuleName());
        modulePoints.setText("Amanota "+moduleModel.getMarks());

        if(moduleModel.getImage().startsWith("http"))
            Glide.with(ctx).load(moduleModel.getImage()).into(moduleIcon);
        else
            Glide.with(ctx).load(Utils.host+"/images/"+moduleModel.getImage()).into(moduleIcon);


        return listitemView;
    }
}