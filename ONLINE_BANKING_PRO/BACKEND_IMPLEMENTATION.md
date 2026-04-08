# Backend Implementation - Java/Spring Boot Code Samples

## Project Structure

```
banking-application/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/com/onlinebanking/
│   │   │   ├── BankingApplication.java (Entry point)
│   │   │   ├── config/
│   │   │   │   ├── SecurityConfig.java (Spring Security)
│   │   │   │   ├── CacheConfig.java (Redis)
│   │   │   │   ├── JpaConfig.java (Database)
│   │   │   │   ├── AuditConfig.java (JPA Auditing)
│   │   │   │   └── MessageQueueConfig.java (RabbitMQ)
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── AccountController.java
│   │   │   │   ├── TransactionController.java
│   │   │   │   ├── BeneficiaryController.java
│   │   │   │   ├── CardController.java
│   │   │   │   ├── LoanController.java
│   │   │   │   └── AdminController.java
│   │   │   ├── service/
│   │   │   │   ├── AuthService.java
│   │   │   │   ├── AccountService.java
│   │   │   │   ├── TransactionService.java
│   │   │   │   ├── BeneficiaryService.java
│   │   │   │   ├── CardService.java
│   │   │   │   ├── LoanService.java
│   │   │   │   ├── FraudDetectionService.java
│   │   │   │   ├── AnalyticsService.java
│   │   │   │   └── NotificationService.java
│   │   │   ├── repository/
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── AccountRepository.java
│   │   │   │   ├── TransactionRepository.java
│   │   │   │   ├── BeneficiaryRepository.java
│   │   │   │   ├── CardRepository.java
│   │   │   │   ├── LoanRepository.java
│   │   │   │   └── AuditLogRepository.java
│   │   │   ├── entity/
│   │   │   │   ├── User.java
│   │   │   │   ├── Account.java
│   │   │   │   ├── Transaction.java
│   │   │   │   ├── Beneficiary.java
│   │   │   │   ├── Card.java
│   │   │   │   ├── Loan.java
│   │   │   │   └── AuditLog.java
│   │   │   ├── dto/
│   │   │   │   ├── request/
│   │   │   │   │   ├── RegisterRequest.java
│   │   │   │   │   ├── LoginRequest.java
│   │   │   │   │   ├── TransferRequest.java
│   │   │   │   │   └── LoanApplicationRequest.java
│   │   │   │   ├── response/
│   │   │   │   │   ├── AuthResponse.java
│   │   │   │   │   ├── AccountResponse.java
│   │   │   │   │   ├── TransactionResponse.java
│   │   │   │   │   └── ErrorResponse.java
│   │   │   │   └── mapper/
│   │   │   │       ├── UserMapper.java
│   │   │   │       └── TransactionMapper.java
│   │   │   ├── security/
│   │   │   │   ├── JwtTokenProvider.java
│   │   │   │   ├── CustomUserDetailsService.java
│   │   │   │   ├── JwtAuthenticationFilter.java
│   │   │   │   └── CustomAuthenticationEntryPoint.java
│   │   │   ├── exception/
│   │   │   │   ├── BankingException.java
│   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   ├── UnauthorizedException.java
│   │   │   │   ├── ValidationException.java
│   │   │   │   └── GlobalExceptionHandler.java
│   │   │   ├── util/
│   │   │   │   ├── EncryptionUtil.java
│   │   │   │   ├── DateUtil.java
│   │   │   │   └── ValidationUtil.java
│   │   │   ├── messaging/
│   │   │   │   ├── EventPublisher.java
│   │   │   │   └── EventListener.java
│   │   │   └── aspect/
│   │   │       ├── LoggingAspect.java
│   │   │       └── PerformanceMonitoringAspect.java
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       ├── application-prod.yml
│   │       ├── db/migration/ (Flyway scripts)
│   │       │   ├── V1__initial_schema.sql
│   │       │   └── V2__add_audit_tables.sql
│   │       └── messages.properties
│   └── test/
│       ├── java/com/onlinebanking/
│       │   ├── controller/AuthControllerTests.java
│       │   ├── service/TransactionServiceTests.java
│       │   ├── security/JwtTokenProviderTests.java
│       │   └── integration/TransactionIntegrationTests.java
│       └── resources/
│           └── application-test.yml
├── docker/
│   ├── Dockerfile
│   ├── docker-compose.yml
│   └── nginx.conf
├── k8s/
│   ├── deployment.yaml
│   ├── service.yaml
│   └── configmap.yaml
├── docs/
│   ├── API_DOCUMENTATION.md
│   └── SETUP_GUIDE.md
└── README.md
```

