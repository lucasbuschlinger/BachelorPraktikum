package de.opendiabetes.vault.plugin.importer.crawler;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.logging.Logger;

/**
 * Class for generating a password hash.
 */
public class SecurityHelper {

    /**
     * The number of iterations for the hashing algorithm.
     * Decreasing this speeds down startup time and can be useful during
     * testing, but it also makes it easier for brute force attackers.
     */
    private static final int ITERATION_COUNT = 40000;

    /**
     * The length of the key.
     * Other values give me java.security.InvalidKeyException: Illegal key
     * size or default parameters.
     */
    private static final int KEY_LENGTH = 128;

    /**
     * Creates a hash for the given username and password.
     *
     * @param username - username of the user.
     * @param password - password of the user.
     * @param logger - a logger instance.
     * @return an array containing the encrypted and the decrypted password.
     * @throws GeneralSecurityException - thrown if there was an error hashing the password.
     * @throws IOException - thrown if there was an input/ouptput error.
     */
    String[] createHash(final String username, final String password, final Logger logger)
            throws GeneralSecurityException, IOException {
        logger.info("Inside Class CreateSecurePasswordClass, Method createHash");

        // The salt (probably) can be stored along with the encrypted data
        byte[] salt = new String("12345678").getBytes();

        SecretKeySpec key = createSecretKey(username.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH, logger);
        String encryptedPassword = encrypt(password, key);
        String decryptedPassword = decrypt(encryptedPassword, key, logger);
        return new String[]{encryptedPassword, decryptedPassword};

    }

    /**
     * Creates a secret key with a password.
     *
     * @param password - the password to encrypt.
     * @param salt - a salt to add to the hash.
     * @param iterationCount - the times the hashing function should iterate.
     * @param keyLength - the final key length.
     * @param logger - a logger instance.
     * @return a secret key specification.
     * @throws NoSuchAlgorithmException - thrown if the hashing algorithm is unknown at system level.
     * @throws InvalidKeySpecException - thrown if the key could not be generated.
     */
    protected static SecretKeySpec createSecretKey(final char[] password,
                                                   final byte[] salt,
                                                   final int iterationCount,
                                                   final int keyLength,
                                                   final Logger logger)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        logger.info("Inside Class CreateSecurePasswordClass, Method createSecretKey");
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        PBEKeySpec keySpec = new PBEKeySpec(password, salt, iterationCount, keyLength);
        SecretKey keyTmp = keyFactory.generateSecret(keySpec);
        return new SecretKeySpec(keyTmp.getEncoded(), "AES");
    }

    /**
     * Encrypts a given property with the given key.
     *
     * @param property - the property to encrypt.
     * @param key - a key specification.
     * @return the encrypted hash.
     * @throws GeneralSecurityException - thrown if there was an error creating the hash.
     * @throws UnsupportedEncodingException - thrown if there was an error encoding the property.
     */
    private static String encrypt(final String property, final SecretKeySpec key)
            throws GeneralSecurityException, UnsupportedEncodingException {
        Cipher pbeCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        pbeCipher.init(Cipher.ENCRYPT_MODE, key);
        AlgorithmParameters parameters = pbeCipher.getParameters();
        IvParameterSpec ivParameterSpec = parameters.getParameterSpec(IvParameterSpec.class);
        byte[] cryptoText = pbeCipher.doFinal(property.getBytes("UTF-8"));
        byte[] iv = ivParameterSpec.getIV();
        return base64Encode(iv) + ":" + base64Encode(cryptoText);
    }

    /**
     * Creates the base64 representation of the given byte array.
     *
     * @param bytes - a byte array.
     * @return a base64 encoded string.
     */
    private static String base64Encode(final byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * Decrypts the given property with the given key.
     *
     * @param string - the hashed string.
     * @param key - a key specification.
     * @param logger - a logger instance.
     * @return the decrypted string.
     * @throws GeneralSecurityException - thrown if there was an error decrypting.
     * @throws IOException - thrown if there was any input/output error.
     */
    protected static String decrypt(final String string, final SecretKeySpec key, final Logger logger)
            throws GeneralSecurityException, IOException {
        logger.info("Inside Class CreateSecurePasswordClass, Method decrypt");
        String iv = string.split(":")[0];
        String property = string.split(":")[1];
        Cipher pbeCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        pbeCipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(base64Decode(iv)));
        return new String(pbeCipher.doFinal(base64Decode(property)), "UTF-8");
    }

    /**
     * Decodes the given base64 string into a byte array.
     *
     * @param property - a base64 encoded string.
     * @return a byte array.
     * @throws IOException - thrown if there was an error decoding.
     */
    private static byte[] base64Decode(final String property) throws IOException {
        return Base64.getDecoder().decode(property);
    }
}
