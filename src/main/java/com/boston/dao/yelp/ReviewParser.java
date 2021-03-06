package com.boston.dao.yelp;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

class ReviewParser 
{
	static void readReviews(InputStream in, String city, List<Review> reviews) throws IOException {
		Gson gson = new Gson();
	
	    JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
	    try {
		    reader.beginObject();
		    
		    reader.peek(); // top level
		    reader.nextName(); // reviews 
            reader.peek(); // array of reviews
		    readReviews(gson, reader, city, reviews);
	    }
	    finally {
		    reader.close();
	    }
	}
	
	private static void readReviews(Gson gson, JsonReader reader, String city, List<Review> reviews) throws IOException {
	    reader.beginArray();
	    while (reader.hasNext()) {
	    	Review review = gson.fromJson(reader, Review.class);
	        reviews.add(review);
	    }
	    reader.endArray();
	}
	
	public static void main(String[] args) {
		try {
			FileInputStream fis = new FileInputStream("c:\\dev\\rb\\crescendo\\src\\test\\resources\\reviews.json");
			List<Review> reviews = new ArrayList<Review>();
			ReviewParser.readReviews(fis, "Boston", reviews);
			reviews.forEach(System.out::println);
			System.out.println(reviews);
		} 
		catch (IOException e) {
			e.printStackTrace();
		} 
	}
}