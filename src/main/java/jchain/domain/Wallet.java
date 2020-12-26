package jchain.domain;

import lombok.extern.log4j.Log4j2;

import java.security.PrivateKey;
import java.security.PublicKey;

@Log4j2
public class Wallet {
    public PrivateKey privateKey;
    public PublicKey publicKey;
}
