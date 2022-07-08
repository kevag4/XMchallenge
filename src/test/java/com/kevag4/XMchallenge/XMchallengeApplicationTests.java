package com.kevag4.XMchallenge;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.support.NullValue;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.kevag4.XMchallenge.model.CryptoSymbol;
import com.kevag4.XMchallenge.repository.CryptoRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = XMchallengeApplication.class)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class XMchallengeApplicationTests {
	Logger logger = LoggerFactory.getLogger(XMchallengeApplicationTests.class);

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private CryptoRepository cryptoRepository;

	// Need towait for db population with cryptos in order to start tests
	@BeforeAll
	void waitForDBPopulation() {
		while (cryptoRepository.count() < 450) {
			try {
				logger.info(null, cryptoRepository.count());
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	// Get first page with 100 cryptos and validate the total number, pages and
	// elements are correct
	@Test
	public void getAllCryptos() throws Exception {

		ResultActions response = mockMvc.perform(get("/api/cryptos")
				.param("page", "0")
				.param("size", "100"));

		response.andExpect(status().isOk())
				.andDo(print())
				.andExpect(jsonPath("$.content", isA(ArrayList.class)))
				.andExpect(jsonPath("$.content", hasSize(100)))
				.andExpect(jsonPath("$.totalElements", is(450)))
				.andExpect(jsonPath("$.totalPages", is(5)));

	}

	// Get Crypto for a norm day. This test is fixed and values are hardcoded!!!
	@Test
	public void getCryptoWithHighestNormalizedRangeForADay_Positive() throws Exception {

		ResultActions responseETH = mockMvc.perform(get("/api/cryptos/getCryptoWithHighestNormalizedRangeForADay")
				.param("day", "2022-01-28"));

		responseETH.andExpect(status().isOk())
				.andDo(print())
				.andExpect(jsonPath("$.normalization_range", is(0.063119)))
				.andExpect(jsonPath("$.day", is("2022-01-28")))
				.andExpect(jsonPath("$.crypto_symbol", is("ETH")));

		ResultActions responseXRP = mockMvc.perform(get("/api/cryptos/getCryptoWithHighestNormalizedRangeForADay")
				.param("day", "2022-01-01"));

		responseXRP.andExpect(status().isOk())
				.andDo(print())
				.andExpect(jsonPath("$.normalization_range", is(0.024096)))
				.andExpect(jsonPath("$.day", is("2022-01-01")))
				.andExpect(jsonPath("$.crypto_symbol", is("XRP")));
	}

	// Get Crypto for a norm daybut with wrong parameter format
	@Test
	public void getCryptoWithHighestNormalizedRangeForADay_Negative() throws Exception {

		ResultActions responseETH = mockMvc.perform(get("/api/cryptos/getCryptoWithHighestNormalizedRangeForADay")
				.param("day", "04-04-2019"));

		responseETH.andExpect(status().isInternalServerError())
				.andDo(print())
				.andExpect(jsonPath("$.error_message", containsString(
						"Failed to convert value of type 'java.lang.String' to required type 'java.time.LocalDate'; nested exception is org.springframework.core.convert.ConversionFailedException: Failed to convert from type [java.lang.String] to type [@org.springframework.web.bind.annotation.RequestParam @org.springframework.format.annotation.DateTimeFormat java.time.LocalDate] for value '04-04-2019'; nested exception is java.lang.IllegalArgumentException: Parse attempt failed for value [04-04-2019]")));
	}

	// Get Crypto details for DOGE. This test is fixed and values are hardcoded!!!
	@Test
	public void getCryptoDetailsDOGE_Positive() throws Exception {

		ResultActions response = mockMvc
				.perform(get("/api/cryptos/getCryptoDetails/{symbol}", CryptoSymbol.DOGE.name()));

		Map<String, Object> olderValue = new HashMap<>();
		olderValue.put("price", 0.17);
		olderValue.put("timestamp", "2022-01-01T05:00:00Z");

		Map<String, Object> newerValue = new HashMap<>();
		newerValue.put("price", 0.14);
		newerValue.put("timestamp", "2022-01-31T19:00:00Z");

		response.andExpect(status().isOk())
				.andDo(print())
				.andExpect(jsonPath("$.symbol", is(CryptoSymbol.DOGE.name())))
				.andExpect(jsonPath("$.min_value_price", is(0.13)))
				.andExpect(jsonPath("$.max_value_price", is(0.19)))
				.andExpect(jsonPath("$.older_value", is(olderValue)))
				.andExpect(jsonPath("$.newer_value", is(newerValue)));
	}

	// Get Crypto details for RTH with date filtering. This test is fixed and values
	// are hardcoded!!!
	@Test
	public void getCryptoDetailsETHWithDateFiltering_Positive() throws Exception {

		ResultActions response = mockMvc.perform(get("/api/cryptos/getCryptoDetails/{symbol}", CryptoSymbol.ETH.name())
				.param("toDate", "2022-01-25"));

		Map<String, Object> olderValue = new HashMap<>();
		olderValue.put("price", 3715.32);
		olderValue.put("timestamp", "2022-01-01T08:00:00Z");

		Map<String, Object> newerValue = new HashMap<>();
		newerValue.put("price", 2336.52);
		newerValue.put("timestamp", "2022-01-24T10:00:00Z");

		response.andExpect(status().isOk())
				.andDo(print())
				.andExpect(jsonPath("$.symbol", is(CryptoSymbol.ETH.name())))
				.andExpect(jsonPath("$.min_value_price", is(2336.52)))
				.andExpect(jsonPath("$.max_value_price", is(3828.11)))
				.andExpect(jsonPath("$.older_value", is(olderValue)))
				.andExpect(jsonPath("$.newer_value", is(newerValue)));

		ResultActions response2 = mockMvc.perform(get("/api/cryptos/getCryptoDetails/{symbol}", CryptoSymbol.ETH.name())
				.param("fromDate", "2022-01-04")		
				.param("toDate", "2022-01-06"));	
				
		olderValue.clear();
		olderValue.put("price", 3751.99);
		olderValue.put("timestamp", "2022-01-03T23:00:00Z");
		newerValue.clear();
		newerValue.put("price", 3792.87);
		newerValue.put("timestamp", "2022-01-05T17:00:00Z");

		response2.andExpect(status().isOk())
				.andDo(print())
				.andExpect(jsonPath("$.symbol", is(CryptoSymbol.ETH.name())))
				.andExpect(jsonPath("$.min_value_price", is(3751.99)))
				.andExpect(jsonPath("$.older_value", is(olderValue)))
				.andExpect(jsonPath("$.newer_value", is(newerValue)));
	}

	// Get Crypto details for a non supported crypto. This test is fixed and values are hardcoded!!!
	@Test
	public void getCryptoDetails_Negative() throws Exception {

		ResultActions response = mockMvc
				.perform(get("/api/cryptos/getCryptoDetails/{symbol}", "ADA"));

		response.andExpect(status().isInternalServerError())
				.andDo(print());
	}

	// Get Crypto details for BTC with WRONG date filtering.
	@Test
	public void getCryptoDetailsBTCWithDateFiltering_Negative() throws Exception {

		ResultActions response = mockMvc.perform(get("/api/cryptos/getCryptoDetails/{symbol}", CryptoSymbol.BTC.name())
				.param("fromDate", "2022-01-10")		
				.param("toDate", "2022-01-06"));

		response.andExpect(status().isOk())
				.andDo(print())
				.andExpect(jsonPath("$.symbol", is(CryptoSymbol.BTC.name())))
				.andExpect(jsonPath("$.min_value_price", is(nullValue())))
				.andExpect(jsonPath("$.max_value_price", is(nullValue())));
	}

	// Get first page with 100 cryptos and validate the total number, pages and
	// first elemets are correct due to normalization range sorting desc
	// This test is fixed and values are hardcoded!!!
	@Test
	public void getAllCryptosSortedByPriceAgainstNormalizedRangeDesc_Positive() throws Exception {

		ResultActions response = mockMvc.perform(get("/api/cryptos/getAllCryptosSortedByPriceAgainstNormalizedRangeDesc")
				.param("page", "0")
				.param("size", "100"));

		response.andExpect(status().isOk())
				.andDo(print())
				.andExpect(jsonPath("$.content", isA(ArrayList.class)))
				.andExpect(jsonPath("$.content", hasSize(100)))
				.andExpect(jsonPath("$.totalElements", is(450)))
				.andExpect(jsonPath("$.totalPages", is(5)))
				.andExpect(jsonPath("$.content[0].symbol", is(CryptoSymbol.BTC.name())))
				.andExpect(jsonPath("$.content[0].price", is(47722.66)))
				.andExpect(jsonPath("$.content[0].id", is(6)))
				.andExpect(jsonPath("$.content[1].id", is(10)))
				.andExpect(jsonPath("$.content[1].price", is(47336.98)));

	}


	// Get first page with 100 cryptos with date filtering and validate the total number, pages and
	// first elemets are correct due to normalization range sorting desc
	// This test is fixed and values are hardcoded!!!
	@Test
	public void getAllCryptosSortedByPriceAgainstNormalizedRangeDescWithDateFiltering_Positive() throws Exception {

		ResultActions response = mockMvc.perform(get("/api/cryptos/getAllCryptosSortedByPriceAgainstNormalizedRangeDesc")
				.param("page", "0")
				.param("size", "100")
				.param("fromDate", "2022-01-13")		
				.param("toDate", "2022-01-18"));

		response.andExpect(status().isOk())
				.andDo(print())
				.andExpect(jsonPath("$.content", isA(ArrayList.class)))
				.andExpect(jsonPath("$.content", hasSize(76)))
				.andExpect(jsonPath("$.totalElements", is(76)))
				.andExpect(jsonPath("$.totalPages", is(1)))
				.andExpect(jsonPath("$.content[0].symbol", is(CryptoSymbol.BTC.name())))
				.andExpect(jsonPath("$.content[0].price", is(44154.52)))
				.andExpect(jsonPath("$.content[0].id", is(39)))
				.andExpect(jsonPath("$.content[0].timestamp", is("2022-01-13T15:00:00Z")))
				.andExpect(jsonPath("$.content[1].id", is(38)));

	}

	// Get first page with 100 cryptos with WRONG date filtering
	@Test
	public void getAllCryptosSortedByPriceAgainstNormalizedRangeDescWithDateFiltering_Negative() throws Exception {

		ResultActions response = mockMvc.perform(get("/api/cryptos/getAllCryptosSortedByPriceAgainstNormalizedRangeDesc")
				.param("page", "0")
				.param("size", "100")
				.param("fromDate", "2022-01-21")		
				.param("toDate", "2022-01-18"));

		response.andExpect(status().isOk())
				.andDo(print())
				.andExpect(jsonPath("$.content", isA(ArrayList.class)))
				.andExpect(jsonPath("$.content", hasSize(0)))
				.andExpect(jsonPath("$.totalElements", is(0)))
				.andExpect(jsonPath("$.totalPages", is(0)));
	}

		// Get first page with 100 cryptos with invalid page and size
	@Test
	public void getAllCryptosSortedByPriceAgainstNormalizedRangeDesc_Negative() throws Exception {

		ResultActions response = mockMvc.perform(get("/api/cryptos/getAllCryptosSortedByPriceAgainstNormalizedRangeDesc")
				.param("page", "-10")
				.param("size", "dffsfd")
				.param("fromDate", "2022-01-15")		
				.param("toDate", "2022-01-18"));

				response.andExpect(status().isInternalServerError())
				.andDo(print());
	}

}