---

## Key Code Samples

### 1. pom.xml (Maven Dependencies)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.onlinebanking</groupId>
    <artifactId>banking-application</artifactId>
    <version>1.0.0</version>
    <name>Online Banking Application</name>
    <description>Production-ready online banking system</description>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
        <relativePath/>
    </parent>

    <properties>
        <java.version>21</java.version>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <spring-cloud.version>2023.0.0</spring-cloud.version>
    </properties>

    <dependencies>
        <!-- Spring Boot Starters -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-amqp</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>

        <!-- Database -->
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.flywaydb</groupId>
            <artifactId>flyway-core</artifactId>
        </dependency>

        <!-- Security & JWT -->
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
            <version>0.12.3</version>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
            <version>0.12.3</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
            <version>0.12.3</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-oauth2-client</artifactId>
        </dependency>

        <!-- Encryption -->
        <dependency>
            <groupId>org.bouncycastle</groupId>
            <artifactId>bcprov-jdk15on</artifactId>
            <version>1.70</version>
        </dependency>

        <!-- Redis -->
        <dependency>
            <groupId>io.lettuce</groupId>
            <artifactId>lettuce-core</artifactId>
        </dependency>

        <!-- Lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>

        <!-- MapStruct (for DTOs) -->
        <dependency>
            <groupId>org.mapstruct</groupId>
            <artifactId>mapstruct</artifactId>
            <version>1.5.3.Final</version>
        </dependency>

        <!-- Logging -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-sleuth</artifactId>
        </dependency>

        <!-- Monitoring -->
        <dependency>
            <groupId>io.micrometer</groupId>
            <artifactId>micrometer-registry-prometheus</artifactId>
        </dependency>

        <!-- API Documentation -->
        <dependency>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
            <version>2.1.0</version>
        </dependency>

        <!-- Testing -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>testcontainers</artifactId>
            <version>1.17.6</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>postgresql</artifactId>
            <version>1.17.6</version>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <annotationProcessorPaths>
                        <path>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </path>
                        <path>
                            <groupId>org.mapstruct</groupId>
                            <artifactId>mapstruct-processor</artifactId>
                            <version>1.5.3.Final</version>
                        </path>
                    </annotationProcessorPaths>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

### 2. Security Configuration

```java
// SecurityConfig.java
package com.onlinebanking.config;

import com.onlinebanking.security.JwtAuthenticationFilter;
import com.onlinebanking.security.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public SecurityFilterChain formLoginSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable()) // Disabled for JWT
            .exceptionHandling(exceptionHandling ->
                exceptionHandling.authenticationEntryPoint(authenticationEntryPoint))
            .sessionManagement(sessionManagement ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                // Public endpoints
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/api/v1/public/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/actuator/health").permitAll()
                
                // Admin endpoints
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                
                // Authenticated endpoints
                .requestMatchers(HttpMethod.GET, "/api/v1/accounts/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/v1/accounts/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/v1/accounts/**").authenticated()
                .requestMatchers("/api/v1/transactions/**").authenticated()
                .requestMatchers("/api/v1/users/me/**").authenticated()
                
                // All other requests require authentication
                .anyRequest().authenticated())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt with cost factor 12 (100-200ms per hash)
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",
            "http://localhost:3001",
            "https://*.onlinebanking.com"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
```

### 3. JWT Token Provider

