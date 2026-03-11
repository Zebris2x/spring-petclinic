package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class PetTypeFormatterTest {

	private PetTypeFormatter formatter;

	@BeforeEach
	void setup() {
		// create a Mockito mock for the repository
		PetTypeRepository repo = org.mockito.Mockito.mock(PetTypeRepository.class);
		PetType cat = new PetType();
		cat.setName("Cat");
		PetType dog = new PetType();
		dog.setName("Dog");
		java.util.List<PetType> types = Arrays.asList(cat, dog);
		org.mockito.Mockito.when(repo.findPetTypes()).thenReturn(types);
		formatter = new PetTypeFormatter(repo);
	}

	@Test
	void printReturnsNameOrPlaceholder() {
		PetType p = new PetType();
		assertEquals("<null>", formatter.print(p, Locale.ENGLISH));
		p.setName("Fish");
		assertEquals("Fish", formatter.print(p, Locale.ENGLISH));
	}

	@Test
	void parseFindsExistingType() throws ParseException {
		PetType result = formatter.parse("Cat", Locale.ENGLISH);
		assertNotNull(result);
		assertEquals("Cat", result.getName());
	}

	@Test
	void parseThrowsForUnknown() {
		assertThrows(ParseException.class, () -> formatter.parse("Bird", Locale.ENGLISH));
	}

}