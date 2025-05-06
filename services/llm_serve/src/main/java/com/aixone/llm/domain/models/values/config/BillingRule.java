package com.aixone.llm.domain.models.values.config;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class BillingRule {
    private String currency;
    private BigDecimal inputTokenPrice;  // Price per 1K input tokens
    private BigDecimal outputTokenPrice; // Price per 1K output tokens
    private BigDecimal minimumCharge;    // Minimum charge per request
    
    @Builder.Default
    private boolean enabled = true;
    
    public boolean isValid() {
        return currency != null && !currency.isBlank() &&
               inputTokenPrice != null && inputTokenPrice.compareTo(BigDecimal.ZERO) >= 0 &&
               outputTokenPrice != null && outputTokenPrice.compareTo(BigDecimal.ZERO) >= 0 &&
               minimumCharge != null && minimumCharge.compareTo(BigDecimal.ZERO) >= 0;
    }
    
    public BigDecimal calculateCost(int inputTokens, int outputTokens) {
        if (!enabled) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal inputCost = inputTokenPrice.multiply(BigDecimal.valueOf(inputTokens))
                                            .divide(BigDecimal.valueOf(1000));
        BigDecimal outputCost = outputTokenPrice.multiply(BigDecimal.valueOf(outputTokens))
                                              .divide(BigDecimal.valueOf(1000));
        BigDecimal totalCost = inputCost.add(outputCost);
        
        return totalCost.max(minimumCharge);
    }
} 