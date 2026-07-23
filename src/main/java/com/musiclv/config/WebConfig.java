package com.musiclv.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 대용량 미디어(드럼 시퀀스 451장, 배경 영상, 상품 업로드 이미지)는
 * jar 안에 넣지 않고 외부 디렉터리에서 /media/** 로 서빙한다.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final Path mediaDir;

    public WebConfig(@Value("${musiclv.media-dir}") String mediaDir) {
        this.mediaDir = Paths.get(mediaDir).toAbsolutePath().normalize();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/media/**")
                .addResourceLocations(mediaDir.toUri().toString())
                .setCachePeriod(60 * 60 * 24 * 30);
    }
}
