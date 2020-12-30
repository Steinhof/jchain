package jchain.domain;

import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Log4j2
public class Blockchain {
    private static final ArrayList<Block> chain = new ArrayList<>();
    private static final int DIFFICULTY = 5;
    protected static final Map<String, TransactionOutput> UTXOs = new HashMap<>();
    public static final float MINIMUM_TRANSACTION = 0.1f;
    public static Transaction testTransaction;

    /**
     * Test method
     */
    public void generate() {
        Wallet walletA = new Wallet();
        Wallet walletB = new Wallet();
        Wallet coinbase = new Wallet();

        testTransaction = new Transaction(coinbase.publicKey, walletA.publicKey, 100f, null);
        testTransaction.generateSignature(coinbase.privateKey);     //manually sign the genesis transaction
        testTransaction.transactionId = "0"; //manually set the transaction id
        testTransaction.outputs.add(new TransactionOutput(testTransaction.receiver, testTransaction.value, testTransaction.transactionId)); //manually add the Transactions Output
        UTXOs.put(testTransaction.outputs.get(0).id, testTransaction.outputs.get(0)); //its important to store our first transaction in the UTXOs list.

        Block genesis = new Block("0");
        genesis.addTransaction(testTransaction);
        Blockchain.addBlock(genesis);

        Block block1 = new Block(genesis.hash);
        log.info(walletA.getBalance());

        block1.addTransaction(walletA.sendFunds(walletB.publicKey, 40f));
        Blockchain.addBlock(block1);
        log.info(walletA.getBalance());
    }

    public boolean checkIntegrity() {
        for (int i = 1; i < Blockchain.chain.size(); i++) {
            Block previousBlock = Blockchain.chain.get(i - 1);
            Block currentBlock = Blockchain.chain.get(i);

            if (!currentBlock.hash.equals(currentBlock.calculateHash())) {
                log.error("Current hashes are not equal");
                return false;
            }

            if (!previousBlock.hash.equals(currentBlock.previousHash)) {
                log.error("Previous hashes are not equal");
                return false;
            }
        }

        return true;
    }

    public static void addBlock(Block newBlock) {
        newBlock.mine(Blockchain.DIFFICULTY);
        Blockchain.chain.add(newBlock);
    }
}
