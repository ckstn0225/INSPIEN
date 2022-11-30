package com.inspien.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
@AllArgsConstructor
public class todayWeatherResponseDto {

//    예보 시간
    String Time;
    // 기온
    String Temperatures;
    // 강수확률
    String RainProbability;
    //  습도
    String Humidity;
    //  강수형태
    String Rain;
//    하늘상태
    String Cloud;



}
