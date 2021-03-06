package com.boston.dao.yelp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import com.boston.config.YelpConfig;
import com.boston.service.MigratedBusinessException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

@Component
public class YelpDao 
{
	private Logger log = LoggerFactory.getLogger(YelpDao.class);

	@Autowired 
	private YelpConfig config;

	@FunctionalInterface
	private interface StreamHandler {
		void handle(InputStream is);
	}

	@FunctionalInterface
	private interface ThrowingHandler<E extends Exception> {
	    void handle(InputStream is) throws E;
	}

	// Wraps and rethrows underlying exception as an unchecked exception
	private static <E extends Exception> StreamHandler handlerWrapper(ThrowingHandler<E> throwingHandler) {
	    return is -> {
	        try {
	            throwingHandler.handle(is);
	        } 
	        catch (Exception ex) {
	            throw new RuntimeException(ex);
	        }
	    };
	}

	private class Error {
		String code;
		String description;
		String new_business_id;
	}
	private class ErrorWrapper {
		Error error;			
	}
	
	@PostConstruct
	private void post() {
		System.out.println(config.getApiKey());
		System.out.println(config.getBusUrl());
		System.out.println(config.getReviewUrl());
	}
	
	public String getCity(String busId) throws IOException, ProviderException {
		StringBuilder cityBuf = new StringBuilder();
		String busUrl = config.getBusUrl();
		busUrl += "/" + busId;
		try {
			String finalBusId = busId;
			doGet(busUrl, config.getApiKey(), (is) -> getCity(finalBusId, is, cityBuf));
		} 
		catch (MigratedBusinessException mbe) {
			busId = mbe.getNewBusId();
			busUrl += "/" + busId;
			try {
				String finalBusId = busId;
				doGet(busUrl, config.getApiKey(), (is) -> getCity(finalBusId, is, cityBuf));
			} 
			catch (Exception e) {
				throw new IOException("Problem getting business: " + busId);
			}
		}
		catch (IOException e) {
			throw new IOException("Problem getting business: " + busId);
		}
		return cityBuf.toString();
	}

	public List<Review> getReviews(String busId, String city) throws IOException, ProviderException {
		String reviewUrl = config.getReviewUrl();
		reviewUrl = reviewUrl.replace("XX", busId);
		
		List<Review> reviews = new ArrayList<Review>(); 
		
		try {
			doGet(reviewUrl, config.getApiKey(), handlerWrapper((is) -> ReviewParser.readReviews(is, city, reviews)));
		} 
		catch (MigratedBusinessException mbe) {
			busId = mbe.getNewBusId();
			reviewUrl = reviewUrl.replace("XX", busId);
			try {
				doGet(reviewUrl, config.getApiKey(), handlerWrapper((is) -> ReviewParser.readReviews(is, city, reviews)));
			} 
			catch (Exception e) {
				throw new IOException("Problem getting reviews for: " + busId);
			}
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
	
	private void doGet(String url, String apiKey, StreamHandler handler) throws MigratedBusinessException, IOException, ProviderException {
		URL serverURL = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) serverURL.openConnection();
		
		try {
			conn.setRequestMethod("GET");
			
	        conn.setRequestProperty(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey);
	        conn.setRequestProperty(HttpHeaders.CONTENT_TYPE, "application/json");
	        //e.g. bearer token= eyJhbGciOiXXXzUxMiJ9.eyJzdWIiOiPyc2hhcm1hQHBsdW1zbGljZS5jb206OjE6OjkwIiwiZXhwIjoxNTM3MzQyNTIxLCJpYXQiOjE1MzY3Mzc3MjF9.O33zP2l_0eDNfcqSQz29jUGJC-_THYsXllrmkFnk85dNRbAw66dyEKBP5dVcFUuNTA8zhA83kk3Y41_qZYx43T
			
			int responseCode = conn.getResponseCode();
			if (responseCode == 301)
				throw new MigratedBusinessException(getNewBusId(conn.getInputStream()));
			else if (responseCode != 200) {
				Error err = getError(conn.getInputStream());
				throw new ProviderException(responseCode, err.code, err.description);
			}
			
			handler.handle(conn.getInputStream());
		}
		catch (IOException e) {
			throw e;
		}
		finally {
			conn.disconnect();
		}
	}
	
	private String getNewBusId(InputStream is) throws IOException {
		return getError(is).new_business_id;
	}

	//	{
	//    "error": {
	//        "code": "BUSINESS_MIGRATED",
	//        "description": "The requested business has been permanently migrated. Please access it using its new ID.",
	//        "new_business_id": "gezgzYZ16YVGnCcnFvy6WQ"
	//    }
	//  }
	private Error getError(InputStream is) throws IOException {
		StringBuffer buf = new StringBuffer();
		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		String inputLine;
		while ((inputLine = in.readLine()) != null) {
			buf.append(inputLine);
		}
		in.close();
		Gson gson = new Gson();
		ErrorWrapper wrap = gson.fromJson(buf.toString(), ErrorWrapper.class);
		return wrap.error;
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
	private void getCity(String busId, InputStream is, StringBuilder cityBuf) {
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
					cityBuf.append(cityNode.textValue());
				}
			}
		}
		catch (Exception e) {
			log.warn("Problem finding city for business: " + busId, e);
		}
	}
}
