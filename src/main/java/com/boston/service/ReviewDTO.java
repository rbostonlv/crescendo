package com.boston.service;

/**
 * 
 	{
		"id": "xAG4O7l-t1ubbwVAlPnDKg",
		"rating": 5,
		"user": {
			"id": "W8UK02IDdRS2GL_66fuq6w",
			"profile_url": "https://www.yelp.com/user_details?userid=W8UK02IDdRS2GL_66fuq6w",
			"image_url": "https://s3-media3.fl.yelpcdn.com/photo/iwoAD12zkONZxJ94ChAaMg/o.jpg",
			"name": "Ella A."
		},
		"text": "Went back again to this place since the last time i visited the bay area 5 months ago, and nothing has changed. Still the sketchy Mission, Still the cashier...",
		"time_created": "2016-08-29 00:41:13",
		"url": "https://www.yelp.com/biz/la-palma-mexicatessen-san-francisco?hrid=hp8hAJ-AnlpqxCCu7kyCWA&adjust_creative=0sidDfoTIHle5vvHEBvF0w&utm_campaign=yelp_api_v3&utm_medium=api_v3_business_reviews&utm_source=0sidDfoTIHle5vvHEBvF0w"
	}
 */
public class ReviewDTO 
{
	private String reviewerName;
	private String avatarImageUrl;
	private String location;
	private int rating;
	private String content;
	private String emotion;
	
	public String getReviewerName() {
		return reviewerName;
	}
	public void setReviewerName(String reviewerName) {
		this.reviewerName = reviewerName;
	}
	public String getAvatarImageUrl() {
		return avatarImageUrl;
	}
	public void setAvatarImageUrl(String avatarImageUrl) {
		this.avatarImageUrl = avatarImageUrl;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public int getRating() {
		return rating;
	}
	public void setRating(int rating) {
		this.rating = rating;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getEmotion() {
		return emotion;
	}
	public void setEmotion(String emotion) {
		this.emotion = emotion;
	}
	public String toString() {
		return reviewerName + ":" + avatarImageUrl + ":" + location + ":" + rating + ":" + content + ":" + emotion;
	}

}
