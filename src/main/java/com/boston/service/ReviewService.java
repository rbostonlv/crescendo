package com.boston.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Yelp doesn't appear to provide pagination for reviews so this service is not including it either.
 * 
 * Due to the potential for very large responses we will be parsing as a stream vs reading 
 * everything into memory. 
 * This might be problematic from a connection perspective but we can find alternatives for that later. 
 * If maintaining a long term connection is a problem we could simply dump the data to a temporary file
 * and then process the file separately - then delete it (true temp).    
 *
 *
 */
@Service
public class ReviewService
{
	private Logger log = LoggerFactory.getLogger(ReviewService.class);

	@Autowired
	private YelpApiWrapper yelp;
	
	public List<ReviewDTO> find(String busId) throws InvalidBusinessException {

		// Call the business API to pull in the location
		StringBuilder city = new StringBuilder();
		try {
			yelp.getBusinessInfo(busId, (is) -> city.append(getCity(busId, is)));
		} 
		catch (IOException e) {
			log.error("Can't retrieve business information)");
			throw new IllegalStateException("Unable to retrieve business: " + busId);
		} 
		catch (YelpException e) {
			if (e.getHttpCode() == 400)
				throw new InvalidBusinessException(busId, "Business not found");
			log.error("Can't retrieve business information: " + busId, e);
			throw new IllegalStateException("Unable to retrieve business: " + busId);
		}
		
		List<ReviewDTO> reviews = new ArrayList<ReviewDTO>();
		try {
			yelp.getReviews(busId, 
				YelpApiWrapper.handlerWrapper((is) -> ReviewParser.readReviews(is, city.toString(), reviews)));
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
		catch (RuntimeException e) {
			if (e.getCause() != null && e.getCause() instanceof IOException) {
				log.error("Can't process review json for: " + busId, e);
				throw new IllegalStateException("Unable to retrieve reviews for: " + busId);
			}
			log.error("Can't retrieve reviews for: " + busId, e);
			throw new IllegalStateException("Unable to retrieve reviews for: " + busId);
		}
		
		return reviews;
	}

	//	{
	//		"id": "WavvLdfdP6g8aZTtbBQHTw",
	//		"review_count": 5296,
	//		"location": {
	//			"address1": "800 N Point St",
	//			"address2": "",
	//			"address3": "",
	//			"city": "San Francisco",
	//			"zip_code": "94109",
	//			"country": "US",
	//			"state": "CA",
	//			"display_address": [
	//				"800 N Point St",
	//				"San Francisco, CA 94109"
	//			],
	//			"cross_streets": ""
	//		},
	//		"price": "$$$$",
	//		"transactions": [],
	//		"special_hours": [{
	//			"date": "2019-02-07",
	//			"is_closed": null,
	//			"start": "1600",
	//			"end": "2000",
	//			"is_overnight": false
	//		}]
	//	}
	// For simplicity sake, if we can't get the city we continue!!
	private String getCity(String busId, InputStream is) {
		try {
			StringBuffer buf = new StringBuffer();
			BufferedReader in = new BufferedReader(new InputStreamReader(is));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				buf.append(inputLine);
			}
			in.close();
			
			ObjectMapper objectMapper = new ObjectMapper();

			//read JSON like DOM Parser
			JsonNode busNode = objectMapper.readTree(buf.toString());
			if (busNode.has("location")) {
				JsonNode locNode = busNode.get("location");
				if (locNode.hasNonNull("city")) {
					JsonNode cityNode = locNode.get("city");
					return cityNode.textValue();
				}
			}
			return null;
		}
		catch (Exception e) {
			log.warn("Problem finding city for business: " + busId, e);
			return null;
		}
	}
}
