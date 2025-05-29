package me.heidlund.skiapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.heidlund.skiapp.models.DailySummery;
import me.heidlund.skiapp.models.SlopeRun;
import me.heidlund.skiapp.services.DailyService;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.Charset;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneOffset;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class DateEndpointTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // Used to convert objects to JSON strings

    @Autowired
    DailyService dailyService;

    //@GetMapping("/active-days")
    //    public ResponseEntity<List<LocalDate>> getDays(@RequestParam int year, @RequestParam int month) {
    @Test
    public void testActiveDays() throws Exception {
        dailyService.getOrCreateSummery(new SlopeRun(YearMonth.of(2023,10).atDay(30).toEpochSecond(LocalTime.NOON, ZoneOffset.ofHours(2))*1000,
                0l,0d,0d,0d,0d));
        dailyService.getOrCreateSummery(new SlopeRun(YearMonth.of(2023,11).atDay(1).toEpochSecond(LocalTime.NOON, ZoneOffset.ofHours(2))*1000,
                0l,0d,0d,0d,0d));
        dailyService.getOrCreateSummery(new SlopeRun(YearMonth.of(2023,11).atDay(10).toEpochSecond(LocalTime.NOON, ZoneOffset.ofHours(2))*1000,
                0l,0d,0d,0d,0d));
        dailyService.getOrCreateSummery(new SlopeRun(YearMonth.of(2023,11).atDay(20).toEpochSecond(LocalTime.NOON, ZoneOffset.ofHours(2))*1000,
                0l,0d,0d,0d,0d));
        dailyService.getOrCreateSummery(new SlopeRun(YearMonth.of(2023,11).atDay(30).toEpochSecond(LocalTime.NOON, ZoneOffset.ofHours(2))*1000,
                0l,0d,0d,0d,0d));
        dailyService.getOrCreateSummery(new SlopeRun(YearMonth.of(2023,12).atDay(1).toEpochSecond(LocalTime.NOON, ZoneOffset.ofHours(2))*1000,
                0l,0d,0d,0d,0d));

        mockMvc.perform(get("/active-days").param("year",String.valueOf(2023))
                .param("month",String.valueOf(10)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        mockMvc.perform(get("/active-days")
                .param("year",String.valueOf(2023))
                .param("month",String.valueOf(11)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(4));

         mockMvc.perform(get("/active-days")
                .param("year",String.valueOf(2023))
                .param("month",String.valueOf(10)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }




    //@GetMapping("/summery")
    //    public ResponseEntity<DailySummery> getSummery(@RequestParam LocalDate date) {
    @Test
    public void testDailySummery() throws Exception {
              dailyService.getOrCreateSummery(new SlopeRun(YearMonth.of(2022,10).atDay(30).toEpochSecond(LocalTime.NOON, ZoneOffset.ofHours(2))*1000,
                0l,0d,0d,5d,0d));
        dailyService.getOrCreateSummery(new SlopeRun(YearMonth.of(2022,11).atDay(1).toEpochSecond(LocalTime.NOON, ZoneOffset.ofHours(2))*1000,
                0l,0d,0d,10d,0d));

        String req = mockMvc.perform(get("/active-days")
                .param("year",String.valueOf(2022))
                .param("month",String.valueOf(10)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1)).andReturn().getResponse().getContentAsString(Charset.defaultCharset());

       mockMvc.perform(get("/summery").param("date",req)).andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.top_speed").value(5d));

    }


}
