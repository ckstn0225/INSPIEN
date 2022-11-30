package com.inspien.response;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class currentWeatherResponseDto {

    String Location;

    String CurrentTime;
// 기온
    String Temperatures;
// 강수량
    String Precipitation;
//  습도
    String Humidity;
//  강수형태
    String Rain;

    public currentWeatherResponseDto(String location, String currentTime) {
        Location = location;
        CurrentTime = currentTime;
    }
}
