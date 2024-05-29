package com.github.nextpertise.keycloak.sha1hashprovider;

import org.jboss.logging.Logger;
import org.keycloak.credential.hash.PasswordHashProvider;
import org.keycloak.models.PasswordPolicy;
import org.keycloak.models.credential.PasswordCredentialModel;

import java.math.BigInteger;
import java.security.MessageDigest;

public class SHA1HashProvider implements PasswordHashProvider {

	private static final Logger logger = Logger.getLogger(SHA1HashProvider.class);

	private final String providerId;
	public static final String ALGORITHM = "SHA-1";

	public SHA1HashProvider(String providerId) {
		this.providerId = providerId;
	}

	@Override
	public void close() {
	}

	@Override
	public boolean policyCheck(PasswordPolicy policy, PasswordCredentialModel credential) {
		return this.providerId.equals(credential.getPasswordCredentialData().getAlgorithm());
	}

	@Override
	public PasswordCredentialModel encodedCredential(String rawPassword, int iterations) {
		String encodedPassword = this.encode(rawPassword, iterations);
		return PasswordCredentialModel.createFromValues(this.providerId, new byte[0], iterations, encodedPassword);
	}

	@Override
	public boolean verify(String rawPassword, PasswordCredentialModel credential) {
		String salt = new String(credential.getPasswordSecretData().getSalt(), java.nio.charset.StandardCharsets.UTF_8);
		logger.infof("salt: '%s'", salt);
		String encodedPassword = this.encode(salt + rawPassword, credential.getPasswordCredentialData().getHashIterations());
		logger.infof("generated hash: '%s'", encodedPassword);
		String hash = credential.getPasswordSecretData().getValue();
		logger.infof("database hash: '%s'", hash);
		Boolean result = encodedPassword.equals(hash);
		logger.infof("result: '%b'", result);
		return result;
	}

	@Override
	public String encode(String rawPassword, int iterations) {
		try {
			MessageDigest md = MessageDigest.getInstance(ALGORITHM);
			md.update(rawPassword.getBytes());

			// convert the digest byte[] to BigInteger
			var aux = new BigInteger(1, md.digest());

			// convert BigInteger to 40-char lowercase string using leading 0s
			return String.format("%040x", aux);
		} catch (Exception e) {
			// fail silently
		}

		return null;
	}

}
