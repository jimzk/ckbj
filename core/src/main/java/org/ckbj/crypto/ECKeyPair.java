/*
 * Modifications Copyright 2022 fjchen7
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.ckbj.crypto;

import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.crypto.signers.HMacDSAKCalculator;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;
import org.ckbj.utils.Hex;

import java.math.BigInteger;
import java.security.KeyPair;
import java.util.Objects;

import static org.ckbj.crypto.Sign.CURVE;

/**
 * Elliptic Curve SECP-256k1 generated key pair.
 */
public class ECKeyPair {
    public static final int PRIVATE_KEY_SIZE = 32;
    private BigInteger privateKey;
    private Point publicKey;

    public ECKeyPair(BigInteger privateKey) {
        this.privateKey = privateKey;
        this.publicKey = Point.fromPrivateKey(privateKey);
    }

    public BigInteger getPrivateKey() {
        return privateKey;
    }

    public byte[] getEncodedPrivateKey() {
        byte[] encoded = Hex.toByteArray(privateKey, PRIVATE_KEY_SIZE);
        return encoded;
    }

    public Point getPublicKey() {
        return publicKey;
    }

    public byte[] getEncodedPublicKey(boolean compressed) {
        return publicKey.encode(compressed);
    }

    /**
     * Sign a hash with the private key of this key pair.
     *
     * @param message the message to sign
     * @return An {@link ECDSASignature} of the hash
     */
    public ECDSASignature sign(byte[] message) {
        ECDSASigner signer = new ECDSASigner(new HMacDSAKCalculator(new SHA256Digest()));

        ECPrivateKeyParameters privKey = new ECPrivateKeyParameters(privateKey, CURVE);
        signer.init(true, privKey);
        BigInteger[] components = signer.generateSignature(message);

        return new ECDSASignature(components[0], components[1]).toCanonicalised();
    }

    public static ECKeyPair create(KeyPair keyPair) {
        BCECPrivateKey privateKey = (BCECPrivateKey) keyPair.getPrivate();
        BigInteger privateKeyValue = privateKey.getD();
        return new ECKeyPair(privateKeyValue);
    }

    public static ECKeyPair create(BigInteger privateKey) {
        return new ECKeyPair(privateKey);
    }

    public static ECKeyPair create(byte[] privateKey) {
        return create(new BigInteger(1, privateKey));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ECKeyPair ecKeyPair = (ECKeyPair) o;

        if (!Objects.equals(privateKey, ecKeyPair.privateKey)) return false;
        return Objects.equals(publicKey, ecKeyPair.publicKey);
    }

    @Override
    public int hashCode() {
        int result = privateKey != null ? privateKey.hashCode() : 0;
        result = 31 * result + (publicKey != null ? publicKey.hashCode() : 0);
        return result;
    }

    public static class Point {
        ECPoint point;

        public Point(BigInteger x, BigInteger y) {
            this.point = CURVE.getCurve().createPoint(x, y);
        }

        public Point(ECPoint point) {
            this.point = point;
        }

        public ECPoint getECPoint() {
            return point;
        }

        /**
         * Encode the point itself to byte array representation.
         *
         * @param compressed whether the returned byte array is uncompressed (0x04 prefix) or compressed (0x02 or 0x03 prefix) form.
         * @return the encoded byte array
         */
        public byte[] encode(boolean compressed) {
            return point.getEncoded(compressed);
        }

        /**
         * Encode byte array to Point.
         *
         * @param encoded compressed or uncompressed byte array representing the point.
         * @return the decoded Point.
         */
        public static Point decode(byte[] encoded) {
            ECPoint point = CURVE.getCurve().decodePoint(encoded);
            return new Point(point);
        }

        /**
         * Returns public key from the given private key.
         *
         * @param privateKey the private key to derive the public key from
         * @return Point public key
         */
        public static Point fromPrivateKey(BigInteger privateKey) {
            /*
             * TODO: FixedPointCombMultiplier currently doesn't support scalars longer than the group
             * order, but that could change in future versions.
             */
            if (privateKey.bitLength() > CURVE.getN().bitLength()) {
                privateKey = privateKey.mod(CURVE.getN());
            }
            ECPoint point = new FixedPointCombMultiplier().multiply(CURVE.getG(), privateKey);
            return new Point(point);
        }

        @Override
        public String toString() {
            return Hex.toHexString(encode(false));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Point point1 = (Point) o;

            return point.equals(point1.point);
        }

        @Override
        public int hashCode() {
            return point.hashCode();
        }
    }
}
