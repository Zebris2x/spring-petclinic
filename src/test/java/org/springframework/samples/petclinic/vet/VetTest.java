package org.springframework.samples.petclinic.vet;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VetTest {

	@Test
	void newVetHasNoSpecialties() {
		Vet vet = new Vet();
		assertEquals(0, vet.getNrOfSpecialties());
		assertTrue(vet.getSpecialties().isEmpty());
	}

	@Test
	void addSpecialtyIncrementsCount() {
		Vet vet = new Vet();
		Specialty s = new Specialty();
		s.setName("radiology");
		vet.addSpecialty(s);
		assertEquals(1, vet.getNrOfSpecialties());
	}

	@Test
	void specialtiesReturnedSortedByName() {
		Vet vet = new Vet();

		Specialty s1 = new Specialty();
		s1.setName("surgery");

		Specialty s2 = new Specialty();
		s2.setName("dentistry");

		Specialty s3 = new Specialty();
		s3.setName("radiology");

		vet.addSpecialty(s1);
		vet.addSpecialty(s2);
		vet.addSpecialty(s3);

		List<Specialty> sorted = vet.getSpecialties();
		assertEquals(3, sorted.size());
		assertEquals("dentistry", sorted.get(0).getName());
		assertEquals("radiology", sorted.get(1).getName());
		assertEquals("surgery", sorted.get(2).getName());
	}

	@Test
	void duplicateSpecialtyNotAddedTwice() {
		Vet vet = new Vet();
		Specialty s = new Specialty();
		s.setName("radiology");
		vet.addSpecialty(s);
		vet.addSpecialty(s);
		assertEquals(1, vet.getNrOfSpecialties());
	}

	@Test
	void getSpecialtiesNotNull() {
		Vet vet = new Vet();
		assertNotNull(vet.getSpecialties());
	}

}