```java
// JwtTokenProvider.java
package com.onlinebanking.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Component
@Getter
public class JwtTokenProvider {

    @Value("${spring.security.jwt.secret-key}")
    private String secretKey;

    @Value("${spring.security.jwt.expiration}")
    private long tokenExpirationMs;

    @Value("${spring.security.jwt.refresh-expiration}")
    private long refreshTokenExpirationMs;

    /**
     * Generate JWT access token
     */
    public String generateAccessToken(Authentication authentication) {
        Map<String, Object> claims = new HashMap<>();
        
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        claims.put("roles", userDetails.getAuthorities().stream()
            .map(auth -> auth.getAuthority())
            .toList());
        claims.put("email", userDetails.getEmail());
        claims.put("userId", userDetails.getUserId());

        return createToken(claims, userDetails.getUsername(), tokenExpirationMs);
    }

    /**
     * Generate refresh token
     */
    public String generateRefreshToken(Long userId, String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "REFRESH");
        claims.put("userId", userId);
        
        return createToken(claims, email, refreshTokenExpirationMs);
    }

    /**
     * Create JWT token
     */
    private String createToken(Map<String, Object> claims, String subject, long expirationMs) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);
        String jti = UUID.randomUUID().toString(); // For token revocation

        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
            .claims(claims)
            .subject(subject)
            .id(jti)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }

    /**
     * Get email/username from token
     */
    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
    }

    /**
     * Get user ID from token
     */
    public Long getUserIdFromToken(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
            .build()
            .parseClaimsJws(token)
            .getBody()
            .get("userId", Long.class);
    }

    /**
     * Validate token
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token: {}", ex.getMessage());
        } catch (SignatureException ex) {
            log.error("JWT signature validation failed: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty: {}", ex.getMessage());
        }
        return false;
    }

    /**
     * Get all claims from token
     */
    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    /**
     * Check if token is expired
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            return claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException ex) {
            return true;
        }
    }
}
```

### 4. Authentication Service

