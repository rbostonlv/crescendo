package com.boston.dao.yelp;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.boston.dao.yelp.Review;
import com.boston.dao.yelp.ReviewParser;
import com.google.gson.Gson;

/**
 * This won't work because of the complexity of the YelpWrapper and leveraging the stream aspect.
 * Might be better to actually write a function with a return value vs void. 
 *
 */
public class ReviewParserTest 
{
    class Reviews {
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

    private Reviews baseReviews;
    
    @Before
    public void setUp() {
        Class<ReviewParserTest> clazz = ReviewParserTest.class;
        InputStream is = clazz.getResourceAsStream("/reviews.json");
        InputStreamReader isr = new InputStreamReader(is);
    	baseReviews = new Gson().fromJson(isr, Reviews.class);
    }

    @Test
    public void testParse() throws IOException {
        Class<ReviewParserTest> clazz = ReviewParserTest.class;
        InputStream is = clazz.getResourceAsStream("/reviews.json");

        List<Review> reviews = new ArrayList<Review>();
    	ReviewParser.readReviews(is, "Boston", reviews);

        Assert.assertTrue(reviews.get(0).getUser().getName().equals(baseReviews.reviews.get(0).getUser().getName()));
     }
}
