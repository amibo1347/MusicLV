package com.musiclv.booking;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class BookingForm {

    @NotNull(message = "관람일을 선택해주세요.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate viewDate;

    @NotNull(message = "인원을 입력해주세요.")
    @Min(value = 1, message = "1매 이상 예매할 수 있습니다.")
    private Integer quantity;

    @NotBlank(message = "예매자 이름을 입력해주세요.")
    @Size(max = 50)
    private String bookerName;

    @NotBlank(message = "연락처를 입력해주세요.")
    @Size(max = 20)
    private String bookerPhone;
}
