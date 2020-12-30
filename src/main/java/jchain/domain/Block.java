package jchain.domain;

import jchain.utils.StringUtil;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.Date;

@Log4j2
public class Block {
    public String hash;
    public final String previousHash;
    public long timeStamp;
    public String merkleRoot;
    private int nonce;

    public ArrayList<Transaction> transactions = new ArrayList<>();

    public Block(String previousHash) {
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash();
    }

    public String calculateHash() {
        return StringUtil.applySha256(
                previousHash + timeStamp + nonce + merkleRoot);
    }

    public void mine(int difficulty) {
        String target = StringUtil.getDifficulty(difficulty);

        while (!hash.substring(0, difficulty).equals(target)) {
            this.nonce++;
            this.hash = calculateHash();
        }

        log.info("mined " + this.hash);
    }

    public boolean addTransaction(Transaction transaction) {
        if (transaction == null) {
            return false;
        }

        if (!previousHash.equals("0") && !transaction.process()) {
            log.error("Transaction failed to process. Discarded.");
            return false;
        }

        transactions.add(transaction);

        log.info("Transaction Successfully added to Block");

        return true;
    }
}
