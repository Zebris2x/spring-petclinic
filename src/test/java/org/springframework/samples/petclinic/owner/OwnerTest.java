package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.model.NamedEntity;

import java.time.LocalDate;
import java.util.Arrays;
import org.springframework.samples.petclinic.owner.Visit;

import static org.junit.jupiter.api.Assertions.*;

class OwnerTest {

	private Owner owner;

	private Pet pet1;

	private Pet pet2;

	@BeforeEach
	void setup() {
		owner = new Owner();
		pet1 = new Pet();
		pet1.setName("Fido");
		pet1.setId(1);
		pet1.setBirthDate(LocalDate.now());
		pet1.setType(new PetType());

		pet2 = new Pet();
		pet2.setName("Milo");
		pet2.setId(2);
		pet2.setBirthDate(LocalDate.now());
		pet2.setType(new PetType());
	}

	@Test
	void addPetOnlyIfNew() {
		pet1.setId(null);
		owner.addPet(pet1);
		assertTrue(owner.getPets().contains(pet1));
		// existing pet should not be added
		pet2.setId(5);
		owner.addPet(pet2);
		assertFalse(owner.getPets().contains(pet2));
	}

	@Test
	void getPetByNameCaseInsensitive() {
		// directly add to bypass new-check
		owner.getPets().add(pet1);
		owner.getPets().add(pet2);
		Pet found = owner.getPet("fIdO");
		assertSame(pet1, found);
	}

	@Test
	void getPetByNameIgnoreNewFlag() {
		owner.getPets().add(pet1);
		Pet newPet = new Pet();
		newPet.setName("Ghost");
		owner.getPets().add(newPet); // simulate new
		assertNotNull(owner.getPet("Ghost", false));
		assertNull(owner.getPet("Ghost", true));
	}

	@Test
	void getPetByIdOnlyNonNew() {
		owner.getPets().add(pet1);
		Pet found = owner.getPet(1);
		assertSame(pet1, found);
		// id not matching returns null
		assertNull(owner.getPet(999));
		// new pet with id should be ignored
		Pet newPet = new Pet();
		newPet.setId(3);
		owner.getPets().add(newPet);
		assertNull(owner.getPet(3));
	}

	@Test
	void toStringContainsImportantFields() {
		owner.setId(10);
		owner.setFirstName("John");
		owner.setLastName("Doe");
		owner.setAddress("123 Maple");
		owner.setCity("Springfield");
		owner.setTelephone("1234567890");
		String str = owner.toString();
		assertTrue(str.contains("John"));
		assertTrue(str.contains("Doe"));
		assertTrue(str.contains("123 Maple"));
		assertTrue(str.contains("Springfield"));
		assertTrue(str.contains("1234567890"));
	}

	@Test
	void addVisitValidAndNullGuards() {
		owner.getPets().add(pet1);
		Visit visit = new Visit();
		visit.setDate(LocalDate.now());
		// null petId should fail
		assertThrows(IllegalArgumentException.class, () -> owner.addVisit(null, visit));
		assertThrows(IllegalArgumentException.class, () -> owner.addVisit(1, null));
		// invalid pet id
		assertThrows(IllegalArgumentException.class, () -> owner.addVisit(999, visit));
		// success
		owner.addVisit(1, visit);
		assertTrue(pet1.getVisits().contains(visit));
	}

}