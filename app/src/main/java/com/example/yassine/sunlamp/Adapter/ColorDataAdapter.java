package com.example.yassine.sunlamp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yassine.sunlamp.ImageHelper;
import com.example.yassine.sunlamp.Model.ColorData;
import com.example.yassine.sunlamp.R;
import com.example.yassine.sunlamp.ViewSingleItemActivity;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by YassIne on 17/08/2015.
 */
public class ColorDataAdapter extends ArrayAdapter<ColorData> {
    Context context;
    List<ColorData> colorsList = new ArrayList<>();
    ImageView itemThumbnail;
    private final static String LOG ="ColorDataAdapter";

    public ColorDataAdapter(Context context, List<ColorData> colors) {
        super(context, 0, colors);
        this.colorsList = colors;
        this.context = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){

        final ColorData color = getItem(position);

        if(convertView == null){

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_color, parent, false);
        }

        TextView colorDescription = (TextView) convertView.findViewById(R.id.element_description);
        TextView colorName = (TextView) convertView.findViewById(R.id.element_name);

        itemThumbnail = (ImageView) convertView.findViewById(R.id.list_item_thumbnail);
       // itemThumbnail.setElevation(4);

        TextView colorPosition = (TextView) convertView.findViewById(R.id.element_position);

        TextView colorCreationTime = (TextView) convertView.findViewById(R.id.item_creation_time);

       // if(color.getDescription() != null)
            colorDescription.setText(color.getDescription());
       // if(color.getName() != null)
            colorName.setText(color.getName());
       // if(color.getPosition() != null)
            colorPosition.setText(color.getPosition());

        Log.i(LOG,"Creation_time -> " + color.getCreation_time());

        colorCreationTime.setText(color.getCreation_time());

        if(color.getImagePath() != null) {
            Log.e(LOG, "Log nome colore --> " + color.getName());
            ImageHelper.setRoundedImageFromFilePath(context, color.getImagePath(), itemThumbnail);
        }

        return convertView;

    }



    }
