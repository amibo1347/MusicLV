package com.musiclv.config;

import com.musiclv.member.Member;
import com.musiclv.member.MemberRepository;
import com.musiclv.member.Role;
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
}
