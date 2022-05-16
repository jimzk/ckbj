package org.ckbj.chain.sign;

import org.ckbj.chain.Contract;
import org.ckbj.chain.Network;
import org.ckbj.type.Script;

import java.util.HashSet;
import java.util.Set;

public abstract class StandardLockScriptSigner extends LockScriptSigner {
    protected Set<Network> networks;

    public StandardLockScriptSigner() {
        networks = new HashSet<>();
        networks.add(Network.MAINNET);
        networks.add(Network.TESTNET);
    }

    @Override
    public boolean match(Script script) {
        if (script == null) {
            return false;
        }

        boolean contractUsed = false;
        for (Network network: networks) {
            if (network.getContractType(script) == getContractType()) {
                contractUsed = true;
            }
        }
        if (contractUsed) {
            return doMatch(script.getArgs());
        } else {
            return false;
        }
    }

    protected abstract boolean doMatch(byte[] scriptArgs);

    public abstract Contract.Type getContractType();
}
