package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class OwnerControllerTest {

	private OwnerRepository owners;

	private OwnerController controller;

	private MockMvc mockMvc;

	@BeforeEach
	void setup() {
		owners = mock(OwnerRepository.class);
		controller = new OwnerController(owners);
		mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
	}

	@Test
	void initCreationForm() throws Exception {
		mockMvc.perform(get("/owners/new"))
			.andExpect(status().isOk())
			.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void processCreationFormWithErrors() throws Exception {
		// post incomplete data (validation should fail)
		mockMvc.perform(post("/owners/new").param("firstName", "").param("lastName", ""))
			.andExpect(status().isOk())
			.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void processCreationFormSuccess() throws Exception {
		when(owners.save(any(Owner.class))).thenAnswer(invocation -> {
			Owner arg = invocation.getArgument(0);
			arg.setId(42);
			return arg;
		});

		mockMvc
			.perform(post("/owners/new").param("firstName", "Joe")
				.param("lastName", "Bloggs")
				.param("address", "123 Street")
				.param("city", "Town")
				.param("telephone", "1234567890"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/owners/42"))
			.andExpect(flash().attributeExists("message"));
	}

	@Test
	void processFindFormNoResults() throws Exception {
		when(owners.findByLastNameStartingWith(anyString(), any(Pageable.class))).thenReturn(Page.empty());

		mockMvc.perform(get("/owners").param("lastName", "Nonexistent"))
			.andExpect(status().isOk())
			.andExpect(view().name("owners/findOwners"));
	}

	@Test
	void processFindFormSingleResult() throws Exception {
		Owner o = new Owner();
		o.setId(5);
		Page<Owner> page = new PageImpl<>(Collections.singletonList(o));
		when(owners.findByLastNameStartingWith(anyString(), any(Pageable.class))).thenReturn(page);

		mockMvc.perform(get("/owners").param("lastName", "Smith"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/owners/5"));
	}

	@Test
	void processFindFormMultipleResults() throws Exception {
		Owner o1 = new Owner();
		o1.setId(1);
		Owner o2 = new Owner();
		o2.setId(2);
		Page<Owner> page = new PageImpl<>(Arrays.asList(o1, o2), PageRequest.of(0, 5), 2);
		when(owners.findByLastNameStartingWith(anyString(), any(Pageable.class))).thenReturn(page);

		mockMvc.perform(get("/owners").param("lastName", "S"))
			.andExpect(status().isOk())
			.andExpect(view().name("owners/ownersList"))
			.andExpect(model().attributeExists("listOwners"))
			.andExpect(model().attribute("currentPage", 1));
	}

	@Test
	void showOwnerFound() throws Exception {
		Owner o = new Owner();
		o.setId(7);
		when(owners.findById(7)).thenReturn(Optional.of(o));

		mockMvc.perform(get("/owners/7"))
			.andExpect(status().isOk())
			.andExpect(view().name("owners/ownerDetails"))
			.andExpect(model().attributeExists("owner"));
	}

	@Test
	void showOwnerNotFound() {
		when(owners.findById(99)).thenReturn(Optional.empty());
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> controller.showOwner(99));
		assertTrue(ex.getMessage().contains("Owner not found"));
	}

}
