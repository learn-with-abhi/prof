package io.letstry.prof.utils;

import java.util.Random;
import java.util.function.BiFunction;

import org.springframework.stereotype.Component;

@Component
public class ObfuscatorUtil {

	private static final String UPPER_ALPHA = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String LOWER_ALPHA = UPPER_ALPHA.toLowerCase();
	private static final String numbers = "0123456789";
	private Random r = new Random();
	private BiFunction<String, Integer, Character> random = (container, length) -> container.charAt(r.nextInt(length));
	private BiFunction<String, Character, Boolean> isPresent = (container, input) -> container
			.contains(String.valueOf(input));

	public String getMaskedValue(String input) {
		char[] cArr = input.toCharArray();
		for (int i = 0; i < cArr.length; i++) {
			if (isPresent.apply(UPPER_ALPHA, cArr[i])) {
				cArr[i] = random.apply(UPPER_ALPHA, UPPER_ALPHA.length());
			} else if (isPresent.apply(LOWER_ALPHA, cArr[i])) {
				cArr[i] = random.apply(LOWER_ALPHA, LOWER_ALPHA.length());
			} else if (isPresent.apply(numbers, cArr[i])) {
				cArr[i] = random.apply(numbers, numbers.length());
			}
		}

		return String.valueOf(cArr);
	}

}