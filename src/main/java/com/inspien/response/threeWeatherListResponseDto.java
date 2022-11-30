package com.inspien.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Setter
@Getter
@AllArgsConstructor
public class threeWeatherListResponseDto {
    String Region;

    String Date;

    List<threeWeatherResponseDto> Weather;

}
