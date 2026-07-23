package com.musiclv.config;

import com.musiclv.member.Member;
import com.musiclv.member.MemberRepository;
import com.musiclv.member.Role;
import com.musiclv.performance.Performance;
import com.musiclv.performance.PerformanceCategory;
import com.musiclv.performance.PerformanceRepository;
import com.musiclv.product.Category;
import com.musiclv.product.Product;
import com.musiclv.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;

/**
 * 최초 구동 시 관리자 계정과 샘플 상품을 넣는다.
 * 이미 데이터가 있으면 아무것도 하지 않는다.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final PerformanceRepository performanceRepository;
    private final PasswordEncoder passwordEncoder;

    /** 운영 서버에서는 환경변수(ADMIN_EMAIL / ADMIN_PASSWORD)로 반드시 덮어쓴다. */
    @Value("${musiclv.admin.email:admin@musiclv.com}")
    private String adminEmail;

    @Value("${musiclv.admin.password:admin1234}")
    private String adminPassword;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            initAdmin();
            initProducts();
            initPerformances();
        };
    }

    private void initAdmin() {
        if (memberRepository.existsByEmail(adminEmail)) {
            return;
        }
        memberRepository.save(Member.of(
                adminEmail,
                passwordEncoder.encode(adminPassword),
                "관리자",
                "02-0000-0000",
                "경기도 하남 미사동 풀스택 캠프 3층",
                Role.ADMIN
        ));
        if ("admin1234".equals(adminPassword)) {
            log.warn("관리자 계정을 기본 비밀번호로 만들었습니다: {} — 외부에 공개되는 환경이라면 "
                    + "ADMIN_PASSWORD 환경변수로 반드시 바꾸세요.", adminEmail);
        } else {
            log.info("관리자 계정을 생성했습니다: {}", adminEmail);
        }
    }

    private void initProducts() {
        if (productRepository.count() > 0) {
            return;
        }
        productRepository.saveAll(List.of(
                Product.of("Stratocaster Player II", "Fender", Category.GUITAR, 1290000, 8,
                        "알더 바디에 메이플 넥을 얹은 스탠더드 스트랫. 싱글코일 3기로 맑고 단단한 중음이 나옵니다.", null),
                Product.of("Les Paul Standard 60s", "Gibson", Category.GUITAR, 3980000, 3,
                        "마호가니 바디와 메이플 탑. 버스트버커 픽업이 만드는 두툼한 리드 톤이 특징입니다.", null),
                Product.of("Pacifica 612VIIX", "Yamaha", Category.GUITAR, 890000, 12,
                        "입문에서 중급으로 넘어가는 구간에 가장 무난한 선택. HSS 구성으로 장르를 가리지 않습니다.", null),

                Product.of("Jazz Bass Player II", "Fender", Category.BASS, 1350000, 5,
                        "두 개의 싱글코일로 폭넓은 톤을 잡아내는 재즈베이스. 밴드 합주에서 존재감이 확실합니다.", null),
                Product.of("StingRay Special 4", "Music Man", Category.BASS, 3200000, 2,
                        "험버커 한 발과 3밴드 EQ. 슬랩과 핑거 모두에서 단단한 어택을 냅니다.", null),

                Product.of("Export Series 5기통 세트", "Pearl", Category.DRUM, 1180000, 4,
                        "포플러/마호가니 셸 5기통 구성. 스탠드와 페달이 포함된 입문 풀세트입니다.", null),
                Product.of("Superstar Classic Maple", "Tama", Category.DRUM, 2450000, 3,
                        "메이플 셸 특유의 밝고 열린 울림. 라이브와 레코딩 모두에서 검증된 라인입니다.", null),
                Product.of("HD-1 전자드럼", "Roland", Category.DRUM, 780000, 10,
                        "층간소음 걱정 없이 연습할 수 있는 컴팩트 전자드럼. 헤드폰 연결로 새벽 연습도 가능합니다.", null),

                Product.of("FP-30X 디지털피아노", "Roland", Category.KEYBOARD, 980000, 7,
                        "88건반 해머 액션. 무대 반입이 잦은 연주자에게 적당한 무게와 음색입니다.", null),
                Product.of("Nord Stage 4 Compact", "Nord", Category.KEYBOARD, 5900000, 1,
                        "피아노, 오르간, 신스를 한 대에. 세션 현장에서 가장 자주 보이는 스테이지 키보드입니다.", null),
                Product.of("MODX8+ 신디사이저", "Yamaha", Category.KEYBOARD, 2280000, 4,
                        "FM-X와 AWM2 두 엔진을 함께 쓰는 신스. 사운드 메이킹 폭이 넓습니다.", null),

                Product.of("YAS-280 알토색소폰", "Yamaha", Category.WIND, 1650000, 5,
                        "학생용 표준 모델. 음정이 안정적이라 첫 색소폰으로 많이 선택합니다.", null),
                Product.of("TR-300H3 트럼펫", "Bach", Category.WIND, 890000, 6,
                        "황동 벨의 밝은 음색. 관악부와 밴드 세션 모두에 무난합니다.", null),

                Product.of("MV-1 바이올린 4/4", "Suzuki", Category.STRINGS, 620000, 9,
                        "스프루스 상판에 메이플 측후판. 활과 하드케이스가 포함됩니다.", null),

                Product.of("Scarlett 2i2 4세대", "Focusrite", Category.AUDIO, 320000, 20,
                        "홈레코딩 표준 오디오 인터페이스. 마이크와 기타를 동시에 물릴 수 있습니다.", null),
                Product.of("SM58 다이나믹 마이크", "Shure", Category.AUDIO, 149000, 30,
                        "라이브 보컬의 기준점. 험하게 다뤄도 잘 버티는 내구성이 강점입니다.", null),
                Product.of("HD 25 모니터링 헤드폰", "Sennheiser", Category.AUDIO, 259000, 15,
                        "드러머와 엔지니어가 오래 써 온 밀폐형 모니터링 헤드폰입니다.", null),

                Product.of("일렉기타 스트링 009-042 3세트", "Ernie Ball", Category.ACCESSORY, 21000, 100,
                        "가장 무난한 게이지. 벤딩이 편해 입문자에게 권합니다.", null),
                Product.of("5A 히코리 드럼스틱 3켤레", "Vic Firth", Category.ACCESSORY, 36000, 80,
                        "표준 5A 굵기. 장르를 가리지 않는 범용 스틱입니다.", null),
                Product.of("기타 하드케이스", "Gator", Category.ACCESSORY, 145000, 25,
                        "일렉기타용 하드케이스. 이동이 잦은 밴드 활동에 필요합니다.", null)
        ));
        log.info("샘플 상품 {}건을 등록했습니다.", productRepository.count());
    }

    /**
     * 실제로 무대에 올랐던 공연들을 표본으로 넣는다.
     * 공연 기간은 구동 시점을 기준으로 잡아 항상 예매 가능한 상태가 되게 한다.
     */
    private void initPerformances() {
        if (performanceRepository.count() > 0) {
            return;
        }
        LocalDate today = LocalDate.now();

        performanceRepository.saveAll(List.of(
                // ----- 뮤지컬 -----
                Performance.of("레미제라블", PerformanceCategory.MUSICAL,
                        "블루스퀘어 신한카드홀", "민우혁, 조정은, 김우형",
                        today.plusDays(7), today.plusMonths(3), 170, "8세 이상", 140000, 320, null,
                        "빅토르 위고의 소설을 옮긴 대작. 장발장이 은촛대를 받아 드는 순간부터 바리케이드가 무너지는 밤까지, "
                        + "한 사람이 어떻게 다시 사람이 되는지를 따라간다. 'One Day More'의 합창은 무대에서 들어야 한다."),

                Performance.of("오페라의 유령", PerformanceCategory.MUSICAL,
                        "샤롯데씨어터", "조승우, 손지수, 최재림",
                        today.plusDays(14), today.plusMonths(4), 160, "8세 이상", 160000, 280, null,
                        "샹들리에가 객석 위로 떨어지는 그 장면 하나로 기억되는 작품. "
                        + "가면 뒤에 숨은 천재의 집착과 외로움을 앤드루 로이드 웨버의 선율이 끌고 간다."),

                Performance.of("위키드", PerformanceCategory.MUSICAL,
                        "블루스퀘어 신한카드홀", "박혜나, 정선아",
                        today.plusDays(21), today.plusMonths(3), 165, "8세 이상", 150000, 300, null,
                        "오즈의 초록 마녀는 왜 나쁜 마녀가 되었나. 'Defying Gravity'로 1막을 닫는 순간, "
                        + "객석의 공기가 바뀐다."),

                Performance.of("시카고", PerformanceCategory.MUSICAL,
                        "디큐브 링크아트센터", "최정원, 아이비, 박건형",
                        today.plusDays(10), today.plusMonths(2), 150, "13세 이상", 130000, 240, null,
                        "재즈와 살인, 그리고 쇼비즈니스. 무대 위 밴드가 그대로 보이는 연출과 "
                        + "밥 파시 스타일의 안무가 작품의 전부라 해도 좋다."),

                Performance.of("지킬 앤 하이드", PerformanceCategory.MUSICAL,
                        "샤롯데씨어터", "홍광호, 신영숙",
                        today.plusDays(30), today.plusMonths(4), 165, "8세 이상", 145000, 260, null,
                        "한 배우가 한 무대에서 두 사람이 된다. 'This Is The Moment'를 부르는 순간을 "
                        + "보러 오는 관객이 많다."),

                Performance.of("맘마미아!", PerformanceCategory.MUSICAL,
                        "충무아트센터 대극장", "최정원, 전수경, 김영주",
                        today.plusDays(5), today.plusMonths(2), 150, "7세 이상", 120000, 300, null,
                        "ABBA의 노래로 엮은 결혼식 전날의 소동극. 커튼콜에서는 대부분의 관객이 일어선다."),

                // ----- 콘서트 -----
                Performance.of("한로로 단독공연 : 입춘", PerformanceCategory.CONCERT,
                        "예스24 라이브홀", "한로로",
                        today.plusDays(12), today.plusDays(13), 110, "전체 관람가", 88000, 900, null,
                        "'사랑반' '입춘'을 부른 싱어송라이터의 단독 무대. "
                        + "밴드 편성으로 재구성한 세트리스트를 들려준다."),

                Performance.of("아이유 콘서트 : HEREH", PerformanceCategory.CONCERT,
                        "KSPO DOME", "아이유",
                        today.plusDays(25), today.plusDays(27), 150, "7세 이상", 165000, 1200, null,
                        "데뷔곡부터 최근작까지 한 번에 훑는 구성. 밴드 세션과 스트링이 함께 오르는 무대."),

                Performance.of("임영웅 전국투어 : IM HERO", PerformanceCategory.CONCERT,
                        "고척스카이돔", "임영웅",
                        today.plusDays(40), today.plusDays(42), 160, "전체 관람가", 154000, 1500, null,
                        "트로트와 발라드를 오가는 3시간에 가까운 무대. 돔 공연장의 음향에 맞춰 편곡을 새로 짰다."),

                Performance.of("데이식스 콘서트 : The Present", PerformanceCategory.CONCERT,
                        "올림픽공원 핸드볼경기장", "데이식스",
                        today.plusDays(18), today.plusDays(20), 140, "전체 관람가", 99000, 800, null,
                        "네 명이 직접 연주하는 밴드 셋. 'Time of Our Life'에서 객석 전체가 따라 부른다."),

                Performance.of("잔나비 단독공연 : 환상의 나라", PerformanceCategory.CONCERT,
                        "올림픽홀", "잔나비",
                        today.plusDays(33), today.plusDays(34), 120, "전체 관람가", 92000, 700, null,
                        "레트로한 편곡과 밴드 사운드. '주저하는 연인들을 위해'는 마지막에 남겨둔다."),

                Performance.of("실리카겔 단독공연 : POWER ANDRE", PerformanceCategory.CONCERT,
                        "무신사 개러지", "실리카겔",
                        today.plusDays(9), today.plusDays(10), 100, "15세 이상", 77000, 500, null,
                        "사이키델릭한 조명과 함께 가는 라이브. 'Tik Tak Tok'의 후반부는 음원과 전혀 다르다."),

                // ----- 클래식 / 무용 -----
                Performance.of("국립발레단 : 백조의 호수", PerformanceCategory.CLASSIC,
                        "예술의전당 오페라극장", "국립발레단",
                        today.plusDays(16), today.plusDays(20), 140, "8세 이상", 100000, 350, null,
                        "차이콥스키의 음악과 32회전 푸에테. 발레를 처음 본다면 가장 무난한 선택이다."),

                Performance.of("호두까기인형", PerformanceCategory.CLASSIC,
                        "세종문화회관 대극장", "유니버설발레단",
                        today.plusDays(50), today.plusDays(60), 120, "5세 이상", 90000, 400, null,
                        "연말이면 어김없이 오르는 작품. 아이와 함께 보기에 부담이 적다."),

                Performance.of("조성진 피아노 리사이틀", PerformanceCategory.CLASSIC,
                        "롯데콘서트홀", "조성진",
                        today.plusDays(28), today.plusDays(28), 100, "8세 이상", 120000, 200, null,
                        "쇼팽과 라벨을 중심으로 한 독주회. 홀의 잔향까지 계산된 프로그램이다."),

                Performance.of("빈 필하모닉 내한공연", PerformanceCategory.CLASSIC,
                        "예술의전당 콘서트홀", "빈 필하모닉 오케스트라",
                        today.plusDays(45), today.plusDays(46), 130, "8세 이상", 250000, 150, null,
                        "현악의 질감으로 유명한 악단. 브람스 교향곡 전곡 중 2번과 4번을 올린다."),

                Performance.of("지젤", PerformanceCategory.CLASSIC,
                        "국립극장 해오름극장", "국립발레단",
                        today.plusDays(38), today.plusDays(41), 125, "8세 이상", 85000, 300, null,
                        "1막의 시골 마을과 2막의 윌리의 숲이 완전히 다른 공기를 만든다. "
                        + "낭만 발레의 대표작으로 꼽힌다.")
        ));
        log.info("샘플 공연 {}건을 등록했습니다.", performanceRepository.count());
    }
}
