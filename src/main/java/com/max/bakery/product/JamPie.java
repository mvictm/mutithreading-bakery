package com.max.bakery.product;

import com.max.bakery.product.abstraction.Ingredient;
import com.max.bakery.product.abstraction.Pie;
import lombok.Getter;

/**
 * Real product
 *
 * @author Max
 * @version 1.0
 */
public class JamPie extends Pie {
    private Ingredient ingredient;

    @Getter
    private String description = "JamPie";

    public JamPie(Ingredient ingredient) {
        if (ingredient instanceof Jam) {
            this.ingredient = ingredient;
        } else {
            System.out.println("You try to create JamPie without JAM!!!");
        }
    }
}
