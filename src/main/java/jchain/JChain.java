package jchain;

import jchain.domain.Blockchain;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JChain {
    private static final Logger logger = LogManager.getLogger(JChain.class);

    public static void main(String[] args) {
        Blockchain blockchain = new Blockchain();
        blockchain.generate();
    }
}
