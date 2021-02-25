package com.boston.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.boston.service.YelpException;
import com.boston.service.InvalidBusinessException;
import com.boston.service.ReviewDTO;
import com.boston.service.ReviewService;

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
	@Autowired
	private ReviewService svc;

	@RequestMapping(method = { RequestMethod.GET }, produces = "application/json")
	public List<ReviewDTO> getReviews(@RequestParam(value="busId", required=true) String busId) throws InvalidBusinessException, YelpException {
		return svc.find(busId);
	}

}
