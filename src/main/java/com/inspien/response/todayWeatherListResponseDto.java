package com.inspien.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class todayWeatherListResponseDto {
    String Region;

    String Date;

    List<todayWeatherResponseDto> weather;

}
