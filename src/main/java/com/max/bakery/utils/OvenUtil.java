package com.max.bakery.utils;

import com.max.bakery.factory.AbstractBakery;
import com.max.bakery.factory.BiscuitFactory;
import com.max.bakery.factory.PieFactory;
import com.max.bakery.product.abstraction.BakedProduct;
import com.max.bakery.storage.order.JamStorage;
import com.max.bakery.storage.order.OrderStorage;
import com.max.bakery.storage.order.PoppyStorage;
import com.max.bakery.system.equipment.ElectricOven;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class helps to determine storage for usage, create cooked product
 *
 * @author Max
 * @version 1.0
 */
public class OvenUtil {
    private static final Logger LOGGER = LogManager.getLogger(OvenUtil.class.getName());

    @Getter
    @Setter
    private static volatile boolean sem = false;

    /**
     * Method determines storage for usage.
     *
     * @param typeOfIngredient type of ingredient
     * @return type of storage
     */
    public static OrderStorage determinationOfTheStorage(String typeOfIngredient) {
        OrderStorage orderStorage = null;
        switch (typeOfIngredient) {
            case "Jam":
                orderStorage = JamStorage.getInstance();
                break;
            case "Poppy":
                orderStorage = PoppyStorage.getInstance();
                break;
            default:
                System.out.println("We don't have this ingredients!");
                break;
        }

        StringBuilder infoString = new StringBuilder();
        infoString.append("We use a ").append(typeOfIngredient).append("storage");
        LOGGER.info(infoString);

        return orderStorage;
    }

    /**
     * Method creates certain type of product
     *
     * @param typeOfProduct    type of product
     * @param typeOfIngredient type of ingredient
     * @return certain type of product
     */
    public static BakedProduct certainProduct(String typeOfProduct, String typeOfIngredient) {
        BakedProduct BakedProduct = null;
        OrderStorage orderStorage = determinationOfTheStorage(typeOfIngredient);
        AbstractBakery abstractBakery = determinationOfTheFactory(typeOfProduct);
        while (BakedProduct == null) {
            if (orderStorage instanceof JamStorage) {
                if (orderStorage.getList().size() >= 2) {
                    BakedProduct = abstractBakery.createWithJam(orderStorage.getList().get(0));
                    JamStorage.getInstance().sendToOven();
                } else {
                    ManagerUtil.setNeedToBuyIngredients(true, typeOfIngredient);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        System.err.println("THREAD CANNOT SLEEP!! " + OvenUtil.class.getName());
                    }
                }
            } else if (orderStorage instanceof PoppyStorage) {
                if (orderStorage.getList().size() > 2) {
                    BakedProduct = abstractBakery.createWithPoppy(orderStorage.getList().get(0));
                    PoppyStorage.getInstance().sendToOven();
                } else {
                    ManagerUtil.setNeedToBuyIngredients(true, typeOfIngredient);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        System.err.println("THREAD CANNOT SLEEP!! " + OvenUtil.class.getName());
                    }
                }
            } else {
                System.out.println("We don't know what we need do!");
            }
        }
        return BakedProduct;
    }

    /**
     * Method calculates the quantity of products which can be baked in one oven.
     *
     * @param typeOfOven         type of oven
     * @param bulkOfBakedProduct bulk of cooked product
     * @return quantity of products which can be baked in one oven
     */
    public static int howOvenCanCreateBakedProduct(String typeOfOven, int bulkOfBakedProduct) {
        int quantityOfProduct = 0;
        switch (typeOfOven) {
            case "Electric": {
                if (bulkOfBakedProduct <= ElectricOven.getOVENCAPACITY()) {
                    quantityOfProduct = bulkOfBakedProduct;
                } else if (bulkOfBakedProduct > ElectricOven.getOVENCAPACITY()) {
                    quantityOfProduct = ElectricOven.getOVENCAPACITY();
                }

                StringBuilder infoString = new StringBuilder();
                infoString.append("Oven can make ").append(quantityOfProduct).append(" product");
                LOGGER.info(infoString);

                break;
            }
            default: {
                System.out.println("We don't know this oven");
            }
        }
        return quantityOfProduct;
    }

    private static AbstractBakery determinationOfTheFactory(String typeOfProduct) {
        AbstractBakery abstractBakery = null;
        switch (typeOfProduct) {
            case "Biscuit":
                abstractBakery = new BiscuitFactory();
                break;
            case "Pie":
                abstractBakery = new PieFactory();
                break;
            default:
                System.out.println("We don't know what you want!");
                break;
        }
        return abstractBakery;
    }
}
