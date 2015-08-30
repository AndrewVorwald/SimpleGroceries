package com.example.andrew.simplegroceries;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.joda.time.Days;
import org.joda.time.LocalDate;

/*This class manages all the data.
 When I describe the "lifespan" of a food below, I'm referring to the time between the food's purchase to when you run out/need to buy more.*/


public class FoodData implements Serializable {


    // These will be sorted arrays, sorted in alphabetical order by foodName of the food.
    ArrayList<Food> stuffINeed;
    ArrayList<Food> stuffIHave;
    Status currentList;

    public enum Status implements Serializable {
        NEED, HAVE;

    }

    public FoodData() {
        stuffINeed = new ArrayList<Food>();
        stuffIHave = new ArrayList<Food>();
        Status currentList = Status.NEED;
    }

    public static FoodData deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInput in = new ObjectInputStream(bis)){
            FoodData temp = (FoodData)in.readObject();
            return temp;
        }
        catch (IOException e){
            e.printStackTrace();
        }
        catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        return null;
    }

    public void addNeed(String x) {
        stuffINeed.add(new Food(x));
    }

    public void addHave(String x) {
        stuffIHave.add(new Food(x));
    }

    public byte[] convertToBytes() throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutput out = new ObjectOutputStream(bos)) {
            out.writeObject(this);
            return bos.toByteArray();
        }

    }


    // Food object that keeps track of the foodName of the food, and how
    // long the food usually lasts.
    public class Food implements Comparable<Food>, Comparator<Food>, Serializable {
        // Name of the food
        private String foodName;

        // An array of length 5 that keeps track of the 5 most recent lifespans
        // for this food in days.
        private int[] lifespans;

        // number of times this food has been purchased
        private int timesPurchased;

        // The percent chance the food needs to be bought again given the
        // lifespans array.
        private int foodRating;

        // Date of last purchase
        private LocalDate lastBought = null;

        // constructor
        Food(String _name) {
            foodName = _name;
            lifespans = new int[5];
            timesPurchased = 0;
            foodRating = 100;

        }

        // When a food item is swiped to stuff I don't have, then the food must have runout.
        public void addLifeSpan() {
            lifespans[timesPurchased % 5] = daysSinceBought();
        }

        public void incTimePurchased() {
            timesPurchased++;
        }

        //Standard equals method.  We can assume we'll only be comparing Food objects, so type check is unnecessary.
        //If a food has the same foodName as another food, then we'll say they're equal.
        public boolean equals(Object other) {
            Food temp = (Food) other;
            return (temp.foodName.compareToIgnoreCase(temp.foodName) == 0);
        }

        // When swiped to stuff I have, we can assume the food object was
        // purchased and we'll set the date.
        public void setDateBought() {
            if (lastBought != LocalDate.now())
                lastBought = new LocalDate();
        }

        @Override
        public String toString(){
            return this.foodName;

        }

        // whenever the stuff I need list is generated, the rating is
        // recalculated.
        int calcRating() {

            // if the food item was just inputed, then there's 100% chance the
            // user needs it.
            if (lifespans[0] == 0)
                return 100;

            // Calculate the mean of the lifespans
            int i = 0;
            double sum = 0;
            while (lifespans[i] != 0) {
                sum += lifespans[i];
            }
            double mean = sum / i;

            // Calculate standard deviation of the lifespans
            i = 0;
            sum = 0;
            while (lifespans[i] != 0) {
                sum += ((lifespans[i] - mean) * (lifespans[i] - mean));
            }
            double std = Math.sqrt(sum / (i - 1));

            // create a normal distribution


            NormalDistribution x = new NormalDistribution(mean, std);
            return (int) (100 * x.cumulativeProbability(daysSinceBought()));
        }

        // calculate number of days since last purchase
        int daysSinceBought() {
            Days d = Days.daysBetween(lastBought, LocalDate.now());
            return d.getDays();
        }

        @Override
        public int compareTo(Food another) {
            return foodName.compareToIgnoreCase(another.foodName);

        }

        @Override
        public int compare(Food a, Food b) {
            if (a.foodRating > b.foodRating)
                return 1;
            if (a.foodRating == b.foodRating)
                return 0;
            else
                return -1;
        }

        public String getFoodName() {
            return foodName;
        }


    }

}