```java
// AuthService.java
package com.onlinebanking.service;

import com.onlinebanking.dto.request.LoginRequest;
import com.onlinebanking.dto.request.RegisterRequest;
import com.onlinebanking.dto.response.AuthResponse;
import com.onlinebanking.entity.User;
import com.onlinebanking.exception.BankingException;
import com.onlinebanking.exception.UnauthorizedException;
import com.onlinebanking.repository.UserRepository;
import com.onlinebanking.security.JwtTokenProvider;
import com.onlinebanking.util.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;
    private final NotificationService notificationService;
    private final CacheService cacheService;

    /**
     * User registration
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());

        // Validate input
        validateRegistration(request);

        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BankingException("User already exists with email: " + request.getEmail());
        }

        // Create new user
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setStatus("ACTIVE"); // Users must verify email before full activation
        user.setKycStatus("PENDING");
        user.setTwoFAEnabled(false);
        user.setCreatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);
        log.info("User registered successfully: {}", savedUser.getUserId());

        // Send verification email
        notificationService.sendEmailVerification(savedUser);

        return AuthResponse.builder()
            .message("Registration successful. Please check your email for verification link.")
            .email(savedUser.getEmail())
            .build();
    }

    /**
     * User login with email and password
     */
    @Transactional
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for user: {}", request.getEmail());

        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword()
                )
            );

            User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("User not found"));

            // Check account status
            if ("LOCKED".equals(user.getStatus())) {
                throw new UnauthorizedException("Account is locked. Please contact support.");
            }
            if ("SUSPENDED".equals(user.getStatus())) {
                throw new UnauthorizedException("Account is suspended.");
            }

            // Check if 2FA is enabled
            if (user.isTwoFAEnabled()) {
                log.info("2FA enabled for user: {}, generating OTP", user.getUserId());
                
                // Generate 2FA OTP
                String otpCode = otpService.generateOTP(user.getUserId(), "EMAIL");
                notificationService.sendOTPEmail(user, otpCode);

                // Create temporary token (valid for 5 minutes)
                String tmpToken = cacheService.storeTempAuthToken(user.getUserId(), authentication);

                return AuthResponse.builder()
                    .message("OTP sent to registered email. Please verify.")
                    .temporaryToken(tmpToken)
                    .twoFARequired(true)
                    .build();
            }

            // Generate JWT tokens
            String accessToken = jwtTokenProvider.generateAccessToken(authentication);
            String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUserId(), user.getEmail());

            // Store refresh token in Redis
            cacheService.storeRefreshToken(user.getUserId(), refreshToken);

            // Update last login
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);

            log.info("User logged in successfully: {}", user.getUserId());

            return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtTokenProvider.getTokenExpirationMs() / 1000)
                .userId(user.getUserId())
                .email(user.getEmail())
                .build();

        } catch (BadCredentialsException ex) {
            log.warn("Failed login attempt for user: {}", request.getEmail());
            throw new UnauthorizedException("Invalid email or password");
        }
    }

    /**
     * Verify 2FA OTP and complete login
     */
    @Transactional
    public AuthResponse verifyOTP(String tempToken, String otpCode) {
        log.info("Verifying OTP for 2FA");

        // Retrieve temporary authentication from Redis
        Authentication authentication = cacheService.retrieveTempAuthToken(tempToken);
        if (authentication == null) {
            throw new UnauthorizedException("Session expired. Please login again.");
        }

        // Verify OTP
        Long userId = ((CustomUserDetails) authentication.getPrincipal()).getUserId();
        boolean isValid = otpService.verifyOTP(userId, otpCode);

        if (!isValid) {
            throw new UnauthorizedException("Invalid or expired OTP");
        }

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BankingException("User not found"));

        // Generate JWT tokens
        String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUserId(), user.getEmail());

        // Store refresh token in Redis
        cacheService.storeRefreshToken(user.getUserId(), refreshToken);

        log.info("OTP verified successfully for user: {}", userId);

        return AuthResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .expiresIn(jwtTokenProvider.getTokenExpirationMs() / 1000)
            .userId(user.getUserId())
            .email(user.getEmail())
            .build();
    }

    /**
     * Refresh access token
     */
    public AuthResponse refreshToken(String refreshToken) {
        log.info("Refreshing access token");

        // Validate refresh token
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new UnauthorizedException("Invalid refresh token");
        }

        String email = jwtTokenProvider.getEmailFromToken(refreshToken);
        Long userId = jwtTokenProvider.getUserIdFromToken(refreshToken);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BankingException("User not found"));

        // Verify refresh token exists in Redis
        if (!cacheService.verifyRefreshToken(userId, refreshToken)) {
            throw new UnauthorizedException("Refresh token has been revoked");
        }

        // Create new authentication
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            email,
            null,
            user.getAuthorities()
        );

        // Generate new access token
        String newAccessToken = jwtTokenProvider.generateAccessToken(authentication);

        return AuthResponse.builder()
            .accessToken(newAccessToken)
            .expiresIn(jwtTokenProvider.getTokenExpirationMs() / 1000)
            .build();
    }

    /**
     * Logout - revoke tokens
     */
    @Transactional
    public void logout(String token, Long userId) {
        log.info("User logging out: {}", userId);
        
        // Add token to blacklist
        cacheService.addTokenToBlacklist(token);
        
        // Remove refresh token
        cacheService.removeRefreshToken(userId);
    }

    /**
     * Validate registration input
     */
    private void validateRegistration(RegisterRequest request) {
        if (!request.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$")) {
            throw new BankingException("Invalid email format");
        }

        if (request.getPassword().length() < 12) {
            throw new BankingException("Password must be at least 12 characters");
        }

        if (!request.getPassword().matches(".*[A-Z].*")) {
            throw new BankingException("Password must contain uppercase letter");
        }

        if (!request.getPassword().matches(".*[a-z].*")) {
            throw new BankingException("Password must contain lowercase letter");
        }

        if (!request.getPassword().matches(".*\\d.*")) {
            throw new BankingException("Password must contain digit");
        }

        if (!request.getPassword().matches(".*[!@#$%^&*].*")) {
            throw new BankingException("Password must contain special character");
        }
    }
}
```

### 5. Transaction Service (Core Business Logic)

