package com.sushil.poc;

import com.sushil.poc.exception.LowerVersionException;
import com.sushil.poc.exception.MaturityDateException;
import com.sushil.poc.model.Trade;
import com.sushil.poc.repository.jpa.TradeRepository;
import com.sushil.poc.service.TradeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class TradeServiceTests {
    @Autowired
    private TradeService tradeService;
    @Autowired
    private TradeRepository repo;

    @BeforeEach
    public void cleanup() {
        repo.deleteAll();
    }

    @Test
    public void shouldRejectLowerVersion() {
        Trade t1 = new Trade();
        t1.setTradeId("T1");
        t1.setVersion(2);
        t1.setMaturityDate(LocalDate.now().plusDays(10));
        tradeService.acceptTrade(t1);

        Trade lower = new Trade();
        lower.setTradeId("T1");
        lower.setVersion(1);
        lower.setMaturityDate(LocalDate.now().plusDays(10));

        assertThrows(LowerVersionException.class, () -> tradeService.acceptTrade(lower));
    }

    @Test
    public void shouldRejectMaturityBeforeToday() {
        Trade t = new Trade();
        t.setTradeId("T2");
        t.setVersion(1);
        t.setMaturityDate(LocalDate.now().minusDays(1));

        assertThrows(MaturityDateException.class, () -> tradeService.acceptTrade(t));
    }

    @Test
    public void shouldReplaceOnSameVersion() {
        Trade t1 = new Trade();
        t1.setTradeId("T3");
        t1.setVersion(1);
        t1.setCounterPartyId("CP-1");
        t1.setMaturityDate(LocalDate.now().plusDays(5));
        tradeService.acceptTrade(t1);

        Trade t2 = new Trade();
        t2.setTradeId("T3");
        t2.setVersion(1);
        t2.setCounterPartyId("CP-2");
        t2.setMaturityDate(LocalDate.now().plusDays(5));

        Trade saved = tradeService.acceptTrade(t2);
        assertEquals("CP-2", saved.getCounterPartyId());
    }

    @Test
    public void shouldMarkExpiredTrades() {
        // Step 1: Insert trade with valid (future) maturity date
        Trade t = new Trade();
        t.setTradeId("T4");
        t.setVersion(1);
        t.setMaturityDate(LocalDate.now().plusDays(2));
        tradeService.acceptTrade(t);

        // Step 2: Set maturity date to past and save directly (bypassing validation)
        Trade inserted = repo.findAll().get(0);
        inserted.setMaturityDate(LocalDate.now().minusDays(2));
        repo.save(inserted);

        // Step 3: Mark expired trades
        int expired = tradeService.markExpiredTrades();
        assertEquals(1, expired);

        // Step 4: Verify the trade is marked as expired
        // Reload the trade from the database to get the updated state
        Trade reloaded = repo.findById(inserted.getId()).orElseThrow();
        assertTrue(reloaded.isExpired());
    }

    // Additional test case to verify trade with today's maturity date is accepted
    @Test
    public void shouldAcceptTradeWithTodayMaturityDate() {
        Trade t = new Trade();
        t.setTradeId("T5");
        t.setVersion(1);
        t.setMaturityDate(LocalDate.now());

        Trade saved = tradeService.acceptTrade(t);
        assertNotNull(saved);
        assertEquals("T5", saved.getTradeId());
    }

    // Test case for higher version trade acceptance
    @Test
    public void shouldAcceptHigherVersion() {
        Trade t1 = new Trade();
        t1.setTradeId("T6");
        t1.setVersion(1);
        t1.setMaturityDate(LocalDate.now().plusDays(10));
        tradeService.acceptTrade(t1);

        Trade higher = new Trade();
        higher.setTradeId("T6");
        higher.setVersion(2);
        higher.setMaturityDate(LocalDate.now().plusDays(10));

        Trade saved = tradeService.acceptTrade(higher);
        assertNotNull(saved);
        assertEquals(2, saved.getVersion());
    }
}