package com.max.bakery.utils;

import com.max.bakery.controller.Manager;
import com.max.bakery.storage.order.JamStorage;
import com.max.bakery.storage.order.PoppyStorage;
import com.max.bakery.system.delivery.DeliverySystem;
import com.max.bakery.system.equipment.ElectricOven;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class monitoring state of storage and buying necessary ingredients
 *
 * @author Max
 */
public class ManagerUtil implements Runnable {
    public final static String TYPE_OF_OVEN = "Electric";

    private static final Logger LOGGER = LogManager.getLogger(ManagerUtil.class.getName());

    private static JamStorage jamStorage = JamStorage.getInstance();
    private static PoppyStorage poppyStorage = PoppyStorage.getInstance();

    @Getter
    @Setter
    private static volatile boolean needToBuyJam = false;

    @Getter
    @Setter
    private static volatile boolean needToBuyPoppy = false;

    @Getter
    @Setter
    private static volatile boolean endOfThread = false;

    private static ManagerUtil instance;

    private ManagerUtil() {
    }

    public static ManagerUtil getInstance() {
        if (instance == null) {
            instance = new ManagerUtil();
        }
        return instance;
    }

    /**
     * Method sets the flag for necessity of buying ingredients. Method defines which ingredient is needed.
     *
     * @param needToBuyIngredients true, if needed, false if not
     * @param typeOfIngredient     type of ingredient
     */
    public static void setNeedToBuyIngredients(boolean needToBuyIngredients, String typeOfIngredient) {
        switch (typeOfIngredient) {
            case "Jam": {
                setNeedToBuyJam(needToBuyIngredients);
                break;
            }
            case "Poppy": {
                setNeedToBuyPoppy(needToBuyIngredients);
                break;
            }
        }
    }

    /**
     * Method calculates the necessary number of ovens.
     *
     * @param typeOfOven         type of oven
     * @param bulkOfBakedProduct bulk of cooked product
     * @return number of ovens
     */
    public static int deduceRequiredNumberOfOvens(String typeOfOven, int bulkOfBakedProduct) {
        int numberOfOvens = 0;
        switch (typeOfOven) {
            case "Electric": {
                if (bulkOfBakedProduct > ElectricOven.getOVENCAPACITY()) {
                    numberOfOvens = (int) Math.ceil((double) bulkOfBakedProduct / (double) ElectricOven.getOVENCAPACITY());
                } else {
                    numberOfOvens = 1;
                }
                break;
            }
            default: {
                System.out.println("Wrong word: " + typeOfOven);
                break;
            }
        }
        return numberOfOvens;
    }

    /**
     * Method calculates how much can one oven bake.
     *
     * @param typeOfOven         type of oven
     * @param bulkOfBakedProduct bulk of cooked product
     * @param numberOfOvens      number of ovens
     * @return raw materials for one oven
     */
    public static int howMuchCanOneOvenBake(String typeOfOven, int bulkOfBakedProduct, int numberOfOvens) {
        int rawMaterialsForOneOven = 0;
        switch (typeOfOven) {
            case "Electric": {
                if (numberOfOvens > 1) {
                    if (bulkOfBakedProduct % numberOfOvens != 0) {
                        rawMaterialsForOneOven = (int) Math.ceil((double) bulkOfBakedProduct / (double) numberOfOvens);
                    } else if (bulkOfBakedProduct % numberOfOvens == 0) {
                        rawMaterialsForOneOven = bulkOfBakedProduct / numberOfOvens;
                    }
                } else if (numberOfOvens == 1) {
                    rawMaterialsForOneOven = bulkOfBakedProduct;
                } else if (numberOfOvens <= 0) {
                    System.out.println("You try to use " + numberOfOvens + " oven");
                }
                break;
            }
        }
        return rawMaterialsForOneOven;
    }

    private static boolean fillPoppyStorage() {
        return PoppyStorage.getSTORAGE_CAPACITY() <= PoppyStorage.getInstance().getList().size();
    }

    private static boolean fillJamStorage() {
        return JamStorage.getSTORAGE_CAPACITY() <= JamStorage.getInstance().getList().size();
    }


    private static synchronized void jamStorageMonitoring() {
        if (fillJamStorage()) {
            setNeedToBuyJam(false);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.err.println("THREAD CANNOT SLEEP! " + ManagerUtil.class.getName());
            }

            StringBuilder infoString = new StringBuilder();
            infoString.append("JamStorage is filled");
            LOGGER.info(infoString);
        } else {
            try {
                jamStorage.buyIngredients();
                Thread.sleep(50);
            } catch (InterruptedException e) {
                System.err.println("THREAD CANNOT SLEEP! " + ManagerUtil.class.getName());
            }
        }
    }


    private static synchronized void poppyStorageMonitoring() {
        if (fillPoppyStorage()) {
            setNeedToBuyPoppy(false);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.err.println("THREAD CANNOT SLEEP! " + ManagerUtil.class.getName());
            }
            StringBuilder infoString = new StringBuilder();
            infoString.append("PoppyStorage is filled");
            LOGGER.info(infoString);
        } else {
            try {
                poppyStorage.buyIngredients();
                Thread.sleep(50);
            } catch (InterruptedException e) {
                System.err.println("THREAD CANNOT SLEEP! " + ManagerUtil.class.getName());
            }
        }
    }

    @Override
    public void run() {
        while (true) {
            if (isEndOfThread()) {
                DeliverySystem.setEndThread(true);
                return;
            } else {
                try {
                    Manager.getSEMAPHORE().acquire();
                    if (isNeedToBuyJam()) {
                        while (!fillJamStorage()) {
                            jamStorageMonitoring();
                        }
                    } else if (isNeedToBuyPoppy()) {
                        while (!fillPoppyStorage()) {
                            poppyStorageMonitoring();
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    Manager.getSEMAPHORE().release();
                }
            }
        }
    }
}