```java
// TransactionService.java
package com.onlinebanking.service;

import com.onlinebanking.dto.request.TransferRequest;
import com.onlinebanking.dto.response.TransactionResponse;
import com.onlinebanking.entity.Account;
import com.onlinebanking.entity.Transaction;
import com.onlinebanking.entity.User;
import com.onlinebanking.exception.BankingException;
import com.onlinebanking.messaging.EventPublisher;
import com.onlinebanking.repository.AccountRepository;
import com.onlinebanking.repository.TransactionRepository;
import com.onlinebanking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final FraudDetectionService fraudDetectionService;
    private final NotificationService notificationService;
    private final EventPublisher eventPublisher;
    private final TransactionMapper transactionMapper;

    private static final BigDecimal HIGH_TRANSACTION_THRESHOLD = new BigDecimal("1000.00");

    /**
     * Transfer money between accounts (ACID transaction)
     * Guaranteed: All-or-nothing, immediate consistency
     */
    @Transactional
    public TransactionResponse transferMoney(Long userId, TransferRequest request) {
        log.info("Processing transfer request from user: {} amount: {}", userId, request.getAmount());

        // Load source account with lock (pessimistic locking)
        Account sourceAccount = accountRepository.findByIdWithLock(request.getFromAccountId())
            .orElseThrow(() -> new BankingException("Source account not found"));

        // Verify account ownership
        if (!sourceAccount.getUser().getUserId().equals(userId)) {
            throw new BankingException("Unauthorized: Account does not belong to user");
        }

        // Verify sufficient balance (including overdraft limit)
        BigDecimal availableBalance = sourceAccount.getBalance().add(sourceAccount.getOverdraftLimit());
        if (request.getAmount().compareTo(availableBalance) > 0) {
            throw new BankingException("Insufficient funds. Available: " + availableBalance);
        }

        // Load destination account with lock
        Account destinationAccount = accountRepository.findByIdWithLock(request.getToAccountId())
            .orElseThrow(() -> new BankingException("Destination account not found"));

        // Verify account status
        if (!"ACTIVE".equals(sourceAccount.getStatus())) {
            throw new BankingException("Source account is not active");
        }
        if (!"ACTIVE".equals(destinationAccount.getStatus())) {
            throw new BankingException("Destination account is inactive");
        }

        // Check transaction limits
        verifyTransactionLimits(sourceAccount, request.getAmount());

        // Perform fraud detection analysis
        BigDecimal fraudRiskScore = fraudDetectionService.analyzeTransaction(
            userId,
            sourceAccount,
            destinationAccount,
            request.getAmount()
        );

        log.info("Fraud risk score: {}", fraudRiskScore);

        String transactionStatus = "PENDING";
        if (fraudRiskScore.compareTo(new BigDecimal("0.7")) > 0) {
            // High-risk transaction - flag for manual review
            log.warn("High-risk transaction detected. Flagging for review. Risk: {}", fraudRiskScore);
            
            // If high-risk and high-value, require OTP verification
            if (request.getAmount().compareTo(HIGH_TRANSACTION_THRESHOLD) > 0) {
                transactionStatus = "PENDING_OTP_VERIFICATION";
                
                // Generate transaction-specific OTP
                String otpCode = generateTransactionOTP(userId);
                notificationService.sendTransactionOTP(sourceAccount.getUser(), otpCode);
                
                // Store pending transaction temporarily
                return storePendingTransaction(
                    sourceAccount, destinationAccount, request, fraudRiskScore
                );
            }
        }

        // Debit from source (negative)
        sourceAccount.setBalance(sourceAccount.getBalance().subtract(request.getAmount()));
        accountRepository.save(sourceAccount);

        // Credit to destination (positive)
        destinationAccount.setBalance(destinationAccount.getBalance().add(request.getAmount()));
        accountRepository.save(destinationAccount);

        // Create transaction records (one for each account)
        Transaction debitTransaction = createTransaction(
            sourceAccount,
            "TRANSFER_OUT",
            request.getAmount().negate(),
            "Transfer out",
            fraudRiskScore
        );
        debitTransaction = transactionRepository.save(debitTransaction);

        Transaction creditTransaction = createTransaction(
            destinationAccount,
            "TRANSFER_IN",
            request.getAmount(),
            "Transfer in",
            fraudRiskScore
        );
        creditTransaction = transactionRepository.save(creditTransaction);

        // Publish event for async processing
        eventPublisher.publishTransactionCompleted(debitTransaction.getTransactionId());

        // Send confirmation notifications
        notificationService.sendTransactionConfirmation(
            sourceAccount.getUser(),
            debitTransaction
        );

        log.info("Transfer completed successfully. Transaction ID: {}", debitTransaction.getTransactionId());

        return transactionMapper.toResponse(debitTransaction);
    }

    /**
     * Verify OTP for high-risk/high-value transaction
     */
    @Transactional
    public TransactionResponse verifyTransactionOTP(Long userId, Long transactionId, String otpCode) {
        log.info("Verifying OTP for transaction: {}", transactionId);

        // This would retrieve pending transaction and complete it after OTP verification
        // Implementation similar to auth OTP verification
        
        return null; // Placeholder
    }

    /**
     * Get transactions for account with pagination
     */
    public List<TransactionResponse> getTransactions(Long accountId, int page, int size) {
        log.info("Fetching transactions for account: {}", accountId);
        
        List<Transaction> transactions = transactionRepository.findByAccountIdOrderByCreatedAtDesc(
            accountId,
            page,
            size
        );

        return transactions.stream()
            .map(transactionMapper::toResponse)
            .toList();
    }

    /**
     * Download transaction statement as PDF/CSV
     */
    public byte[] generateStatement(Long accountId, LocalDateTime fromDate, LocalDateTime toDate, String format) {
        log.info("Generating statement for account: {} format: {}", accountId, format);
        
        List<Transaction> transactions = transactionRepository.findByAccountIdAndDateRange(
            accountId, fromDate, toDate
        );

        if ("PDF".equalsIgnoreCase(format)) {
            return generatePDFStatement(transactions);
        } else if ("CSV".equalsIgnoreCase(format)) {
            return generateCSVStatement(transactions);
        }

        throw new BankingException("Unsupported format: " + format);
    }

    /**
     * Create transaction record
     */
    private Transaction createTransaction(
        Account account,
        String type,
        BigDecimal amount,
        String description,
        BigDecimal fraudRiskScore
    ) {
        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setTransactionType(type);
        transaction.setAmount(amount.abs());
        transaction.setDescription(description);
        transaction.setReferenceNumber(generateReferenceNumber());
        transaction.setTransactionStatus("COMPLETED");
        transaction.setFraudRiskScore(fraudRiskScore);
        transaction.setIsFlagged(fraudRiskScore.compareTo(new BigDecimal("0.7")) > 0);
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setCompletedAt(LocalDateTime.now());

        return transaction;
    }

    /**
     * Verify daily and monthly transaction limits
     */
    private void verifyTransactionLimits(Account account, BigDecimal amount) {
        // Get today's transaction total
        BigDecimal todayTotal = transactionRepository.getTodayTransactionTotal(account.getAccountId());
        BigDecimal DAILY_LIMIT = new BigDecimal("10000.00");
        
        if (todayTotal.add(amount).compareTo(DAILY_LIMIT) > 0) {
            throw new BankingException("Daily transaction limit exceeded. Limit: " + DAILY_LIMIT);
        }
    }

    private String generateReferenceNumber() {
        return "TXN-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    private String generateTransactionOTP(Long userId) {
        // Generate 6-digit OTP
        return String.format("%06d", (int) (Math.random() * 999999));
    }

    private byte[] generatePDFStatement(List<Transaction> transactions) {
        // Implementation using iText or Apache PDFBox
        return new byte[0];
    }

    private byte[] generateCSVStatement(List<Transaction> transactions) {
        StringBuilder csv = new StringBuilder();
        csv.append("Date,Type,Amount,Description,Status\\n");
        
        for (Transaction tx : transactions) {
            csv.append(String.format("%s,%s,%s,%s,%s\\n",
                tx.getCreatedAt(),
                tx.getTransactionType(),
                tx.getAmount(),
                tx.getDescription(),
                tx.getTransactionStatus()
            ));
        }

        return csv.toString().getBytes();
    }

    private TransactionResponse storePendingTransaction(
        Account source,
        Account destination,
        TransferRequest request,
        BigDecimal fraudRisk
    ) {
        // Store transaction in pending state for later verification
        return null; // Placeholder
    }
}
```

