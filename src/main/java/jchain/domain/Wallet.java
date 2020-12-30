package jchain.domain;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.Map;

@Log4j2
public class Wallet {
    public PrivateKey privateKey;
    public PublicKey publicKey;

    public Wallet() {
        this.generateKeyPair();
    }

    public float getBalance() {
        float total = 0;
        for (Map.Entry<String, TransactionOutput> transaction : Blockchain.UTXOs.entrySet()) {
            TransactionOutput UTXO = transaction.getValue();
            if (UTXO.isMine(publicKey)) {
                Blockchain.UTXOs.put(UTXO.id, UTXO); //add it to our list of unspent transactions.
                total += UTXO.value;
            }
        }
        return total;
    }

    public Transaction sendFunds(PublicKey receiver, float value) {
        if (this.getBalance() < value) {
            log.error("#Not Enough funds to send transaction. Transaction Discarded.");
            return null;
        }

        ArrayList<TransactionInput> inputs = new ArrayList<>();

        float total = 0;
        for (Map.Entry<String, TransactionOutput> item : Blockchain.UTXOs.entrySet()) {
            TransactionOutput UTXO = item.getValue();

            total += UTXO.value;
            inputs.add(new TransactionInput(UTXO.id));

            if (total > value) {
                break;
            }
        }

        Transaction newTransaction = new Transaction(publicKey, receiver, value, inputs);
        newTransaction.generateSignature(privateKey);

        for (TransactionInput input : inputs) {
            Blockchain.UTXOs.remove(input.transactionOutputId);
        }

        return newTransaction;
    }

    @SneakyThrows
    public void generateKeyPair() {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256r1");

        keyGen.initialize(ecSpec, random);   //256 bytes provides an acceptable security level
        KeyPair keyPair = keyGen.generateKeyPair();
        // Set the public and private keys from the keyPair
        this.privateKey = keyPair.getPrivate();
        this.publicKey = keyPair.getPublic();
    }
}
