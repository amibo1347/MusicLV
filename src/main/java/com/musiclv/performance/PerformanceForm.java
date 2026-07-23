package com.musiclv.performance;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Getter
@Setter
public class PerformanceForm {

    private Long id;

    @NotBlank(message = "공연명을 입력해주세요.")
    @Size(max = 150)
    private String title;

    @NotNull(message = "카테고리를 선택해주세요.")
    private PerformanceCategory category;

    @NotBlank(message = "공연장을 입력해주세요.")
    @Size(max = 120)
    private String venue;

    @Size(max = 255)
    private String cast;

    @NotNull(message = "시작일을 입력해주세요.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @NotNull(message = "종료일을 입력해주세요.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

    @NotNull(message = "관람 시간을 입력해주세요.")
    @Min(value = 1, message = "관람 시간은 1분 이상이어야 합니다.")
    private Integer runningTime;

    @Size(max = 40)
    private String ageRating;

    @NotNull(message = "가격을 입력해주세요.")
    @Min(value = 0, message = "가격은 0원 이상이어야 합니다.")
    private Integer price;

    @NotNull(message = "좌석 수를 입력해주세요.")
    @Min(value = 0, message = "좌석 수는 0석 이상이어야 합니다.")
    private Integer seats;

    private String description;

    private MultipartFile posterFile;

    private String posterUrl;

    /** 종료일이 시작일보다 빠르면 안 된다. */
    public boolean isPeriodValid() {
        return startDate == null || endDate == null || !endDate.isBefore(startDate);
    }

    public static PerformanceForm from(Performance p) {
        PerformanceForm f = new PerformanceForm();
        f.id = p.getId();
        f.title = p.getTitle();
        f.category = p.getCategory();
        f.venue = p.getVenue();
        f.cast = p.getCast();
        f.startDate = p.getStartDate();
        f.endDate = p.getEndDate();
        f.runningTime = p.getRunningTime();
        f.ageRating = p.getAgeRating();
        f.price = p.getPrice();
        f.seats = p.getSeats();
        f.description = p.getDescription();
        f.posterUrl = p.getPosterUrl();
        return f;
    }
}
