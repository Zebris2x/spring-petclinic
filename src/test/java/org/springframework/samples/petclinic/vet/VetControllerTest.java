package org.springframework.samples.petclinic.vet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

class VetControllerTest {

	private VetRepository vetRepository;

	private MockMvc mockMvc;

	@BeforeEach
	void setup() {
		vetRepository = mock(VetRepository.class);
		VetController controller = new VetController(vetRepository);
		mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
	}

	@Test
	void showVetListFirstPage() throws Exception {
		Vet v1 = new Vet();
		v1.setFirstName("James");
		v1.setLastName("Carter");

		Vet v2 = new Vet();
		v2.setFirstName("Helen");
		v2.setLastName("Leary");

		Page<Vet> page = new PageImpl<>(Arrays.asList(v1, v2), PageRequest.of(0, 5), 2);
		when(vetRepository.findAll(any(Pageable.class))).thenReturn(page);

		mockMvc.perform(get("/vets.html").param("page", "1"))
			.andExpect(status().isOk())
			.andExpect(view().name("vets/vetList"))
			.andExpect(model().attributeExists("listVets"))
			.andExpect(model().attribute("currentPage", 1))
			.andExpect(model().attribute("totalPages", 1))
			.andExpect(model().attribute("totalItems", 2L));
	}

	@Test
	void showVetListDefaultPage() throws Exception {
		Page<Vet> page = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 5), 0);
		when(vetRepository.findAll(any(Pageable.class))).thenReturn(page);

		mockMvc.perform(get("/vets.html"))
			.andExpect(status().isOk())
			.andExpect(view().name("vets/vetList"))
			.andExpect(model().attribute("currentPage", 1));
	}

	@Test
	void showResourcesVetList() throws Exception {
		Vet v1 = new Vet();
		v1.setFirstName("James");
		v1.setLastName("Carter");

		when(vetRepository.findAll()).thenReturn(Arrays.asList(v1));

		mockMvc.perform(get("/vets").header("Accept", "application/json")).andExpect(status().isOk());
	}

}