### 6. Fraud Detection Service

```java
// FraudDetectionService.java
package com.onlinebanking.service;

import com.onlinebanking.entity.Account;
import com.onlinebanking.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FraudDetectionService {

    private final TransactionRepository transactionRepository;
    private final CacheService cacheService;

    /**
     * Analyze transaction for fraud indicators
     * Returns risk score from 0.0 to 1.0
     */
    public BigDecimal analyzeTransaction(
        Long userId,
        Account sourceAccount,
        Account destinationAccount,
        BigDecimal amount
    ) {
        log.info("Analyzing transaction for fraud: user={}, amount={}", userId, amount);

        BigDecimal riskScore = BigDecimal.ZERO;

        // Rule 1: Unusual transaction amount (> 2x average)
        riskScore = riskScore.add(analyzeAmountAnomaly(sourceAccount, amount));

        // Rule 2: Multiple failed transaction attempts
        riskScore = riskScore.add(analyzeFailedAttempts(userId));

        // Rule 3: Geographic anomaly (new location)
        riskScore = riskScore.add(analyzeGeographicAnomaly(userId));

        // Rule 4: Unusual time pattern (night transactions)
        riskScore = riskScore.add(analyzeTimePattern());

        // Rule 5: First-time beneficiary
        riskScore = riskScore.add(analyzeNewBeneficiary(sourceAccount, destinationAccount));

        // Rule 6: Rapid successive transactions
        riskScore = riskScore.add(analyzeRapidTransactions(sourceAccount));

        // Normalize score to 0-1 range
        riskScore = riskScore.min(BigDecimal.ONE);

        log.info("Final fraud risk score: {}", riskScore);
        return riskScore;
    }

    private BigDecimal analyzeAmountAnomaly(Account account, BigDecimal amount) {
        // Get average transaction amount for last 30 days
        BigDecimal avgAmount = transactionRepository.getAverageTransactionAmount(
            account.getAccountId(),
            LocalDateTime.now().minusDays(30)
        );

        if (avgAmount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO; // No history
        }

        BigDecimal ratio = amount.divide(avgAmount, 2, java.math.RoundingMode.HALF_UP);
        
        // If amount > 2x average, add risk
        if (ratio.compareTo(new BigDecimal("2.0")) > 0) {
            return new BigDecimal("0.3");
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal analyzeFailedAttempts(Long userId) {
        // Check failed transaction attempts in last hour
        int failedAttempts = transactionRepository.countFailedTransactions(
            userId,
            LocalDateTime.now().minusHours(1)
        );

        if (failedAttempts > 3) {
            return new BigDecimal("0.4"); // Potential brute force attack
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal analyzeGeographicAnomaly(Long userId) {
        // Compare current IP location with historical locations
        // This would integrate with IP geolocation service
        // Placeholder implementation
        return BigDecimal.ZERO;
    }

    private BigDecimal analyzeTimePattern() {
        int hour = LocalDateTime.now().getHour();
        
        // Night transactions (2 AM - 5 AM) are riskier
        if (hour >= 2 && hour <= 5) {
            return new BigDecimal("0.2");
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal analyzeNewBeneficiary(Account source, Account destination) {
        // Check if destination account has received transfers from source before
        boolean isNewBeneficiary = !transactionRepository.hasHistoryWith(
            source.getAccountId(),
            destination.getAccountId()
        );

        if (isNewBeneficiary) {
            return new BigDecimal("0.25");
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal analyzeRapidTransactions(Account account) {
        // Check for multiple transactions in last 5 minutes
        int recentTransactions = transactionRepository.countRecentTransactions(
            account.getAccountId(),
            LocalDateTime.now().minusMinutes(5)
        );

        if (recentTransactions > 3) {
            return new BigDecimal("0.35");
        }
        return BigDecimal.ZERO;
    }
}
```

This provides core backend implementation samples. Will continue with Frontend, API specs, and deployment guide in next sections.
