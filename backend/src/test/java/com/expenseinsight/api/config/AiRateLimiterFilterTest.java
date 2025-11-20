package com.expenseinsight.api.config;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AiRateLimiterFilterTest {

    @Mock
    private FilterChain filterChain;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void determinesFreeTierLimitForUnauthenticatedRequests() {
        AiRateLimiterFilter filter = buildFilterWithLimits(10L, 20L, 200L);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/ai/prompt");

        Long limit = ReflectionTestUtils.invokeMethod(filter, "determineLimitForRequest", request);

        assertEquals(10L, limit);
    }

    @Test
    void determinesPremiumLimitWhenRoleIsPresent() {
        AiRateLimiterFilter filter = buildFilterWithLimits(5L, 20L, 250L);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/ai/prompt");

        TestingAuthenticationToken authenticationToken = new TestingAuthenticationToken(
                "user", "password", java.util.Collections.singletonList(new SimpleGrantedAuthority("ROLE_PREMIUM")));
        authenticationToken.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        Long limit = ReflectionTestUtils.invokeMethod(filter, "determineLimitForRequest", request);

        assertEquals(250L, limit);
    }

    @Test
    void enforcesRateLimitForFreeTier() throws Exception {
        AiRateLimiterFilter filter = buildFilterWithLimits(2L, 2L, 5L);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/ai/prompt");
        request.setRemoteAddr("127.0.0.1");

        MockHttpServletResponse firstResponse = new MockHttpServletResponse();
        filter.doFilterInternal(request, firstResponse, filterChain);
        assertEquals(200, firstResponse.getStatus());

        MockHttpServletResponse secondResponse = new MockHttpServletResponse();
        filter.doFilterInternal(request, secondResponse, filterChain);
        assertEquals(200, secondResponse.getStatus());

        MockHttpServletResponse blockedResponse = new MockHttpServletResponse();
        filter.doFilterInternal(request, blockedResponse, filterChain);
        assertEquals(429, blockedResponse.getStatus());

        verify(filterChain, times(2)).doFilter(any(), any());
    }

    private AiRateLimiterFilter buildFilterWithLimits(long freeLimit, long standardLimit, long premiumLimit) {
        AiRateLimiterFilter filter = new AiRateLimiterFilter();
        ReflectionTestUtils.setField(filter, "freeRequestsPerMinute", freeLimit);
        ReflectionTestUtils.setField(filter, "requestsPerMinute", standardLimit);
        ReflectionTestUtils.setField(filter, "capacity", standardLimit);
        ReflectionTestUtils.setField(filter, "premiumRequestsPerMinute", premiumLimit);
        return filter;
    }
}