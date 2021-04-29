package com.srobber.common.util;

import java.security.SecureRandom;
import java.util.Random;

/**
 * TODO 考虑性能更高的ThreadLocalRandom
 *
 * 随机数工具类,线程安全
 *
 * @author chensenlai
 */
public class RandomUtil {
	
	private static final Random RANDOM = new SecureRandom();

	public static String random(final int count) {
		return random(count, false, false);
	}

	public static String random(final int count, final boolean letters, final boolean numbers) {
		return random(count, 0, 0, letters, numbers);
	}

	public static String random(final int count, final int start, final int end, final boolean letters,
			final boolean numbers) {
		return random(count, start, end, letters, numbers, null, RANDOM);
	}

	public static String random(int count, int start, int end, final boolean letters, final boolean numbers,
			final char[] chars, final Random random) {
		if (count == 0) {
			return "";
		} else if (count < 0) {
			throw new IllegalArgumentException("Requested random string length " + count + " is less than 0.");
		}
		if ((start == 0) && (end == 0)) {
			end = 'z' + 1;
			start = ' ';
			if (!letters && !numbers) {
				start = 0;
				end = Integer.MAX_VALUE;
			}
		}

		final char[] buffer = new char[count];
		final int gap = end - start;

		while (count-- != 0) {
			char ch;
			if (chars == null) {
				ch = (char) (random.nextInt(gap) + start);
			} else {
				ch = chars[random.nextInt(gap) + start];
			}
			if ((letters && Character.isLetter(ch)) || (numbers && Character.isDigit(ch)) || (!letters && !numbers)) {
				if ((ch >= 56320) && (ch <= 57343)) {
					if (count == 0) {
						count++;
					} else {
						// low surrogate, insert high surrogate after putting it
						// in
						buffer[count] = ch;
						count--;
						buffer[count] = (char) (55296 + random.nextInt(128));
					}
				} else if ((ch >= 55296) && (ch <= 56191)) {
					if (count == 0) {
						count++;
					} else {
						// high surrogate, insert low surrogate before putting
						// it in
						buffer[count] = (char) (56320 + random.nextInt(128));
						count--;
						buffer[count] = ch;
					}
				} else if ((ch >= 56192) && (ch <= 56319)) {
					// private high surrogate, no effing clue, so skip it
					count++;
				} else {
					buffer[count] = ch;
				}
			} else {
				count++;
			}
		}
		return new String(buffer);
	}

	public static String randomAlphanumeric(final int count) {
		return random(count, true, true);
	}

	public static String randomAscii(final int count) {
		return random(count, 32, 127, false, false);
	}

	public static String randomNumeric(final int count) {
		return random(count, false, true);
	}
}
