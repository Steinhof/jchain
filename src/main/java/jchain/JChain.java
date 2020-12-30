package jchain;

import jchain.domain.Blockchain;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class JChain {

    public static void main(String[] args) {
        Blockchain blockchain = new Blockchain();
        blockchain.generate();
    }
}
