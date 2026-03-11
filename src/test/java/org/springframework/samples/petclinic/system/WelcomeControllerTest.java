package org.springframework.samples.petclinic.system;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

class WelcomeControllerTest {

	private WelcomeController controller;

	private MockMvc mockMvc;

	@BeforeEach
	void setup() {
		controller = new WelcomeController();
		mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
	}

	@Test
	void welcomeDirectCallReturnsWelcomeView() {
		assertEquals("welcome", controller.welcome());
	}

	@Test
	void welcomeEndpointReturnsWelcomeView() throws Exception {
		mockMvc.perform(get("/")).andExpect(status().isOk()).andExpect(view().name("welcome"));
	}

}
