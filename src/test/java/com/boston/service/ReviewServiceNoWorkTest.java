package com.boston.service;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.gson.Gson;

/**
 * This won't work because of the complexity of the YelpWrapper and leveraging the stream aspect.
 * Might be better to actually write a function with a return value vs void. 
 * In this version there's nothing to really stub out at the lower levels.
 *
 */
//@RunWith(SpringRunner.class)
public class ReviewServiceNoWorkTest 
{
    @TestConfiguration
    static class TestContextConfiguration {
         @Bean
        public ReviewService reviewService() {
            return new ReviewService();
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
    	public String toString() {
    		return Arrays.<String>asList(id, ",", Integer.toString(rating), ",", Optional.ofNullable(user).toString(), ",", text, ",", url)
			    .stream().collect(Collectors.joining(","));
    	}
	}
	
    static class Reviews {
    	List<Review> reviews = new ArrayList<Review>();
    	public String toString() {
    		return reviews.toString();
    	}
    	public List<Review> getReviews() {
    		return reviews;
    	}
    	public void setReviews(List<Review> reviews) {
    		this.reviews = reviews;
    	}
    }

    @MockBean
    private YelpApiWrapper yelp;

    @Autowired
    private ReviewService svc;

    @Before
    public void setUp() throws InvalidBusinessException {
        Class<ReviewServiceNoWorkTest> clazz = ReviewServiceNoWorkTest.class;
        InputStream is = clazz.getResourceAsStream("/reviews.json");
        InputStreamReader isr = new InputStreamReader(is);
    	Reviews reviews = new Gson().fromJson(isr, Reviews.class);
    	System.out.println(reviews);
    	List<ReviewDTO> results = reviews.reviews.stream().map(r -> new ReviewDTO()).collect(Collectors.toList());
        Mockito.when(svc.find("100")).thenReturn(results);
    }

//    @Test
    public void testCreateFind() throws InvalidBusinessException {
        List<ReviewDTO> found = svc.find("100");
     
        Assert.assertTrue(found.get(0).getReviewerName().equals(""));
     }
}
