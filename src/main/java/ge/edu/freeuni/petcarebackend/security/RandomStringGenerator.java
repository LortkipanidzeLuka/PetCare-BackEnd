package ge.edu.freeuni.petcarebackend.security;

import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class RandomStringGenerator {

    public String nextString() {
        for (int i = 0; i < buf.length; ++i) {
            buf[i] = symbols[random.nextInt(symbols.length)];
        }
        return new String(buf);
    }

    public static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static final String LOWER = UPPER.toLowerCase(Locale.ROOT);

    public static final String DIGITS = "0123456789";

    public static final String OTHER = "!@#$%^&*";

    public static final String ALL = UPPER + LOWER + DIGITS + OTHER;

    private final Random random;

    private final char[] symbols;

    private final char[] buf;

    public RandomStringGenerator(int length, Random random, String symbols) {
        if (length < 1) {
            throw new IllegalArgumentException();
        }
        if (symbols.length() < 2) {
            throw new IllegalArgumentException();
        }
        this.random = Objects.requireNonNull(random);
        this.symbols = symbols.toCharArray();
        this.buf = new char[length];
    }

    public RandomStringGenerator(int length, Random random) {
        this(length, random, ALL);
    }
}
