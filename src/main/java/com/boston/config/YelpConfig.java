package com.boston.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix="yelp")
public class YelpConfig 
{
	private String apiKey;
	private String busUrl;
	private String reviewUrl;
	
	public String getApiKey() {
		return apiKey;
	}
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}
	public String getBusUrl() {
		return busUrl;
	}
	public void setBusUrl(String busUrl) {
		this.busUrl = busUrl;
	}
	public String getReviewUrl() {
		return reviewUrl;
	}
	public void setReviewUrl(String reviewUrl) {
		this.reviewUrl = reviewUrl;
	}
}
