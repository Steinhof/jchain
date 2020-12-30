package jchain.domain;

import jchain.utils.StringUtil;
import lombok.extern.log4j.Log4j2;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public class Transaction {
    public String transactionId;
    public PublicKey sender;
    public PublicKey receiver;
    public float value;
    public byte[] signature;

    public List<TransactionInput> inputs = new ArrayList<>();
    public List<TransactionOutput> outputs = new ArrayList<>();

    private int sequence = 0;
    public static float minimumTransaction = 0.1f;

    public Transaction(PublicKey sender, PublicKey receiver, float value, List<TransactionInput> inputs) {
        this.sender = sender;
        this.receiver = receiver;
        this.value = value;
        this.inputs = inputs;
    }

    public void generateSignature(PrivateKey privateKey) {
        String data = StringUtil.getStringFromKey(this.sender) + StringUtil.getStringFromKey(this.receiver) + value;
        this.signature = StringUtil.applyECDSASig(privateKey, data);
    }

    public boolean verifySignature() {
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(this.receiver) + value;
        return StringUtil.verifyECDSASig(this.sender, data, this.signature);
    }

    public boolean process() {
        if (!this.verifySignature()) {
            log.error("#Transaction Signature failed to verify");
            return false;
        }

        for (TransactionInput input : inputs) {
            input.UTXO = Blockchain.UTXOs.get(input.transactionOutputId);
        }

        if (this.getInputsValue() < Blockchain.MINIMUM_TRANSACTION) {
            log.error("#Transaction Inputs to small: " + getInputsValue());
            return false;
        }

        float leftOver = this.getInputsValue() - value;
        transactionId = this.calculateHash();
        outputs.add(new TransactionOutput(this.receiver, value, transactionId));
        outputs.add(new TransactionOutput(this.sender, leftOver, transactionId));

        for (TransactionOutput output : outputs) {
            Blockchain.UTXOs.put(output.id, output);
        }

        for (TransactionInput input : inputs) {
            if (input.UTXO == null) {
                continue;
            }
            Blockchain.UTXOs.remove(input.UTXO.id);
        }

        return true;
    }

    public float getInputsValue() {
        float total = 0;
        for (TransactionInput input : inputs) {
            if (input.UTXO == null) {
                continue;
            }
            total += input.UTXO.value;
        }
        return total;
    }

    public float getOutputsValue() {
        float total = 0;
        for (TransactionOutput o : outputs) {
            total += o.value;
        }
        return total;
    }


    private String calculateHash() {
        this.sequence++;
        return StringUtil.applySha256(StringUtil.getStringFromKey(this.sender) + StringUtil.getStringFromKey(this.receiver) + this.value + this.sequence);
    }
}
