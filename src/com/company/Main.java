package com.company;


import com.google.gson.GsonBuilder;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

//main,only this thread
public class Main {
    //store blocks in arrayList
    public static ArrayList<Block> blockChain = new ArrayList<Block>();
    public static int prefix = 5;


    public static void main(String[] args) {

        Thread th = Thread.currentThread(); // this is the main thread
        FileWriter file = null; // create the file
        String BlockchainJson = null;

        //Menu
        Scanner sc = new Scanner(System.in); // create a scanner
        int choice = 0; // read user input
        while (choice != 6) {
            System.out.println("*****MENU*****" +
                    "\nPress 1.Display all products  " +
                    "\nPress 2.Add a single Product " +
                    "\nPress 3.Add many products" +
                    "\nPress 4.Search for a product" +
                    "\nPress 5.Display product statistics" +
                    "\nPress 6.Exit");
            choice = Integer.parseInt(sc.next());
            sc.nextLine();
            switch (choice) {
                case 1:
                    //JSON parser object to parse read file
                    JSONParser jsonParser = new JSONParser();
                    try (FileReader reader = new FileReader("/home/meggi/IdeaProjects/blockchainproject/blockchainproject.json")) {
                        if (!blockChain.isEmpty()) {
                            //Read JSON file
                            Object obj = jsonParser.parse(reader);
                            JSONArray productList = (JSONArray) obj;
                            System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(productList));
                        } else {
                            System.out.println("you haven't added any product");
                        }

                    } catch (IOException | ParseException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    AddSingleProduct(sc, blockChain, file);
                    break;
                case 3:
                    AddMultipleProducts(sc, file, blockChain);
                    break;
                case 4:
                    System.out.println("Enter the code or name of the product ");
                    //search with code or name etc
                    String[] element = sc.nextLine().split(",");
                    SearchForAproduct(element);
                    break;
                case 5:
                    SimpleDateFormat dt = new SimpleDateFormat("HH.mm.ss");
                    System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(blockChain));
                    System.out.println("Enter the code of the product you want");
                    String code = sc.nextLine();
                    int k;
                    System.out.println("statistics");
                    for (k = 0; k < blockChain.size(); k++) {
                        if (code.equals(blockChain.get(k).getCode())) {
                            System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(blockChain.get(k).getPrice()) + " EUR" + new GsonBuilder().setPrettyPrinting().create().toJson(dt.format(blockChain.get(k).getTimeStamp())));
                        }
                    }
                    break;
                case 6:
                    choice = 6;
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + choice);
            }
        }

    }

    public static void AddMultipleProducts(Scanner sc, FileWriter file, ArrayList<Block> blockChain) {
        System.out.println("Give the number of products you want to add");
        int number, i;
        number = sc.nextInt();
        sc.nextLine();
        for (i = 0; i <= number; i++) {
            AddSingleProduct(sc, blockChain, file);  //Same like Add Single Product
        }
    }

    public static void AddSingleProduct(Scanner sc, ArrayList<Block> blockChain, FileWriter file) {
        try {
            file = new FileWriter("/home/meggi/IdeaProjects/blockchainproject/blockchainproject.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
        long startTime = System.nanoTime();
        String name,category, description,code;
        double price;
        //read the user inputs
        System.out.println("Product Code");
        code = sc.nextLine();
        System.out.println("Product Name");
        name = sc.nextLine();
        System.out.println("Product Category ");
        category = sc.nextLine();
        System.out.println("Product Description");
        description = sc.nextLine();
        System.out.println("Product Price");
        price = Double.parseDouble(sc.nextLine());

        Integer previousID = null;
        String previousHash;
        if (blockChain.isEmpty()) {
            previousHash = "";
            // First the Genesis Block
            Block genesisBlock = new Block(name, code, category, description, price, (blockChain.size()), previousID, previousHash, new Date().getTime());
            genesisBlock.mineBlock(prefix);
            blockChain.add(genesisBlock);
            System.out.println("Node " + (blockChain.size() - 1) + " The block Created");
        } else {
            previousHash = blockChain.get(blockChain.size() - 1).getHash();
            int i;
            for (i = 0; i < blockChain.size(); i++) {
                int id = (int) blockChain.get(i).getProductID();
                if (code.equals(blockChain.get(i).getCode())) {
                    System.out.println("The Id of product it is same with other.Please enter another id");
                    previousID = previousID;
                }
            }
            Block genesisBlock = new Block(name, code, category, description, price, (blockChain.size()), previousID, previousHash, new Date().getTime());
            genesisBlock.mineBlock(prefix);
            blockChain.add(genesisBlock);
            System.out.println(" block " + (blockChain.size() - 1) + " created...");
        }
        //Transform BlockChain into Json
        String blockChainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockChain);

        // Check for validity
        System.out.println("\nBlockchain is Valid: " +

                isChainValid());

        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        System.out.println("Total time ellapsed: " + (float) duration / 1000000000 + " seconds");
        try {

            file.write(blockChainJson);
            file.close();
        } catch (
                IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void SearchForAproduct(String[] keys) {
        System.out.println("Searching");
        int i, j;
        String position = "first";
        String b = null;
        for (i = 0; i < blockChain.size(); i++) {
            for (j = 0; j < keys.length; j++) {
                String key = keys[j];
                boolean equal = false;

                try {
                    double number = Double.parseDouble(key);
                    if (number == blockChain.get(i).getPrice()) {
                        key = "";
                        equal = true;
                    }

                } catch (NumberFormatException e) {
                    key = keys[j];
                    equal = false;
                }
                if (key.equals(blockChain.get(i).getCategory()) || key.equals(blockChain.get(i).getName()) || key.equals(blockChain.get(i).getCode()) || equal == true) {

                    if (blockChain.get(i).getPreviousID() != null) {
                        System.out.println("The product exist also with ID: " + blockChain.get(i).getPreviousID());
                        position = "last";
                    }

                    b = new GsonBuilder().setPrettyPrinting().create().toJson(blockChain.get(i));
                    j = keys.length;
                }
            }

        }
        System.out.println("Results:");
        if (b == null) {
            System.out.println("There is not any product like this");
        } else {

            System.out.println(b);

        }
    }

    // check for validity
    public static boolean isChainValid () {
        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String(new char[prefix]).replace('\0', '0');
        //loop through blockChain to check the hashes
        int i;
        for (i = 1; i < blockChain.size(); i++) {
            currentBlock = blockChain.get(i);
            previousBlock = blockChain.get(i - 1);

            if (!currentBlock.getHash().equals(currentBlock.calculateBlockHash())) {
                System.out.println("Current Hashes not equal");
                return false;
            }
            //compare previous hash and registered previous hash
            if (!previousBlock.getHash().equals(currentBlock.getPreviousHash())) {
                System.out.println("Previous Hashes not equal");
                return false;
            }
            //check if hash is solved
            if (!currentBlock.getHash().substring(0, prefix).equals(hashTarget)) {
                System.out.println("This block hasn't been mined");
                return false;
            }
        }

        return false;
    }
}






