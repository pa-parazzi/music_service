package org.musicservice.demo.controller.auth;

import org.musicservice.demo.security.verification.VerificationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/auth")
public class ActivationTokenController {

    private final VerificationTokenService verificationTokenService;

    @Autowired
    public ActivationTokenController(VerificationTokenService verificationTokenService) {
        this.verificationTokenService = verificationTokenService;
    }

    @GetMapping("/activate")
    public ResponseEntity<String> activate(@RequestParam("token") String token){
        return ResponseEntity.ok(verificationTokenService.verify(token));
    }
}
