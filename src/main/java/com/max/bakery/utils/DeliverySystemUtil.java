package com.max.bakery.utils;

import com.max.bakery.storage.finish.ProductStorage;

/**
 * Class helps delivery system. But now class don't use.
 *
 * @author Max
 * @version 1.0
 */
public class DeliverySystemUtil {
    private static ProductStorage productStorage = ProductStorage.getInstance();

    /**
     * @param typeOfProduct type of product
     * @return count of product, which kept in suitable storage
     */
    public static int haveAProduct(String typeOfProduct) {
        int product = 0;
        switch (typeOfProduct) {
            case "JamBiscuit":
                product = productStorage.getCountJamBiscuit();
                break;
            case "JamPie":
                product = productStorage.getCountJamPie();
                break;
            case "PoppyBiscuit":
                product = productStorage.getCountPoppyBiscuit();
                break;
            case "PoppyPie":
                product = productStorage.getCountPoppyPie();
                break;
            default:
                System.out.println("We don't have what you want");
                break;
        }
        return product;
    }
}
