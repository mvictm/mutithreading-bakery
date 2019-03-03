package com.max.bakery.storage.finish;

import com.max.bakery.product.abstraction.BakedProduct;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * Storage of cooked products. Singleton
 *
 * @author Max
 * @version 1.0
 */
public class ProductStorage {
    private static final Logger LOGGER = LogManager.getLogger(ProductStorage.class.getName());

    @Getter
    private List<BakedProduct> bakedProductList = new LinkedList<>();

    private static ProductStorage instance;

    public static ProductStorage getInstance() {
        if (instance == null) {
            instance = new ProductStorage();
        }
        return instance;
    }

    public int getCountJamPie() {
        return (int) bakedProductList.stream().filter(s -> s.getDescription().equals("JamPie")).count();
    }

    public int getCountJamBiscuit() {
        if (!bakedProductList.isEmpty()) {
            return (int) bakedProductList.stream().filter(s -> s.getDescription().equals("JamBiscuit")).count();
        } else {
            return 0;
        }
    }

    public int getCountPoppyPie() {
        if (!bakedProductList.isEmpty()) {
            return (int) bakedProductList.stream().filter(s -> s.getDescription().equals("PoppyPie")).count();
        } else {
            return 0;
        }
    }

    public int getCountPoppyBiscuit() {
        if (!bakedProductList.isEmpty()) {
            return (int) bakedProductList.stream().filter(s -> s.getDescription().equals("PoppyBiscuit")).count();
        } else {
            return 0;
        }
    }

    public void storageInfo() {
        System.out.println("We have " + getCountJamPie() + " JamPie");
        System.out.println("We have " + getCountJamBiscuit() + " JamBiscuit");
        System.out.println("We have " + getCountPoppyPie() + " PoppyPie");
        System.out.println("We have " + getCountPoppyBiscuit() + " PoppyBiscuit");

        StringBuilder infoString = new StringBuilder();
        infoString.append("We have ").append(getCountJamPie()).append(" JamPie")
                .append("We have ").append(getCountJamBiscuit()).append(" JamBiscuit")
                .append("We have ").append(getCountPoppyPie()).append(" PoppyPie")
                .append("We have ").append(getCountPoppyBiscuit()).append(" PoppyBiscuit");
        LOGGER.info(infoString);
    }
}
