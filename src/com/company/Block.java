package com.company;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Block {

    private static Logger logger = Logger.getLogger(Block.class.getName());

    private String hash;




    public void setPreviousID(Integer previousID) {
        this.previousID = previousID;
    }

    private String previousHash;
    private String name,code,category,description;
    private long timeStamp;

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    private int productID=0;
    private Integer previousID;
    private int nonce;
    private double price;


    public String getName() {
        return this.name;
    }

    public String getCode() {
        return this.code;
    }

    public String getCategory() {
        return this.category;
    }

    public String getDescription() {
        return this.description;
    }

    public double getPrice() {
        return this.price;
    }

    public long getTimeStamp() {
        return this.timeStamp;
    }

    public Block(String name, String code, String category, String description, double price, int productID, Integer previousID, String previousHash, long timeStamp) {

        this.name= name;
        this.previousHash = previousHash;
        this.timeStamp = timeStamp;
        this.code =code;
        this.category = category;
        this. description = description;
        this.productID = productID;
        this.previousID = previousID;
        this.price= price;
        this.hash = calculateBlockHash();
    }
    public String mineBlock(int prefix) {
        String prefixString = new String(new char[prefix]).replace('\0', '0');
        while (!hash.substring(0, prefix)
                .equals(prefixString)) {
            nonce++;
            hash = calculateBlockHash();
        }
        return hash;
    }

    public String calculateBlockHash() {
        String dataToHash = previousHash + Long.toString(timeStamp) + Integer.toString(nonce) + name;
        MessageDigest digest = null;
        byte[] bytes = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            bytes = digest.digest(dataToHash.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
        }
        StringBuffer buffer = new StringBuffer();
        for (byte b : bytes) {
            buffer.append(String.format("%02x", b));
        }
        return buffer.toString();
    }

    public String getHash() {
        return this.hash;
    }
    public Integer getPreviousID() {
        return this.previousID;
    }

    public String getPreviousHash() {
        return this.previousHash;
    }



}


