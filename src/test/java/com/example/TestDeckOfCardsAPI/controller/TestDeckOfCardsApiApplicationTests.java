package com.example.TestDeckOfCardsAPI.controller;

import com.example.TestDeckOfCardsAPI.CommonUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.text.MessageFormat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TestDeckOfCardsApiApplicationTests {

	private static final Logger LOGGER = LoggerFactory.getLogger(TestDeckOfCardsApiApplicationTests.class);
	private RestTemplate restTemplate = new RestTemplate();
	private HttpHeaders httpHeaders = CommonUtils.getHttpHeader();

	private final String BASE_URL = "https://deckofcardsapi.com/api/deck/";
	private final String NEW_DECK_URL = BASE_URL+"new/";
	private final String DRAW_CARDS_URL = BASE_URL+"{0}/draw/";

	private static String deckId;

	@Test
	@Order(1)
	public void contextLoads() {
	}

	@Test
	@Order(2)
	public void createNewDeckOfCardsTest() throws JSONException {
		HttpEntity<String> httpEntity = new HttpEntity<String>(null, httpHeaders);

		LOGGER.info("Testing Create a new deck of cards...");
		ResponseEntity<String> responseEntity = restTemplate.exchange(NEW_DECK_URL, HttpMethod.GET, httpEntity, String.class);
		String body = responseEntity.getBody();
		LOGGER.info("Response: " + responseEntity.getStatusCode() + ", " + body);
		Assert.assertEquals(200, responseEntity.getStatusCodeValue());
		Assert.assertEquals(true, body.contains("\"success\": true"));
		Assert.assertEquals(true, body.contains("\"deck_id\": "));
		Assert.assertEquals(true, body.contains("\"remaining\": 52"));
		Assert.assertEquals(true, body.contains("\"shuffled\": false"));

		LOGGER.info("Testing Create a new deck of cards adding Jokers...");
		responseEntity = restTemplate.exchange(NEW_DECK_URL+"?jokers_enabled=true", HttpMethod.GET, httpEntity, String.class);
		body = responseEntity.getBody();
		LOGGER.info("Response: " + responseEntity.getStatusCode() + ", " + body);
		Assert.assertEquals(200, responseEntity.getStatusCodeValue());
		Assert.assertEquals(true, body.contains("\"success\": true"));
		Assert.assertEquals(true, body.contains("\"deck_id\": "));
		Assert.assertEquals(true, body.contains("\"remaining\": 54"));
		Assert.assertEquals(true, body.contains("\"shuffled\": false"));

		JSONObject jsonObject = new JSONObject(body);
		deckId = jsonObject.getString("deck_id");
	}

	@Test
	@Order(3)
	public void drawCardsTest() throws JSONException {
		if(deckId == null) {
			this.createNewDeckOfCardsTest();
		}
		HttpEntity<String> httpEntity = new HttpEntity<String>(null, httpHeaders);
		String url = MessageFormat.format(DRAW_CARDS_URL, deckId);

		LOGGER.info("Testing Draw one or more cards from a deck...");
		ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
		String body = responseEntity.getBody();
		LOGGER.info("Response: " + responseEntity.getStatusCode() + ", " + body);
		Assert.assertEquals(200, responseEntity.getStatusCodeValue());
		Assert.assertEquals(true, body.contains("\"success\": true"));
		Assert.assertEquals(true, body.contains("\"deck_id\": "));
		Assert.assertEquals(true, body.contains("\"cards\": "));
		Assert.assertEquals(true, body.contains("\"remaining\": "));

			JSONObject jsonObject = new JSONObject(body);
			JSONArray jsonArray = jsonObject.getJSONArray("cards");
			if(jsonArray != null && jsonArray.length() > 0) {
				for(int i=0; i<jsonArray.length(); i++) {
					JSONObject obj = jsonArray.getJSONObject(i);
					Assert.assertEquals(true, obj.toString().contains("\"image\":"));
					Assert.assertEquals(true, obj.toString().contains("\"images\":"));
					Assert.assertEquals(true, obj.toString().contains("\"code\":"));
					Assert.assertEquals(true, obj.toString().contains("\"suit\":"));
					Assert.assertEquals(true, obj.toString().contains("\"value\":"));

					Assert.assertEquals(true, obj.getString("images").contains("\"svg\":"));
					Assert.assertEquals(true, obj.getString("images").contains("\"png\":"));
				}
			}
	}

}
