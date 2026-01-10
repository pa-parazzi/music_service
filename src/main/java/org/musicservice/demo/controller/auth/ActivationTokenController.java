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
    private final UserRepository userRepository;

    @Autowired
    public ActivationTokenController(VerificationTokenService verificationTokenService, UserRepository userRepository) {
        this.verificationTokenService = verificationTokenService;
        this.userRepository = userRepository;
    }

    @GetMapping("/activate")
    public ResponseEntity<String> activate(@RequestParam("token") String token){
        VerificationToken verificationToken = verificationTokenService.findByToken(token);
        if(verificationTokenService.isExpiryDate(verificationToken)){
            verificationTokenService.createToken(verificationToken.getUser());
            return ResponseEntity.badRequest().body("Ваш токен активации истек, новый был отправлен на почту " + verificationToken.getUser().getEmail());
        }
        User user = verificationToken.getUser();
        user.setEnabled(true);
        verificationTokenService.delete(verificationToken);
        userRepository.save(user);
        return ResponseEntity.ok("Аккаунт активирован!");
    }
}
