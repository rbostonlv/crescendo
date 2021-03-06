package com.boston.service.yelp;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public class Review 
{
	private String id;
	private int rating;
	private User user;
	private String text;
	private String time_created;
	private String url;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getRating() {
		return rating;
	}
	public void setRating(int rating) {
		this.rating = rating;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getTime_created() {
		return time_created;
	}
	public void setTime_created(String time_created) {
		this.time_created = time_created;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String toString() {
		return Arrays.<String>asList(id, ",", Integer.toString(rating), ",", Optional.ofNullable(user).toString(), ",", text, ",", url)
		    .stream().collect(Collectors.joining(","));
	}
}
