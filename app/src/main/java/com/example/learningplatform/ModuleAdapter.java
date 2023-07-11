package com.example.learningplatform;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.learn.R;

import java.util.ArrayList;

public class ModuleAdapter extends ArrayAdapter<ModuleModel> {
    private Context ctx;

    public ModuleAdapter(@NonNull Context context, ArrayList<ModuleModel> moduleModelArrayList) {
        super(context, 0, moduleModelArrayList);
        ctx = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listitemView = convertView;
        if (listitemView == null) {
            // Layout Inflater inflates each item to be displayed in GridView.
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.card_item, parent, false);
        }

        ModuleModel moduleModel = getItem(position);
        TextView moduleName = listitemView.findViewById(R.id.moduleName);
        ImageView moduleIcon = listitemView.findViewById(R.id.moduleIcon);

        moduleName.setText(moduleModel.getModuleName());

        if(moduleModel.getImage().startsWith("http"))
            Glide.with(ctx).load(moduleModel.getImage()).into(moduleIcon);
        else
            Glide.with(ctx).load(Utils.host+"/images/"+moduleModel.getImage()).into(moduleIcon);

        listitemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ctx, ContentActivity.class);
                intent.putExtra("id",moduleModel.getId());
                ctx.startActivity(intent);
            }
        });

        return listitemView;
    }
}