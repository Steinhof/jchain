package jchain.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import jchain.domain.Transaction;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Log4j2
public class StringUtil {

    private StringUtil() {
    }

    @SneakyThrows
    public static String applySha256(String input) {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

        StringBuilder hexString = new StringBuilder();

        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);

            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }

        return hexString.toString();
    }

    // "00000"
    public static String getDifficulty(int difficulty) {
        return new String(new char[difficulty]).replace('\0', '0');
    }

    @SneakyThrows
    public static String getJson(Object object) {
        return new ObjectMapper().writeValueAsString(object);
    }

    @SneakyThrows
    public static byte[] applyECDSASig(PrivateKey privateKey, String input) {
        Signature dsa = Signature.getInstance("SHA256withECDSA");
        byte[] strByte = input.getBytes();

        dsa.initSign(privateKey);
        dsa.update(strByte);

        return dsa.sign();
    }

    @SneakyThrows
    public static boolean verifyECDSASig(PublicKey publicKey, String data, byte[] signature) {
        Signature dsa = Signature.getInstance("SHA256withECDSA");
        dsa.initVerify(publicKey);
        dsa.update(data.getBytes());
        return dsa.verify(signature);
    }

    public static String getStringFromKey(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public static String getMerkleRoot(List<Transaction> transactions) {
        ArrayList<String> previousTreeLayer = new ArrayList<>();
        int count = transactions.size();

        for (Transaction transaction : transactions) {
            previousTreeLayer.add(transaction.transactionId);
        }

        ArrayList<String> treeLayer = previousTreeLayer;

        while (count > 1) {
            treeLayer = new ArrayList<>();

            for (int i = 1; i < previousTreeLayer.size(); i++) {
                treeLayer.add(applySha256(previousTreeLayer.get(i - 1) + previousTreeLayer.get(i)));
            }

            count = treeLayer.size();
            previousTreeLayer = treeLayer;
        }

        return (treeLayer.size() == 1) ? treeLayer.get(0) : "";
    }
}
