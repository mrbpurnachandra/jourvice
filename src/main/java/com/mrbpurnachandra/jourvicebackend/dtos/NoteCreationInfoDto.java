package com.mrbpurnachandra.jourvicebackend.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoteCreationInfoDto {
    @NotBlank
    @Size(min = 2, max = 256)
    private String content;

    private MoodDto mood;

    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MoodDto {
        private Integer id;
    }

    public void setContent(String content) {
        this.content = content.trim();
    }
}
