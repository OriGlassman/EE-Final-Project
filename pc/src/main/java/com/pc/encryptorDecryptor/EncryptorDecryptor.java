package com.pc.encryptorDecryptor;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import static com.pc.configuration.Constants.MAX_ENCODED_LENGTH_BYTES;
import static com.pc.configuration.Parameters.ivLength;


public class EncryptorDecryptor {
	public static final String encryptionAlgorithm = "AES";
	public static final int symmetricKeyLength = 256;
	private static final String encryptionMethod = encryptionAlgorithm + "/CBC/NoPadding";

	/**
	 * XORs the image bytes with the generated pseudo random bytes
	 * @param imageBytes - The image bytes
	 * @param generatedXorBytes - The generated pseudo random bytes
	 * @return the bytes XORed
	 */
	public static byte[] xorPaddedImage(byte[] imageBytes, byte[] generatedXorBytes) {
		final byte[] xoredImage = new byte[imageBytes.length];
		for (int i = 0; i < imageBytes.length; i++) {
			xoredImage[i] = (byte) (imageBytes[i] ^ generatedXorBytes[i]);
		}

		return xoredImage;
	}

	/**
	 * Generates the pseudo random bytes with AES
	 * @param skeySpec - The secret key spec
	 * @param iv - The IV
	 * @return the generated bytes
	 */
	public static byte[] generateXorBytes(SecretKeySpec skeySpec, IvParameterSpec iv)  {
		final byte[] cipherIV = new byte[16];
		final byte[] ourIV = iv.getIV();

		System.arraycopy(ourIV, 0, cipherIV, 0, ivLength);

		final byte[] randomBytes = new byte[MAX_ENCODED_LENGTH_BYTES];
		final Random random = new Random(ByteBuffer.wrap(ourIV).getLong());
		random.nextBytes(randomBytes);

		try {
			final Cipher cipher = Cipher.getInstance(encryptionMethod);
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(cipherIV));
			return cipher.doFinal(randomBytes);
		} catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException |
				InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchPaddingException e) {
			e.printStackTrace();
		}
		return null;
	}
}
