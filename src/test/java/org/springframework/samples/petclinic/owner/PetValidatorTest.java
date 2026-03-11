package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PetValidatorTest {

	private PetValidator validator;

	private Pet pet;

	private Errors errors;

	@BeforeEach
	void setup() {
		validator = new PetValidator();
		pet = new Pet();
		errors = new BeanPropertyBindingResult(pet, "pet");
	}

	@Test
	void supportsOnlyPet() {
		assertTrue(validator.supports(Pet.class));
		assertFalse(validator.supports(Object.class));
	}

	@Test
	void rejectsEmptyName() {
		pet.setName("   ");
		pet.setBirthDate(LocalDate.now());
		pet.setType(new PetType());
		validator.validate(pet, errors);
		assertTrue(errors.hasFieldErrors("name"));
	}

	@Test
	void requiresTypeForNewPet() {
		pet.setName("Fido");
		pet.setBirthDate(LocalDate.now());
		pet.setType(null);
		// new pet by default
		validator.validate(pet, errors);
		assertTrue(errors.hasFieldErrors("type"));
	}

	@Test
	void allowsNullTypeForExistingPet() {
		pet.setName("Fido");
		pet.setBirthDate(LocalDate.now());
		pet.setType(null);
		pet.setId(5); // mark as existing
		validator.validate(pet, errors);
		assertFalse(errors.hasFieldErrors("type"));
	}

	@Test
	void requiresBirthDate() {
		pet.setName("Fido");
		pet.setType(new PetType());
		pet.setBirthDate(null);
		validator.validate(pet, errors);
		assertTrue(errors.hasFieldErrors("birthDate"));
	}

	@Test
	void validPetProducesNoErrors() {
		pet.setName("Buddy");
		pet.setType(new PetType());
		pet.setBirthDate(LocalDate.now());
		validator.validate(pet, errors);
		assertFalse(errors.hasErrors());
	}

}