package org.ckbj.chain;

import org.ckbj.type.Script;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class NetworkTest {

    @Test
    public void test() {
        Network mainnet = Network.MAINNET;
        Network testnet = Network.TESTNET;
        for (Contract.Type contractType: Contract.Type.values()) {
            Assertions.assertNotNull(mainnet.getContract(contractType));
            Assertions.assertNotNull(testnet.getContract(contractType));
        }
    }

    @Test
    public void testGetContractType() {
        Script script = Script.builder()
                .setCodeHash("0x9bd7e06f3ecf4be0f2fcd2188b23f1b9fcc88e5d4b65a8637b17723bbda3cce8")
                .setArgs("0xa3b8598e1d53e6c5e89e8acb6b4c34d3adb13f2b")
                .setHashType(Script.HashType.TYPE)
                .build();
        Assertions.assertEquals(
                Contract.Type.SECP256K1_BLAKE160_SIGHASH_ALL,
                Network.TESTNET.getContractType(script));
    }
}