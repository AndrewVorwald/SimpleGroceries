package com.example.andrew.simplegroceries;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Andrew on 8/17/2015.
 */
public class FoodAdapter extends BaseAdapter {
    private final Context context;
    private final int layout;
    private ArrayList<FoodData.Food> foodList;


    public FoodAdapter(Context context, int layout, ArrayList<FoodData.Food> foodList) {
        this.context = context;
        this.layout = layout;
        this.foodList = foodList;

    }

    @Override
    public int getCount() {
        return foodList.size();
    }

    @Override
    public Object getItem(int position) {
        return foodList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layout, parent, false);
        }
        TextView text1 = (TextView) view.findViewById(android.R.id.text1);
        text1.setText(foodList.get(position).getFoodName());

        return view;
    }

    public void remove(int index) {
        foodList.remove(index);
    }

    public void add(FoodData.Food food){
        foodList.add(food);
    }

    /* Did this because the guy in the google video said so*/
    @Override
    public boolean hasStableIds(){
        return true;
    }
}
