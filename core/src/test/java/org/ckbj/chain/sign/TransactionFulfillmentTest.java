package org.ckbj.chain.sign;

import org.ckbj.chain.TransactionFulfillment;
import org.ckbj.chain.contract.Secp256k1Blake160SighashAll;
import org.ckbj.crypto.ECKeyPair;
import org.ckbj.molecule.Serializer;
import org.ckbj.type.*;
import org.ckbj.utils.Hex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class TransactionFulfillmentTest {

    @Test
    public void testFulfill() {
        Transaction tx = Transaction.builder()
                .addCellDep(CellDep.DepType.DEP_GROUP, "0xf8de3bb47d055cdf460d93a2a6e1b05f7432f9777c8c474abf4eec1d4aee5d37", 0)
                .addCellDep(CellDep.DepType.DEP_GROUP, "0xec26b0f85ed839ece5f11c4c4e837ec359f5adc4420410f6453b1f6b60fb96a6", 0)
                .addInput("0xbaf3371f487a0d40f8ebc341a34b93a2d36e1d9f77b9533fb8c579c87958b7aa", 0)
                .addOutput(Cell.builder()
                                   .setCapacity(0x2540be400L)
                                   .setLock(Script.builder()
                                                    .setCodeHash("0x9bd7e06f3ecf4be0f2fcd2188b23f1b9fcc88e5d4b65a8637b17723bbda3cce8")
                                                    .setArgs("0x05a1fabfa84db9e538e2e7fe3ca9adf849f55ce0")
                                                    .build())
                                   .setData(new byte[]{})
                                   .build())
                .addOutput(Cell.builder()
                                   .setCapacity(0x368f7cadfb00L)
                                   .setLock(Script.builder()
                                                    .setCodeHash("0x9bd7e06f3ecf4be0f2fcd2188b23f1b9fcc88e5d4b65a8637b17723bbda3cce8")
                                                    .setArgs("0xa3b8598e1d53e6c5e89e8acb6b4c34d3adb13f2b")
                                                    .build())
                                   .setData(new byte[]{})
                                   .build())
                .addWitness(Serializer.serialize(WitnessArgs.builder().setLock(new byte[65]).build()))
                .build();

        TransactionFulfillment transactionFulfillment = new TransactionFulfillment();
        ECKeyPair ecKeyPair = ECKeyPair.create("0x6fc935dad260867c749cf1ba6602d5f5ed7fb1131f1beb65be2d342e912eaafe");
        transactionFulfillment.register(Secp256k1Blake160SighashAll.fulfillment(ecKeyPair));

        List<Cell> inputDetails = new ArrayList<>();
        inputDetails.add(Cell.builder()
                                 .setCapacity(60000000000000L)
                                 .setLock(Script.builder()
                                                  .setCodeHash("0x9bd7e06f3ecf4be0f2fcd2188b23f1b9fcc88e5d4b65a8637b17723bbda3cce8")
                                                  .setArgs("0xa3b8598e1d53e6c5e89e8acb6b4c34d3adb13f2b")
                                                  .build())
                                 .build());

        transactionFulfillment.fulfill(tx, inputDetails);
        Assertions.assertArrayEquals(
                Hex.toByteArray("0x550000001000000055000000550000004100000090b18cc17b8c67e20075ffcffe82d079e0b6a78cb3184157d78962bdd5a648d82c9bc8e1bbe87e7b8b0661440c1060f939be85d26742148e08dc58743a900df401"),
                tx.getWitnesses().get(0));
    }
}