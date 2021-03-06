package com.boston.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
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

import com.boston.dao.yelp.ProviderException;
import com.boston.dao.yelp.Review;
import com.boston.dao.yelp.YelpDao;
import com.google.gson.Gson;

/**
 * This won't work because of the complexity of the YelpWrapper and leveraging the stream aspect.
 * Might be better to actually write a function with a return value vs void. 
 *
 */
@RunWith(SpringRunner.class)
public class ReviewService2Test 
{
    @TestConfiguration
    static class TestContextConfiguration {
    	@Bean
        public ReviewService2 reviewService2() {
            return new ReviewService2();
        }
    }
    
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

    @MockBean
    private YelpDao yelp;

    @Autowired
    private ReviewService2 svc;

    @Before
    public void setUp() throws InvalidBusinessException, IOException, ProviderException {
        Class<ReviewService2Test> clazz = ReviewService2Test.class;
        InputStream is = clazz.getResourceAsStream("/reviews.json");
        InputStreamReader isr = new InputStreamReader(is);
    	Reviews reviews = new Gson().fromJson(isr, Reviews.class);
        Mockito.when(yelp.getCity("100")).thenReturn("Boston");
        Mockito.when(yelp.getReviews("100", "Boston")).thenReturn(reviews.reviews);
    }

    @Test
    public void testFind() throws InvalidBusinessException {
        List<ReviewDTO> found = svc.find("100");
     
        Assert.assertTrue(found.get(0).getReviewerName().equals("Ella A."));
     }
}
