package com.example.cyberawareness;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
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
import com.squareup.picasso.Picasso;

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
        TextView moduleLockIcon = listitemView.findViewById(R.id.moduleLockIcon);
        TextView moduleUnlockIcon = listitemView.findViewById(R.id.moduleUnlockIcon);

        moduleName.setText(moduleModel.getModuleName());

        try {
            Glide.with(ctx).clear(moduleIcon);
            if (moduleModel.getImage().startsWith("http"))
                Picasso.get().load(moduleModel.getImage()).into(moduleIcon);
//                Glide.with(ctx).load(moduleModel.getImage()).into(moduleIcon).onLoadFailed(ctx.getDrawable(R.drawable.baseline_menu_book_24));
            else {
                Picasso.get().load(Utils.host + "/images/" + moduleModel.getImage()).into(moduleIcon);
//                Glide.with(ctx).load(Utils.host + "/images/" + moduleModel.getImage()).diskCacheStrategy(DiskCacheStrategy.ALL).into(moduleIcon).onLoadFailed(ctx.getDrawable(R.drawable.baseline_menu_book_24));
            }

        }catch (Exception ex){
            Log.d("GlideExc",ex.getMessage());
        }
        if(Utils.getUser(ctx,"user_type").equals("Learner")) {
            if (moduleModel.getIsEnrolled().equals("enrolled")) {
                moduleLockIcon.setVisibility(View.GONE);
                moduleUnlockIcon.setVisibility(View.VISIBLE);
            } else if (moduleModel.getIsEnrolled().equals("not_enrolled")) {
                moduleLockIcon.setVisibility(View.VISIBLE);
                moduleUnlockIcon.setVisibility(View.GONE);
            }
        }
        listitemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent();
                if(!Utils.getUser(ctx,"id").equals("")) {
                    intent.setClass(ctx,ContentActivity.class);
                    intent.putExtra("id", moduleModel.getId());
                }else{
                    intent.setClass(ctx, Login.class);
                }
                ctx.startActivity(intent);
            }
        });
        listitemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent intent = new Intent(ctx,UpdateModuleActivity.class);
                intent.putExtra("module",moduleModel.getId());
                intent.putExtra("name",moduleModel.getModuleName());
                intent.putExtra("level",moduleModel.getLevel());
                intent.putExtra("icon",moduleModel.getImage());
                ctx.startActivity(intent);
                return false;
            }
        });

        return listitemView;
    }
}