package com.inspien.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
@AllArgsConstructor
public class threeWeatherResponseDto {
    //    예보 시간
    String Time;
    // 기온
    String Temperatures;
    // 강수확률
    String RainProbability;
    //  날씨
    String Weather;
    //  강수형태
    String Rain;

}
