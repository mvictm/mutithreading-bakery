package com.max.bakery.system.delivery;

import com.max.bakery.controller.Manager;
import com.max.bakery.storage.finish.ProductStorage;
import com.max.bakery.system.equipment.Oven;
import lombok.Getter;
import lombok.Setter;
import lombok.Synchronized;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class imitates delivery system.
 *
 * @author Max
 * @version 1.0
 */
public class DeliverySystem implements Runnable {
    private static final Logger LOGGER = LogManager.getLogger(DeliverySystem.class.getName());

    private static ProductStorage productStorage = ProductStorage.getInstance();

    @Getter
    @Setter
    private volatile static boolean endThread = false;

    private static DeliverySystem instance;

    public static DeliverySystem getInstance() {
        if (instance == null) {
            instance = new DeliverySystem();
        }
        return instance;
    }

    /**
     * Delivers cooked product to {@link ProductStorage}
     *
     * @param oven certain oven
     */
    @Synchronized
    private static void deliveryToStorage(Oven oven) {
        productStorage.getBakedProductList().addAll(oven.getOvenBakedProductList());
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            System.err.println("THREAD CANNOT SLEEP! " + DeliverySystem.class.getName());
        }
        oven.getOvenBakedProductList().clear();

        StringBuilder infoString = new StringBuilder();
        infoString.append("delivery final product to storage in ").append(oven.getNumber()).append(" oven");
        LOGGER.info(infoString);
    }

    /**
     * Method looks for free and full ovens and delivers cooked product to {@link ProductStorage}
     */
    @Override
    public void run() {
        while (!endThread) {
            try {
                Manager.getSEMAPHORE().acquire();
                for (int i = 0; i < Manager.getInstance().getOvenList().size(); i++) {
                    if (!Manager.getInstance().getOvenList().get(i).isBusyOven()
                            && !Manager.getInstance().getOvenList().get(i).getOvenBakedProductList().isEmpty()) {
                        deliveryToStorage(Manager.getInstance().getOvenList().get(i));
                    }
                }
            } catch (InterruptedException e) {
                e.getMessage();
            } finally {
                Manager.getSEMAPHORE().release();
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                System.err.println("THREAD CANNOT SLEEP!! " + DeliverySystem.class.getName());
            }
        }
    }
}
