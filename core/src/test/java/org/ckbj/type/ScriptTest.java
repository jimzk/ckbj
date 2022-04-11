package org.ckbj.type;

import org.ckbj.utils.Hex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ScriptTest {
    @Test
    public void testHash() {
        Script script = Script.builder()
                .setCodeHash("0x61d7e01908bafa29d742e37b470dc906fb05c2115b0beba7b1c4fa3e66ca3e44")
                .setHashType(Script.HashType.DATA)
                .build();
        Assertions.assertArrayEquals(
                Hex.toByteArray("0x63c5fece50cdeb3978b02b43d3cff43809310b7ac89519bc717309ecc58904d9"),
                script.hash());

    }
}
