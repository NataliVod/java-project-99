package hexlet.code.controller;

import hexlet.code.dto.AuthRequest;
import hexlet.code.utils.JWTUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class AuthenticationController {

    @Autowired
    private final JWTUtils jwtUtils;

    @Autowired
    private final AuthenticationManager authenticationManager;

    @Operation(summary = "Authenticate user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User authenticated"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/login")
    public String create(
            @Parameter(description = "User data to get authentication")
            @RequestBody AuthRequest authRequest) {
        var authentication = new UsernamePasswordAuthenticationToken(
                authRequest.getUsername(), authRequest.getPassword());

        authenticationManager.authenticate(authentication);

        var token = jwtUtils.generateToken(authRequest.getUsername());
        return token;
    }
}
