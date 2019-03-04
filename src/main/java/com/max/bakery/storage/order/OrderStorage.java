package com.max.bakery.storage.order;

import com.max.bakery.product.abstraction.Ingredient;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Abstract class for shortages
 *
 * @author Max
 * @version 1.0
 */
public abstract class OrderStorage {
    private static final Logger LOGGER = LogManager.getLogger(OrderStorage.class);

    @Getter
    private static final Integer STORAGE_CAPACITY = 30;

    /**
     * Method buy necessary ingredient and put it in list
     */
    public synchronized void buyIngredients() {
        if (getList().size() < STORAGE_CAPACITY) {
            getList().add(getIngredientInstance());
        } else {
            StringBuilder infoString = new StringBuilder();
            infoString.append(getIngredientInstance().getDescription()).append("storage is fill");
            LOGGER.info(infoString);
        }
    }

    /**
     * Method imitates transfer raw materials to oven. If container with raw material isn't contain necessary amount,
     * method send a command for buying process.
     */
    public synchronized void sendToOven() {
        for (int i = 0; i < 2; i++) {
            if (!getList().isEmpty()) {
                getList().remove(0);
            } else {
                buyIngredients();
                getList().remove(0);
            }
        }
        StringBuilder infoString = new StringBuilder();
        infoString.append("Send to oven ").append(2).append(" ").append(getIngredientInstance().getDescription());
        LOGGER.info(infoString);
    }

    public abstract List<Ingredient> getList();

    protected abstract Ingredient getIngredientInstance();
}
