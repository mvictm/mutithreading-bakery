package com.max.bakery;

import com.max.bakery.controller.Manager;
import com.max.bakery.storage.finish.ProductStorage;
import com.max.bakery.utils.ManagerUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Scanner;

/**
 * Start point
 *
 * @author Max
 */
public class BakerySimulator {
    private static final Logger LOGGER = LogManager.getLogger(BakerySimulator.class);

    private static void startProgram() {
        int quantityOfOperation = 0;
        String ingredient;
        String typeOfProduct;
        String next;

        int bulkOfBakedProducts = 0;
        Manager manager = Manager.getInstance();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to bakery system!");
        System.out.println("Please entered next information");
        System.out.println(" ");
        while (true) {
            {
                String tmp;
                System.out.println("Choose type of ingredient: Jam or Poppy");
                tmp = scanner.next();
                if (tmp.equals("Jam") || tmp.equals("Poppy")) {
                    ingredient = tmp;
                } else {
                    System.out.println("You entered not correct value: " + tmp);
                    continue;
                }
            }
            {
                String tmp;
                System.out.println("Choose type of product: Pie or Biscuit");
                tmp = scanner.next();
                if (tmp.equals("Pie") || tmp.equals("Biscuit")) {
                    typeOfProduct = tmp;
                } else {
                    System.out.println("You entered not correct value: " + tmp);
                    continue;
                }
            }

            {
                int a = 0;
                System.out.println("Entered quantity of baked products: ");
                a = scanner.nextInt();
                if (a > 0) {
                    bulkOfBakedProducts = a;
                } else {
                    System.out.println("You entered unacceptable value: " + a);
                    continue;
                }
            }


            manager.processAdministrator(ManagerUtil.TYPE_OF_OVEN, ingredient, typeOfProduct, bulkOfBakedProducts);
            quantityOfOperation++;

            ProductStorage.getInstance().storageInfo();

            System.out.println("Next? (y/n)");
            next = scanner.next();

            if (next.equals("n")) {
                break;
            }
        }
        StringBuilder infoString = new StringBuilder();
        infoString.append("Quantity of operation: ").append(quantityOfOperation);
        LOGGER.info(infoString);

        ProductStorage.getInstance().storageInfo();

        StringBuilder infoString1 = new StringBuilder();
        infoString1.append("system finish.");
        LOGGER.info(infoString1);
    }

    public static void main(String[] args) {
        startProgram();
    }
}
