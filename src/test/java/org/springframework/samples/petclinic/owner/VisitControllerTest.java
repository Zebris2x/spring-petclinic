package org.springframework.samples.petclinic.owner;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class VisitControllerTest {

	private OwnerRepository owners;

	private VisitController controller;

	@BeforeEach
	void setup() {
		owners = mock(OwnerRepository.class);
		controller = new VisitController(owners);
	}

	@Test
	void setAllowedFieldsDisallowsId() {
		WebDataBinder binder = new WebDataBinder(new Object());
		controller.setAllowedFields(binder);
		assertEquals(1, binder.getDisallowedFields().length);
		assertEquals("id", binder.getDisallowedFields()[0]);
	}

	@Test
	void loadPetWithVisitLoadsOwnerAndPetAndCreatesVisit() {
		Owner owner = new Owner();
		owner.setId(10);
		Pet pet = new Pet();
		pet.setName("Fido");
		owner.addPet(pet);
		pet.setId(20);
		when(owners.findById(10)).thenReturn(Optional.of(owner));

		Map<String, Object> model = new HashMap<>();
		Visit visit = controller.loadPetWithVisit(10, 20, model);

		assertNotNull(visit);
		assertEquals(pet, model.get("pet"));
		assertEquals(owner, model.get("owner"));
		assertTrue(pet.getVisits().contains(visit));
	}

	@Test
	void loadPetWithVisitThrowsWhenOwnerMissing() {
		when(owners.findById(10)).thenReturn(Optional.empty());

		Map<String, Object> model = new HashMap<>();
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
				() -> controller.loadPetWithVisit(10, 20, model));
		assertTrue(ex.getMessage().contains("Owner not found with id: 10"));
	}

	@Test
	void loadPetWithVisitThrowsWhenPetMissing() {
		Owner owner = new Owner();
		owner.setId(10);
		when(owners.findById(10)).thenReturn(Optional.of(owner));

		Map<String, Object> model = new HashMap<>();
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
				() -> controller.loadPetWithVisit(10, 99, model));
		assertTrue(ex.getMessage().contains("Pet with id 99 not found"));
	}

	@Test
	void initNewVisitFormReturnsCreateOrUpdateView() {
		assertEquals("pets/createOrUpdateVisitForm", controller.initNewVisitForm());
	}

	@Test
	void processNewVisitFormReturnsFormWhenValidationErrors() {
		Owner owner = new Owner();
		Visit visit = new Visit();
		BindingResult result = new BeanPropertyBindingResult(visit, "visit");
		result.reject("error");
		RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

		String view = controller.processNewVisitForm(owner, 20, visit, result, redirectAttributes);

		assertEquals("pets/createOrUpdateVisitForm", view);
	}

	@Test
	void processNewVisitFormSavesOwnerAndRedirectsWhenValid() {
		Owner owner = new Owner();
		owner.setId(10);
		Pet pet = new Pet();
		pet.setName("Fido");
		owner.addPet(pet);
		pet.setId(20);
		Visit visit = new Visit();
		BindingResult result = new BeanPropertyBindingResult(visit, "visit");
		RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

		String view = controller.processNewVisitForm(owner, 20, visit, result, redirectAttributes);

		assertEquals("redirect:/owners/{ownerId}", view);
		assertEquals("Your visit has been booked", redirectAttributes.getFlashAttributes().get("message"));
		assertEquals(1, pet.getVisits().size());
		verify(owners).save(owner);
	}

}
