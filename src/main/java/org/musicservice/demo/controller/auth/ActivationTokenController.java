package org.musicservice.demo.controller.auth;


import org.musicservice.demo.entity.user.User;
import org.musicservice.demo.entity.auth.VerificationToken;
import org.musicservice.demo.repository.user.UserRepository;
import org.musicservice.demo.security.verification.VerificationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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
        String message = verificationTokenService.verify(token);
        return ResponseEntity.ok(message);
    }
}
