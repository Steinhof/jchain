package jchain.domain;

import jchain.utils.StringUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class Blockchain {
    private static final Logger logger = LogManager.getLogger(Blockchain.class);

    private static final ArrayList<Block> chain = new ArrayList<>();
    private static final int DIFFICULTY = 5;

    public void generate() {
        Blockchain.chain.add(new Block("azula", "0"));
        Blockchain.chain.get(0).mine(Blockchain.DIFFICULTY);

        Blockchain.chain.add(new Block("azula 1", Blockchain.chain.get(Blockchain.chain.size() - 1).hash));
        Blockchain.chain.get(0).mine(Blockchain.DIFFICULTY);

        logger.info(StringUtil.getJson(Blockchain.chain));
    }

    public boolean checkIntegrity() {
        for (int i = 1; i < Blockchain.chain.size(); i++) {
            Block previousBlock = Blockchain.chain.get(i - 1);
            Block currentBlock = Blockchain.chain.get(i);

            if (!currentBlock.hash.equals(currentBlock.calculateHash())) {
                logger.error("Current hashes are not equal");
                return false;
            }

            if (!previousBlock.hash.equals(currentBlock.previousHash)) {
                logger.error("Previous hashes are not equal");
                return false;
            }
        }

        return true;
    }
}
