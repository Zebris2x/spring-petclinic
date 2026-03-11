package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class PetTest {

	@Test
	void addVisitShouldAppearInCollection() {
		Pet pet = new Pet();
		Visit visit = new Visit();
		visit.setDate(LocalDate.of(2023, 1, 1));
		assertTrue(pet.getVisits().isEmpty());
		pet.addVisit(visit);
		assertTrue(pet.getVisits().contains(visit));
	}

	@Test
	void gettersAndSetters() {
		Pet pet = new Pet();
		pet.setName("Spike");
		pet.setId(7);
		pet.setBirthDate(LocalDate.of(2022, 5, 5));
		PetType type = new PetType();
		type.setName("Lizard");
		pet.setType(type);
		assertEquals("Spike", pet.getName());
		assertEquals(7, pet.getId());
		assertEquals(LocalDate.of(2022, 5, 5), pet.getBirthDate());
		assertSame(type, pet.getType());
	}

}