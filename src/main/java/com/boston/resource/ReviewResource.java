package com.boston.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.boston.service.YelpException;
import com.boston.service.yelp.YelpReviewService;
import com.boston.service.InvalidBusinessException;
import com.boston.service.ReviewDTO;
import com.boston.service.ReviewService;
import com.boston.service.ReviewService2;

import java.util.List;

/**
 * Normally for the potential of large results sets we would have pagination 
 * of some sort but it appears yelp doesn't have that for reviews. 
 * Just FYI. 
 */

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewResource
{
	@Value("${review.provider:yelp1}")
	private String provider;
	
	@Autowired
	private ReviewService yelp1;

	@Autowired
	private ReviewService2 yelp2;

	@Autowired
	private YelpReviewService yelp3;

	@RequestMapping(method = { RequestMethod.GET }, produces = "application/json")
	public List<ReviewDTO> getReviews(@RequestParam(value="busId", required=true) String busId) throws InvalidBusinessException, YelpException {
		switch (provider) {
			case "yelp1":
				return yelp1.find(busId);
			case "yelp2":
				return yelp2.find(busId);
			case "yelp3":
				return yelp3.find(busId);
			case "google":
				break;
		}
		return yelp1.find(busId);
	}

}
