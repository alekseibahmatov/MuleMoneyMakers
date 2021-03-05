package utils;

import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.model.Player;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.utility.ConditionalSleep;

public class Trading extends MethodProvider {
    private RS2Widget inTradeWidget;
    private RS2Widget amountWidget;
    private RS2Widget declineButton;
    private RS2Widget acceptButton;
    private RS2Widget acceptButtonSelected;


    private boolean inputXAmount(int amount) {
        new ConditionalSleep(10000, 100) {
            @Override
            public boolean condition() throws InterruptedException {
                return ((amountWidget = getWidgets().get(162, 44)) != null) && amountWidget.isVisible();
            }
        }.sleep();
        if (amountWidget != null) {
            return keyboard.typeString("" + amount);
        } else {
            return false;
        }
    }

    public boolean acceptTrade(boolean acceptAllScreens) {
        new ConditionalSleep(5000, 100) {
            @Override
            public boolean condition() throws InterruptedException {
                return ((acceptButton = getWidgets().getWidgetContainingText("Accept")) != null) && acceptButton.isVisible();
            }
        }.sleep();
        if (acceptButton != null) {
            if (acceptButton.interact("Accept")) {
                new ConditionalSleep(10000, 100) {
                    @Override
                    public boolean condition() throws InterruptedException {
                        return ((acceptButtonSelected = getWidgets().getWidgetContainingText("Waiting for other player...")) != null) && acceptButtonSelected.isVisible();
                    }
                }.sleep();
                new ConditionalSleep(100000, 1500) {
                    @Override
                    public boolean condition() throws InterruptedException {
                        return !acceptButton.isVisible() || acceptButtonSelected.getMessage().equals("");
                    }
                }.sleep();
                if (acceptButtonSelected.getMessage().equals("")) {
                    acceptTrade(acceptAllScreens);
                }
                if (acceptAllScreens) {
                    acceptTrade(true);
                }
                return true;
            }
        }
        return false;
    }

    public boolean declineTrade() {
        if ((declineButton = getWidgets().getWidgetContainingText("Decline")) != null) {
            if (declineButton.interact("Decline")) {
                new ConditionalSleep(100000, 100) {
                    @Override
                    public boolean condition() throws InterruptedException {
                        return !declineButton.isVisible();
                    }
                }.sleep();
                return true;
            }
        }
        return false;
    }

    public boolean offerAll(String itemName) {
        if (getWidgets().getWidgetContainingText("Trading with: ") != null) {
            return getInventory().interact("Offer-All", itemName);
        }
        return false;
    }

    public boolean offerAll(Item item) {
        if (getWidgets().getWidgetContainingText("Trading with: ") != null) {
            return item.interact("Offer-All");
        }
        return false;
    }

    public boolean offerItem(String itemName, int amount) {
        String amountSelection;
        if (amount == 1) {
            amountSelection = "Offer";
        } else if (amount == 5) {
            amountSelection = "Offer-5";
        } else if (amount == 10) {
            amountSelection = "Offer-10";
        } else {
            amountSelection = "Offer-X";
        }
        if (getWidgets().getWidgetContainingText("Trading with: ") != null) {
            if (getInventory().interact(amountSelection, itemName)) {
                if (amountSelection.equals("Offer-X")) {
                    return inputXAmount(amount);
                } else {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean offerItem(Item item, int amount) {
        if (item == null) {
            return false;
        }
        String amountSelection;
        if (amount == 1) {
            amountSelection = "Offer";
        } else if (amount == 5) {
            amountSelection = "Offer-5";
        } else if (amount == 10) {
            amountSelection = "Offer-10";
        } else {
            amountSelection = "Offer-X";
        }
        if (getWidgets().getWidgetContainingText("Trading with: ") != null) {
            if (item.interact(amountSelection)) {
                if (amountSelection.equals("Offer-X")) {
                    return inputXAmount(amount);
                } else {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean trade(Player player) {
        if (player == null) {
            return false;
        }
        if (((inTradeWidget = getWidgets().getWidgetContainingText("Accept")) != null) && inTradeWidget.isVisible()) {
            return true;
        }
        if (player.interact("Trade with")) {
            new ConditionalSleep(10000, 100) {
                @Override
                public boolean condition() throws InterruptedException {
                    return ((inTradeWidget = getWidgets().getWidgetContainingText("Accept")) != null);
                }
            }.sleep();
            return inTradeWidget != null;
        }
        return false;
    }

    public boolean trade(String playerName) {
        Player player = players.closest(player1 -> player1.getName().equals(playerName));
        if (((inTradeWidget = getWidgets().getWidgetContainingText("Accept")) != null) && inTradeWidget.isVisible()) {
            return true;
        }
        if (player != null) {
            if (player.interact("Trade with")) {
                new ConditionalSleep(10000, 100) {
                    @Override
                    public boolean condition() throws InterruptedException {
                        return ((inTradeWidget = getWidgets().getWidgetContainingText("Accept")) != null);
                    }
                }.sleep();
                return inTradeWidget != null;
            }
        }
        return false;
    }
}
