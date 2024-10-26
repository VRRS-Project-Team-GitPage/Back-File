package com.shinhan.VRRS.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class FeedbackDTO {
    @NotNull
    @Pattern(regexp = "^(DUP : \\d{1,4}|ERR : \\d{1,4}|RD|ECT)$")
    private String type = "ECT";
    @NotBlank
    @Size(max = 500)
    private String content;
}
