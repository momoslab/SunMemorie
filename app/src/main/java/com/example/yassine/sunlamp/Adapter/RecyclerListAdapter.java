package com.example.yassine.sunlamp.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yassine.sunlamp.ImageHelper;
import com.example.yassine.sunlamp.Model.ColorData;
import com.example.yassine.sunlamp.R;

import java.util.ArrayList;
import java.util.List;

import co.dift.ui.SwipeToAction;

/**
 * Created by YassIne on 14/02/2016.
 */
public class RecyclerListAdapter extends RecyclerView.Adapter<RecyclerListAdapter.ColorHolderView> {

    List<ColorData> mColorList;
    Context mContext;

    public RecyclerListAdapter(List<ColorData> list, Context context){
        this.mColorList = list;
        mContext = context;
    }

    public class ColorHolderView extends SwipeToAction.ViewHolder {
        TextView colorDescription;
        TextView colorName;
        ImageView itemThumbnail;
        TextView colorPosition;
        TextView colorCreationTime;

        public ColorHolderView(View itemView) {
            super(itemView);

            colorDescription   = (TextView) itemView.findViewById(R.id.element_description);
            colorName          = (TextView) itemView.findViewById(R.id.element_name);
            itemThumbnail     = (ImageView) itemView.findViewById(R.id.list_item_thumbnail);
            colorPosition      = (TextView) itemView.findViewById(R.id.element_position);
            colorCreationTime  = (TextView) itemView.findViewById(R.id.item_creation_time);
        }
    }

    @Override
    public ColorHolderView onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView =  LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_color, viewGroup, false);
        return new ColorHolderView(itemView);
    }

    public void addItems(List<ColorData> items){
        mColorList = items;
    }

    @Override
    public void onBindViewHolder(ColorHolderView colorHolderView, int i) {
        ColorData color = mColorList.get(i);

        colorHolderView.colorDescription.setText(color.getDescription());
        colorHolderView.colorName.setText(color.getName());
        colorHolderView.colorPosition.setText(color.getPosition());
        colorHolderView.colorCreationTime.setText(color.getCreation_time());

        if(color.getImagePath() != null) {
            ImageHelper.setRoundedImageFromFilePath(mContext, color.getImagePath(), colorHolderView.itemThumbnail);
        }
        colorHolderView.data = color;
    }

    @Override
    public int getItemCount() {
        return mColorList.size();
    }



}
