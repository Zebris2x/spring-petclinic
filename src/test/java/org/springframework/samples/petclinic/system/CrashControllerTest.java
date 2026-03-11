package org.springframework.samples.petclinic.system;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import jakarta.servlet.ServletException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

class CrashControllerTest {

	private CrashController controller;

	private MockMvc mockMvc;

	@BeforeEach
	void setup() {
		controller = new CrashController();
		mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
	}

	@Test
	void triggerExceptionDirectCallThrowsRuntimeException() {
		RuntimeException ex = assertThrows(RuntimeException.class, () -> controller.triggerException());
		assertTrue(ex.getMessage().contains("Expected: controller used to showcase what"));
	}

	@Test
	void triggerExceptionEndpointReturnsInternalServerError() throws Exception {
		ServletException ex = assertThrows(ServletException.class, () -> mockMvc.perform(get("/oups")));
		assertTrue(ex.getMessage().contains("Expected: controller used to showcase what happens"));
	}

}
