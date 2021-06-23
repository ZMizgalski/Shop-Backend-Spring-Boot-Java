package com.shop.Main.security;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.security.crypto.password.PasswordEncoder;

import static de.mkammerer.argon2.Argon2Factory.Argon2Types.ARGON2id;

@Slf4j
public class Argon2idPasswordEncoder implements PasswordEncoder {
    private static final int ITERATIONS = 10;
    private static final int MEMORY_TO_USE = 4049;
    private static final int PARALLELISM = 4;

    private static final Argon2 argon2idHasher = Argon2Factory.create(ARGON2id);

    @Override
    public String encode(CharSequence rawPassword) {
        return argon2idHash(rawPassword);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return verifyArgon2idHash(rawPassword, encodedPassword);
    }

    private static String argon2idHash(CharSequence plainTextPassword) {
        char[] sha512HashedPassword = sha512Hash(plainTextPassword.toString());
        String hash = "";

        try {
            hash = argon2idHasher.hash(ITERATIONS, MEMORY_TO_USE, PARALLELISM, sha512HashedPassword);
        } finally {
            argon2idHasher.wipeArray(sha512HashedPassword);
        }
        return hash;
    }

    private static boolean verifyArgon2idHash(CharSequence plainTextPassword, CharSequence hashedPassword) {
        char[] sha512HashedPassword = sha512Hash(plainTextPassword.toString());
        boolean isMatched;

        try {
            isMatched = argon2idHasher.verify(hashedPassword.toString(), sha512HashedPassword);
        } finally {
            argon2idHasher.wipeArray(sha512HashedPassword);
        }

        return isMatched;
    }

    private static char[] sha512Hash(String textToHash) {
        SHA3.Digest512 digest512 = new SHA3.Digest512();
        byte[] digest = digest512.digest(textToHash.getBytes());
        String hashed = Hex.toHexString(digest);
        return hashed.toCharArray();
    }
}
