package com.max.bakery.system.equipment;

import com.max.bakery.product.abstraction.BakedProduct;

import java.util.List;

/**
 * Interface describes oven's functions
 *
 * @author Max
 * @version 1.0
 */
public interface Oven {
    void process();

    List<BakedProduct> getOvenBakedProductList();

    boolean isBusyOven();

    int getNumber();

    void joinThread();
}
