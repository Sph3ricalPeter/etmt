package cz.cvut.fel.pro.etmt.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import cz.cvut.fel.pro.etmt.payload.ApiResponse;

@Controller
public class AppController {

    @GetMapping("/alive")
    public ResponseEntity<ApiResponse> alive() {
        return ResponseEntity.ok().body(new ApiResponse("OK"));
    }

}
