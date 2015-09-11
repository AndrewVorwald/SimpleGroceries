package com.example.andrew.simplegroceries.Undo.UserActions;

import com.example.andrew.simplegroceries.FoodData;
import com.example.andrew.simplegroceries.Undo.UndoStack;
import com.example.andrew.simplegroceries.Undo.UserAction;

/**
 * Created by Andrew on 9/10/2015.
 */
public class ActionAddFood implements UserAction {

    FoodData.Status addedToList;
    FoodData.Food food;
    FoodData foodData;

    public ActionAddFood(FoodData.Status status, FoodData.Food food, FoodData foodData){
        this.addedToList = status;
        this.food = food;
        this.foodData = foodData;

    }

    @Override
    public void undo() {
        foodData.remove(addedToList, food);
    }
}
