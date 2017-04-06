/*******************************************************************************
 * Copyright (c) 2015 Institute for Pervasive Computing, ETH Zurich and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution.
 * 
 * The Eclipse Public License is available at
 *    http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 *    http://www.eclipse.org/org/documents/edl-v10.html.
 * 
 * Contributors:
 *    Matthias Kovatsch - creator and main architect
 *    Stefan Jucker - DTLS implementation
 ******************************************************************************/
package org.eclipse.californium.scandium.dtls;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Date;

import org.eclipse.californium.scandium.util.ByteArrayUtils;

/**
 * A 32-byte value provided by the client and the server in the
 * {@link ClientHello} respectively in the {@link ServerHello} used later in the
 * protocol to compute the premaster secret. See <a
 * href="http://tools.ietf.org/html/rfc5246#appendix-A.4.1">RFC 5246</a> for the
 * message format.
 */
public class Random {

	// Members ////////////////////////////////////////////////////////

	/**
	 * The current time and date in standard UNIX 32-bit format + 28 bytes
	 * generated by a secure random number generator
	 */
	private final byte[] randomBytes;

	// Constructor ////////////////////////////////////////////////////

	public Random() {
		this(new SecureRandom());
	}

	public Random(SecureRandom generator) {
		int gmtUnixTime = (int) (System.currentTimeMillis() / 1000);

		this.randomBytes = new byte[32];
		// fill all 32 bytes with random bytes
		generator.nextBytes(this.randomBytes);

		// overwrite the first 4 bytes with the UNIX time
		this.randomBytes[0] = (byte) (gmtUnixTime >> 24);
		this.randomBytes[1] = (byte) (gmtUnixTime >> 16);
		this.randomBytes[2] = (byte) (gmtUnixTime >> 8);
		this.randomBytes[3] = (byte) gmtUnixTime;
	}

	/**
	 * Sets the random bytes explicitly.
	 * 
	 * @param randomBytes the bytes to use
	 * @throws NullPointerException if the given array is <code>null</code>
	 * @throws IllegalArgumentException if the given array's length is not 32
	 */
	public Random(byte[] randomBytes) {
		if (randomBytes == null) {
			throw new NullPointerException("Random bytes must not be null");
		} else if (randomBytes.length != 32) {
			throw new IllegalArgumentException("Random bytes array's length must be 32");
		} else {
			this.randomBytes = Arrays.copyOf(randomBytes, randomBytes.length);
		}
	}

	// Methods ////////////////////////////////////////////////////////

	/**
	 * Gets the random bytes.
	 * 
	 * @return the random bytes
	 */
	public byte[] getRandomBytes() {
		return Arrays.copyOf(randomBytes, randomBytes.length);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		// get the UNIX timestamp from the first 4 bytes
		byte b0 = randomBytes[0];
		byte b1 = randomBytes[1];
		byte b2 = randomBytes[2];
		byte b3 = randomBytes[3];
		
		long gmtUnixTime = ((0xFF & b0) << 24) | ((0xFF & b1) << 16) | ((0xFF & b2) << 8) | (0xFF & b3);

		Date date = new Date(gmtUnixTime * 1000L);

		sb.append("\t\t\tGMT Unix Time: ").append(date).append("\n");
		
		// output the remaining 28 random bytes
		byte[] rand = Arrays.copyOfRange(randomBytes, 4, 32);
		sb.append("\t\t\tRandom Bytes: ").append(ByteArrayUtils.toHexString(rand)).append("\n");

		return sb.toString();
	}

}
