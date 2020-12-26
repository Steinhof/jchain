package jchain.domain;

import jchain.utils.StringUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;

public class Block {
    public String hash;
    public final String previousHash;
    public final String data;
    private int nonce;

    private static final Logger logger = LogManager.getLogger(Block.class);

    public Block(String data, String previousHash) {
        this.data = data;
        this.previousHash = previousHash;
        this.hash = this.calculateHash();
    }

    public String calculateHash() {
        long timeStamp = new Date().getTime();

        return StringUtil.applySha256(this.previousHash + timeStamp + this.nonce + this.data);
    }

    public void mine(int difficulty) {
        String target = StringUtil.getDifficulty(difficulty);

        while (!hash.substring(0, difficulty).equals(target)) {
            this.nonce++;
            this.hash = calculateHash();
        }

        logger.info("mined " + this.hash);
    }

}
