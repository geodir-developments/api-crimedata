package com.geodir.apidatacrime.apidatacrime.domain.ratelimits;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Refill;

import java.time.Duration;
//import io.github.bucket4j.Bandwidth;
//import io.github.bucket4j.Refill;
public enum PricingPlan {

    FREE(10),

    BASIC(40),

    PROFESSIONAL(100);

    private int bucketCapacity;

    private PricingPlan(int bucketCapacity) {
        this.bucketCapacity = bucketCapacity;
    }

    /*Bandwidth getLimit() {
        return Bandwidth.classic(bucketCapacity, Refill.intervally(bucketCapacity, Duration.ofHours(1)));
    }*/

    Bandwidth getLimit() {
        return Bandwidth.classic(bucketCapacity, Refill.intervally(bucketCapacity, Duration.ofSeconds(60)));
    }

    public int bucketCapacity() {
        return bucketCapacity;
    }

    static PricingPlan resolvePlanFromApiKey(String apiKey) {
        if (apiKey == null || apiKey.isEmpty()) {
            return FREE;

        } else if (apiKey.startsWith("PX001-")) {
            return PROFESSIONAL;

        } else if (apiKey.startsWith("BX001-")) {
            return BASIC;
        }
        return FREE;
    }

}
