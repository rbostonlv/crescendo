package com.boston.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import com.boston.config.YelpConfig;
import com.google.gson.Gson;

/**
 * For the heck of it, I went old school vs using something like Apache HttpClient.
 * Since I want to demonstrate larger datasets, I'm actually getting back the InputStream 
 * rather than reading the entire response into memory.
 *
 */
@Component
public class YelpApiWrapper 
{
	@Autowired 
	private YelpConfig config;

	@FunctionalInterface
	public interface StreamHandler {
		void handle(InputStream is);
	}

	@FunctionalInterface
	public interface ThrowingHandler<E extends Exception> {
	    void handle(InputStream is) throws E;
	}

	// Wraps and rethrows underlying exception as an unchecked exception
	public static <E extends Exception> StreamHandler handlerWrapper(ThrowingHandler<E> throwingHandler) {
	    return is -> {
	        try {
	            throwingHandler.handle(is);
	        } 
	        catch (Exception ex) {
	            throw new RuntimeException(ex);
	        }
	    };
	}

	// Unwraps and throws the exception properly
	public static <E extends Exception> void unwrap(Class<E> clazz, Runnable r) throws E {
		try {
			r.run();
		}
		catch (RuntimeException e) {
		    E e1 = clazz != null && clazz.isInstance(e.getCause()) ? clazz.cast(e.getCause()) : null;
			//throw e1 != null ? e1 : e; // compiler doesn't like this
			if (e1 != null)
				throw e1;
			throw e;
		}
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
	
	public void getBusinessInfo(String busId, StreamHandler handler) throws IOException, YelpException {
		String busUrl = config.getBusUrl();
		busUrl += "/" + busId;
		try {
			doGet(busUrl, config.getApiKey(), handler);
		} 
		catch (MigratedBusinessException mbe) {
			busId = mbe.getNewBusId();
			busUrl += "/" + busId;
			try {
				doGet(busUrl, config.getApiKey(), handler);
			} 
			catch (Exception e) {
				throw new IOException("Problem getting business: " + busId);
			}
		}
		catch (IOException e) {
			throw new IOException("Problem getting business: " + busId);
		}
	}

	public void getReviews(String busId, StreamHandler handler) throws IOException, YelpException {
		String reviewUrl = config.getReviewUrl();
		reviewUrl = reviewUrl.replace("XX", busId);
		try {
			doGet(reviewUrl, config.getApiKey(), handler);
		} 
		catch (MigratedBusinessException mbe) {
			busId = mbe.getNewBusId();
			reviewUrl = reviewUrl.replace("XX", busId);
			try {
				doGet(reviewUrl, config.getApiKey(), handler);
			} 
			catch (Exception e) {
				throw new IOException("Problem getting reviews for: " + busId);
			}
		}
	}
	
	private void doGet(String url, String apiKey, StreamHandler handler) throws MigratedBusinessException, IOException, YelpException {
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
				throw new YelpException(responseCode, err.code, err.description);
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
}
