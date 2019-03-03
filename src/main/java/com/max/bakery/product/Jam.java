package com.max.bakery.product;

import com.max.bakery.product.abstraction.Ingredient;

/**
 * Real ingredient
 *
 * @author Max
 * @version 1.0
 */
public class Jam implements Ingredient {
    @Override
    public String getDescription() {
        return "Jam";
    }
}
