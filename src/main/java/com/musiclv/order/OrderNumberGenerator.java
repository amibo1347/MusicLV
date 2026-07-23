package com.musiclv.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 주문번호를 만든다. 예) MLV-20260723-K7X2QP
 *
 * 비회원은 이 번호로 주문을 찾으므로 추측이 어려워야 한다.
 * 뒤 6자리를 난수로 채우고, 혹시 겹치면 다시 뽑는다.
 */
@Component
@RequiredArgsConstructor
public class OrderNumberGenerator {

    /** 0/O, 1/I 처럼 헷갈리는 글자는 뺐다. 전화로 불러줄 일이 있다. */
    private static final char[] ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789".toCharArray();
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final int SUFFIX_LENGTH = 6;
    private static final int MAX_ATTEMPTS = 10;

    private final SecureRandom random = new SecureRandom();
    private final OrderRepository orderRepository;

    public String generate() {
        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            String candidate = "MLV-" + LocalDate.now().format(DATE) + "-" + randomSuffix();
            if (!orderRepository.existsByOrderNumber(candidate)) {
                return candidate;
            }
        }
        throw new IllegalStateException("주문번호를 생성하지 못했습니다. 잠시 후 다시 시도해주세요.");
    }

    private String randomSuffix() {
        StringBuilder sb = new StringBuilder(SUFFIX_LENGTH);
        for (int i = 0; i < SUFFIX_LENGTH; i++) {
            sb.append(ALPHABET[random.nextInt(ALPHABET.length)]);
        }
        return sb.toString();
    }
}
