package com.reviewmind.review;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReviewAnalysisController {

    private final ReviewAnalysisService service;

    @PostMapping("/analyze")
    public ReviewAnalyzeResponse analyze(
            @RequestBody ReviewAnalyzeRequest request
    ) {
        return service.analyze(request);
    }
}