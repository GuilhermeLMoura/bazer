package bazer.domain.analytics.controller;

import bazer.domain.analytics.dto.AdminAnalyticsDto;
import bazer.domain.analytics.dto.StoreAnalyticsDto;
import bazer.domain.analytics.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/store")
    public ResponseEntity<StoreAnalyticsDto> getStoreAnalytics() {
        return ResponseEntity.ok(analyticsService.getStoreAnalytics());
    }

    @GetMapping("/admin")
    public ResponseEntity<AdminAnalyticsDto> getAdminAnalytics(
            @RequestParam(required = false) Integer year) {
        return ResponseEntity.ok(analyticsService.getAdminAnalytics(year));
    }

    @GetMapping("/admin/store/{profileId}")
    public ResponseEntity<StoreAnalyticsDto> getStoreAnalyticsById(@PathVariable Long profileId) {
        return ResponseEntity.ok(analyticsService.getStoreAnalyticsById(profileId));
    }
}