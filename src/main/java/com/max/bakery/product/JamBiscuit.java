package com.max.bakery.product;

import com.max.bakery.product.abstraction.Ingredient;
import com.max.bakery.product.abstraction.Biscuit;
import lombok.Getter;

/**
 * Real product
 *
 * @author Max
 * @version 1.0
 */
public class JamBiscuit extends Biscuit {
    private Ingredient ingredient;

    @Getter
    private String description = "JamBiscuit";

    public JamBiscuit(Ingredient ingredient) {
        if (ingredient instanceof Jam) {
            this.ingredient = ingredient;
        } else {
            System.out.println("You try to create JamBiscuit without JAM!!!");
        }
    }
}
