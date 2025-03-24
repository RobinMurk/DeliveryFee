package com.deliveryFee.Main;

import com.deliveryFee.Main.API.BusinessRules;
import com.deliveryFee.Main.API.Controller;
import com.deliveryFee.Main.API.FeeService;
import com.deliveryFee.Main.database.WeatherDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(MockitoExtension.class)
public class MainTest {

	@Autowired
	private MockMvc mockMvc;

	@Mock
	private FeeService feeService;

	@Mock
	private WeatherDataRepository weatherDataRepository;

	@Mock
	private BusinessRules businessRules;


	@InjectMocks
	private Controller controller;

	@BeforeEach
	public void setup() {
		mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
	}

	@Test
	public void testGetRuleset() throws Exception {
		String city = "Tallinn";
		String vehicle = "Car";

		lenient().when(feeService.getRules()).thenReturn(List.of(
				"TALLINN_CAR_RBF",
				"TALLINN_SCOOTER_RBF",
				"TALLINN_BIKE_RBF",
				"TARTU_CAR_RBF",
				"TARTU_SCOOTER_RBF",
				"TARTU_BIKE_RBF",
				"PARNU_CAR_RBF",
				"PARNU_SCOOTER_RBF",
				"PARNU_BIKE_RBF",
				"EXTRA_FOR_RAIN",
				"EXTRA_FOR_SNOW",
				"EXTRA_FOR_WIND",
				"EXTRA_FOR_LOWER_TEMP",
				"EXTRA_FOR_LOW_TEMP"
		));

		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/get-ruleset"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.rules").isArray());

	}

	@Test
	public void testChangingRules() throws Exception {
		List<String> rules = List.of("TARTU_CAR_RBF","PARNU_SCOOTER_RBF","EXTRA_FOR_LOW_TEMP");
		mockMvc.perform(post("/api/v1/update-rules")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"ruleset\":[{\"rule\":\" TARTU_CAR_RBF\",\"value\": 5}]}")
				)
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value("SUCCESS"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.values").isArray());
	}


	/**
	@Test
	public void testWithoutDataInput() throws Exception {
		assertEquals("should be the same", -1,feeService.calculateFee("Tallinn", "24325"));
	}
	**/
}
