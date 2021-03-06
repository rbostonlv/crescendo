package com.boston.service.yelp;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.boston.service.InvalidBusinessException;
import com.boston.service.ReviewDTO;

/**
 * This is a cleaner implementation but requires more memory due to having multiple arrays of results. 
 * If we had pagination and a limit this wouldn't be a huge issue.
 * 
 * The other advantage of this implementation is the ability to swap out review sources from other than Yelp.
 * The service can stay mostly the same with the underlying Dao implementation changing. 
 * We might need different entity object equivalents but that's fine. 
 */
@Service
public class YelpReviewService
{
	private Logger log = LoggerFactory.getLogger(YelpReviewService.class);

	@Autowired
	private YelpApi yelp;
	
	public List<ReviewDTO> find(String busId) throws InvalidBusinessException {
		try {
			// For simplicity sake if empty still continue
			String city = yelp.getCity(busId);
			
			List<Review> reviews = yelp.getReviews(busId, city);
			List<ReviewDTO> reviewDtos = reviews.stream().<ReviewDTO>map(r -> { 
		    	ReviewDTO dto = new ReviewDTO();
		    	dto.setAvatarImageUrl(r.getUser().getImage_url());
		    	dto.setContent(r.getText());
		    	dto.setEmotion(null);
		    	dto.setLocation(city);
		    	dto.setRating(r.getRating());
		    	dto.setReviewerName(r.getUser().getName());
				return dto;
			}).collect(Collectors.toList());

			return reviewDtos;
		}
		catch (IOException e) {
			log.error("Can't retrieve reviews for: " + busId, e);
			throw new IllegalStateException("Unable to retrieve reviews for: " + busId);
		} 
		catch (YelpException e) {
			if (e.getHttpCode() == 400)
				throw new InvalidBusinessException(busId, "Business not found");
			log.error("Can't retrieve reviews for: " + busId, e);
			throw new IllegalStateException("Unable to retrieve reviews for: " + busId);
		}
	}
}
