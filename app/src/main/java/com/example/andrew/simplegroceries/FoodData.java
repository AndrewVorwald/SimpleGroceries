package com.example.andrew.simplegroceries;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeSet;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.joda.time.Days;
import org.joda.time.LocalDate;

/*This class manages all the data.
 When I describe the "lifespan" of a food below, I'm referring to the time between the food's purchase to when you run out/need to buy more.*/


public class FoodData {
	// I debated for a while between organizing the data with an ArrayList vs a HashSet.  Ultimately, I decided on ArrayList for two reasons:
	//        -The extra memory cost of a HashSet isn't worth the speed gained for operations since we're dealing with a small amount of data (<100)
	//        -Since the data set is small, it really doesn't matter what I pick.
	
	
	// These will be sorted arrays, sorted in alphabetical order by name of the food.
	ArrayList<Food> stuffINeed = new ArrayList<Food>();
	ArrayList<Food> stuffIHave = new ArrayList<Food>();

	void addNeed(String x){
		stuffINeed.add(new Food(x));
	}

	void addHave(String x){
		stuffIHave.add(new Food(x));
	}

	// Food object that keeps track of the name of the food, and how
	// long the food usually lasts.
	public class Food implements Comparable<Food>, Comparator<Food>{
		// Name of the food
		private String name;

		// An array of length 5 that keeps track of the 5 most recent lifespans
		// for this food in days.
		private int[] lifespan;

		// number of times this food has been purchased
		private int timesPurchased;

		// The percent chance the food needs to be bought again given the
		// lifespans array.
		private int rating;

		// Date of last purchase
		private LocalDate lastBought = null;

		// constructor
		Food(String _name) {
			name = _name;
			lifespan = new int[5];
			timesPurchased = 0;
			rating = 100;
		
		}
		
		// When a food item is swiped to stuff I don't have, then the food must have runout.  
		public void addLifeSpan(){
			lifespan[timesPurchased%5] = daysSinceBought();
		}
		
		public void incTimePurchased(){
			timesPurchased++;
		}
		
		//Standard equals method.  We can assume we'll only be comparing Food objects, so type check is unnecessary.
		//If a food has the same name as another food, then we'll say they're equal.
		public boolean equals(Object other){
			Food temp = (Food) other;
			return (temp.name.compareToIgnoreCase(temp.name)==0);	
		}

		// When swiped to stuff I have, we can assume the food object was
		// purchased and we'll set the date.
		public void setDateBought() {
			if (lastBought!= LocalDate.now())
				lastBought = new LocalDate();
		}

		// whenever the stuff I need list is generated, the rating is
		// recalculated.
		int calcRating() {

			// if the food item was just inputed, then there's 100% chance the
			// user needs it.
			if (lifespan[0] == 0)
				return 100;

			// Calculate the mean of the times purchased
			int i = 0;
			double sum = 0;
			while (lifespan[i] != 0) {
				sum += lifespan[i];
			}
			double mean = sum / i;

			// Calculate standard deviation
			i = 0;
			sum = 0;
			while (lifespan[i] != 0) {
				sum += ((lifespan[i] - mean) * (lifespan[i] - mean));
			}
			double std = Math.sqrt(sum / (i - 1));

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
			return name.compareToIgnoreCase(another.name);
			
		}

		@Override
		public int compare(Food a, Food b) {
			if(a.rating > b.rating)
				return 1;
			if(a.rating == b.rating)
				return 0;
			else
				return -1;
		}

		

	}

}
