package com.boston.service;

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
	public static void readReviews(InputStream in, String city, List<ReviewDTO> reviews) throws IOException {
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
	
	@SuppressWarnings("unused")
	private class User {
		String id;
		String profile_url;
		String image_url;
		String name;
	}
	@SuppressWarnings("unused")
	private class Review {
		String id;
		int rating;
		User user;
		String text;
		String time_created;
		String url;
	}
	
	private static void readReviews(Gson gson, JsonReader reader, String city, List<ReviewDTO> reviews) throws IOException {
	    reader.beginArray();
	    while (reader.hasNext()) {
	    	Review review = gson.fromJson(reader, Review.class);
	    	ReviewDTO dto = new ReviewDTO();
	    	dto.setAvatarImageUrl(review.user.image_url);
	    	dto.setContent(review.text);
	    	dto.setEmotion(null);
	    	dto.setLocation(city);
	    	dto.setRating(review.rating);
	    	dto.setReviewerName(review.user.name);
	        reviews.add(dto);
	    }
	    reader.endArray();
	}
	
	public static void main(String[] args) {
		try {
			FileInputStream fis = new FileInputStream("c:\\dev\\rb\\crescendo\\src\\test\\resources\\reviews.json");
			List<ReviewDTO> reviews = new ArrayList<ReviewDTO>();
			ReviewParser.readReviews(fis, "Boston", reviews);
			reviews.forEach(System.out::println);
			System.out.println(reviews);
		} 
		catch (IOException e) {
			e.printStackTrace();
		} 
	}
}