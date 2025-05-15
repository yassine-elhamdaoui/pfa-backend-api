package com.pfa.api.app.auth;

import java.sql.SQLIntegrityConstraintViolationException;

import org.hibernate.PropertyValueException;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.pfa.api.app.JsonRsponse.JsonResponse;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;
    
    @PostMapping("/register")
    public ResponseEntity<JsonResponse> register(@ModelAttribute RegisterDTO request,
                @RequestParam(value = "image", required = false) MultipartFile image) throws SQLIntegrityConstraintViolationException, PropertyValueException, NotFoundException{
        // using this part when i want that confirmation stuff
        authenticationService.register(request,image);
        return new ResponseEntity<JsonResponse>(new JsonResponse(201,
                "Account registered successfully , Your request will be sent to the head of the branch you're in to check you're credentials , you will get an email informing you about you're account's state."),
                HttpStatus.CREATED);
        
        // return ResponseEntity.ok(authenticationService.register(request,image));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationDTO request)throws JwtException{
        return ResponseEntity.ok(authenticationService.authenticate(request));

    }

    @PostMapping("/forgot-password")
    public ResponseEntity<JsonResponse> forgotPassword(@RequestBody String email){
        authenticationService.forgotPassword(email);
        return new ResponseEntity<JsonResponse>(new JsonResponse(200, "A security reset token has been sent to you're email"), HttpStatus.OK);
    }

    @PostMapping("/validate-token")
    public ResponseEntity<JsonResponse> validateResetToken(@RequestParam("token") String token,
            @RequestParam("email") String email){
        authenticationService.validateResetToken(email,token);
        return new ResponseEntity<JsonResponse>(new JsonResponse(200, "The token is valid"), HttpStatus.OK);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<JsonResponse> resetPassword(@RequestParam("token") String token,
            @RequestParam("password") String password){
        authenticationService.resetPassword(token,password);
        return new ResponseEntity<JsonResponse>(new JsonResponse(200, "Password has been reset"), HttpStatus.OK);
    }

    @GetMapping("/verify")
    public ModelAndView confirmUserAccount(@RequestParam("token") String token) {
        Boolean isUserConfirmed = authenticationService.validateToken(token);
        ModelAndView modelAndView = new ModelAndView();

        if (isUserConfirmed) {
            // User account is confirmed
            modelAndView.setViewName("verification-success");
            modelAndView.setStatus(HttpStatus.OK);
        } else {
            // Token is not confirmed
            modelAndView.setViewName("verification-failure");
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
        }

        return modelAndView;
    }

    @GetMapping("/accept/token/{token}")
    public ModelAndView acceptUserAccount(@PathVariable("token") String token){
        authenticationService.acceptUserByToken(token);
        ModelAndView modelAndView = new ModelAndView();

        // return new ResponseEntity<JsonResponse>(new JsonResponse(200, "you account has been confirmed"), HttpStatus.OK);
        modelAndView.setViewName("accept-user"); 
        modelAndView.setStatus(HttpStatus.OK);
        
        return modelAndView;

    }

    @GetMapping("/reject/token/{token}")
    public ModelAndView rejectUserAccount(@PathVariable("token") String token){
        authenticationService.rejectUserByToken(token);
        ModelAndView modelAndView = new ModelAndView();

        // return new ResponseEntity<JsonResponse>(new JsonResponse(200, "you account has been confirmed"), HttpStatus.OK);
        modelAndView.setViewName("reject-user"); 
        modelAndView.setStatus(HttpStatus.OK);
        
        return modelAndView;

    }

    @PostMapping("/accept")
    // @PreAuthorize("hasRole('ROLE_HEAD_OF_BRANCH')")
    public ResponseEntity<JsonResponse> acceptUser(@RequestParam("user") Long user){
        authenticationService.acceptUser(user);
        return new ResponseEntity<JsonResponse>(new JsonResponse(200, "The user is accepted ,he can login now"), HttpStatus.OK);

    }

    @PostMapping("/reject")
    // @PreAuthorize("hasRole('ROLE_HEAD_OF_BRANCH')")
    public ResponseEntity<JsonResponse> rejectUser(@RequestParam("user") Long user){
        authenticationService.rejectUser(user);
        return new ResponseEntity<JsonResponse>(new JsonResponse(200, "The user is rejected ,he can't login now"), HttpStatus.OK);

    }
}
