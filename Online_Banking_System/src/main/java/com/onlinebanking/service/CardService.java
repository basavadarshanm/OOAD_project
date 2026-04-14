package com.onlinebanking.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.onlinebanking.model.Card;

/**
 * Service for managing cards with business logic for blocking, unblocking, and spending limits
 */
public class CardService {
    private final ConcurrentHashMap<Long, Card> cardStore = new ConcurrentHashMap<>();
    private long cardIdCounter = 1;

    public CardService() {}

    /**
     * Issue a new card to an account
     */
    public Card issueCard(long accountId, String cardType, BigDecimal dailyLimit, BigDecimal spendingLimit) {
        if (cardType == null || !isValidCardType(cardType)) {
            throw new IllegalArgumentException("Invalid card type: " + cardType);
        }

        Card card = new Card(
            accountId,
            generateCardNumber(),
            cardType,
            generateExpiryDate(),
            generateCVV(),
            dailyLimit,
            spendingLimit
        );
        card.setCardId(cardIdCounter++);
        cardStore.put(card.getCardId(), card);
        return card;
    }

    /**
     * Get all active cards for an account
     */
    public List<Card> getCardsByAccount(long accountId) {
        List<Card> accountCards = new ArrayList<>();
        for (Card card : cardStore.values()) {
            if (card.getAccountId() == accountId) {
                accountCards.add(card);
            }
        }
        return accountCards;
    }

    /**
     * Get a specific card
     */
    public Card getCard(long cardId) {
        return cardStore.getOrDefault(cardId, null);
    }

    /**
     * Block a card
     */
    public Card blockCard(long cardId, String reason) {
        Card card = cardStore.get(cardId);
        if (card == null) {
            throw new IllegalArgumentException("Card not found: " + cardId);
        }
        card.blockCard();
        cardStore.put(cardId, card);
        return card;
    }

    /**
     * Unblock a card
     */
    public Card unblockCard(long cardId) {
        Card card = cardStore.get(cardId);
        if (card == null) {
            throw new IllegalArgumentException("Card not found: " + cardId);
        }
        card.unblockCard();
        cardStore.put(cardId, card);
        return card;
    }

    /**
     * Update card spending limits
     */
    public Card updateSpendingLimits(long cardId, BigDecimal dailyLimit, BigDecimal spendingLimit) {
        Card card = cardStore.get(cardId);
        if (card == null) {
            throw new IllegalArgumentException("Card not found: " + cardId);
        }

        if (dailyLimit != null && dailyLimit.compareTo(BigDecimal.ZERO) > 0) {
            card.setDailyLimit(dailyLimit);
        }
        if (spendingLimit != null && spendingLimit.compareTo(BigDecimal.ZERO) > 0) {
            card.setSpendingLimit(spendingLimit);
        }

        cardStore.put(cardId, card);
        return card;
    }

    /**
     * Deduct amount from card spending (simulate transaction)
     */
    public synchronized boolean deductSpending(long cardId, BigDecimal amount) {
        Card card = cardStore.get(cardId);
        if (card == null || !card.isActive()) {
            return false;
        }

        if (!card.canSwipe(amount)) {
            return false;
        }

        card.setCurrentSpending(card.getCurrentSpending().add(amount));
        cardStore.put(cardId, card);
        return true;
    }

    /**
     * Reset daily spending at end of day
     */
    public void resetDailySpending(long cardId) {
        Card card = cardStore.get(cardId);
        if (card != null) {
            card.setCurrentSpending(BigDecimal.ZERO);
            cardStore.put(cardId, card);
        }
    }

    /**
     * Check card validity
     */
    public boolean isCardValid(long cardId) {
        Card card = cardStore.get(cardId);
        return card != null && card.isActive();
    }

    // Helper methods
    private String generateCardNumber() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            sb.append((int)(Math.random() * 10));
        }
        return sb.toString();
    }

    private LocalDate generateExpiryDate() {
        return LocalDate.now().plusYears(5);
    }

    private String generateCVV() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            sb.append((int)(Math.random() * 10));
        }
        return sb.toString();
    }

    private boolean isValidCardType(String cardType) {
        return "DEBIT".equals(cardType) || "CREDIT".equals(cardType) || "PREPAID".equals(cardType);
    }
}
