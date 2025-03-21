# Documentación: Autenticación JWT en Spring Boot

## Introducción

Este documento describe la implementación de autenticación y autorización utilizando JWT (JSON Web Tokens) en una aplicación Spring Boot. El sistema permite gestión de usuarios con diferentes roles y protección de endpoints según los permisos de cada rol.

## Estructura del Proyecto

La aplicación está organizada siguiendo la estructura estándar de un proyecto Spring Boot con las siguientes partes principales:

- **Modelos**: Entidades JPA para persistencia de datos
- **Repositorios**: Interfaces para acceso a datos
- **Controladores**: Endpoints REST para interactuar con la aplicación
- **Seguridad**: Configuración de seguridad y componentes JWT
- **DTOs (Payload)**: Objetos para transferencia de datos entre cliente y servidor

## Componentes Principales

### Modelos (Entidades)

#### `Role.java`
```java
@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ERole name;
    
    public Role(ERole name) {
        this.name = name;
    }
}
```

#### `ERole.java`
```java
public enum ERole {
    ROLE_DOCENTE,
    ROLE_RECTOR
}
```

### Seguridad

#### `WebSecurityConfig.java`
```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {
    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> 
                auth.requestMatchers(new AntPathRequestMatcher("/api/auth/**")).permitAll()
                    .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()
                    .requestMatchers(new AntPathRequestMatcher("/v3/api-docs/**")).permitAll()
                    .requestMatchers(new AntPathRequestMatcher("/swagger-ui/**")).permitAll()
                    .requestMatchers(new AntPathRequestMatcher("/swagger-ui.html")).permitAll()
                    .requestMatchers(new AntPathRequestMatcher("/api/docente/**")).hasRole("DOCENTE")
                    .requestMatchers(new AntPathRequestMatcher("/api/rector/**")).hasRole("RECTOR")
                    .requestMatchers(new AntPathRequestMatcher("/api/users/**")).hasRole("RECTOR")
                    .anyRequest().authenticated()
            );
        
        // Fix for H2 console
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));
        
        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
```

#### `JwtUtils.java`
```java
@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpirationMs;

    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject((userPrincipal.getUsername()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(authToken);
            return true;
        } catch (SecurityException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }
    
    public String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }
}
```

#### `AuthTokenFilter.java`
```java
public class AuthTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = jwtUtils.parseJwt(request);
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String username = jwtUtils.getUserNameFromJwtToken(jwt);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails,
                                                                null,
                                                                userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e);
        }

        filterChain.doFilter(request, response);
    }
}
```

### Controladores

#### `AuthController.java`
```java
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        // Procesar la contraseña (puede estar en texto plano o Base64)
        String processedPassword = PasswordUtil.processPassword(loginRequest.getPassword());

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), processedPassword));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();        
        List<String> roles = userDetails.getAuthorities().stream()
            .map(item -> item.getAuthority())
            .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt, 
                                                 userDetails.getId(), 
                                                 userDetails.getUsername(), 
                                                 roles));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: El nombre de usuario ya está en uso!"));
        }

        // Procesar la contraseña (puede estar en texto plano o Base64)
        String processedPassword = PasswordUtil.processPassword(signUpRequest.getPassword());

        // Crear la cuenta de usuario con la contraseña procesada
        User user = new User(signUpRequest.getUsername(), 
                             encoder.encode(processedPassword));

        Set<String> strRoles = signUpRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role docenteRole = roleRepository.findByName(ERole.ROLE_DOCENTE)
                .orElseThrow(() -> new RuntimeException("Error: Rol no encontrado."));
            roles.add(docenteRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "rector":
                        Role rectorRole = roleRepository.findByName(ERole.ROLE_RECTOR)
                            .orElseThrow(() -> new RuntimeException("Error: Rol no encontrado."));
                        roles.add(rectorRole);
                        break;
                    default:
                        Role docenteRole = roleRepository.findByName(ERole.ROLE_DOCENTE)
                            .orElseThrow(() -> new RuntimeException("Error: Rol no encontrado."));
                        roles.add(docenteRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("Usuario registrado exitosamente!"));
    }
}
```

## Flujo de Autenticación

1. El cliente envía una solicitud POST a `/api/auth/login` con credenciales (nombre de usuario y contraseña).
2. El controlador autentica las credenciales y, si son válidas, genera un token JWT.
3. El token JWT se devuelve al cliente junto con información del usuario.
4. Para solicitudes posteriores a endpoints protegidos, el cliente debe incluir el token JWT en el encabezado "Authorization" con el formato "Bearer [token]".
5. El filtro `AuthTokenFilter` intercepta cada solicitud, extrae el token JWT del encabezado y valida su autenticidad.
6. Si el token es válido, se establece la autenticación en el contexto de seguridad.
7. La solicitud continúa al controlador correspondiente si el usuario tiene los permisos necesarios.

## Roles y Permisos

El sistema implementa dos roles principales:
- **ROLE_DOCENTE**: Acceso a recursos limitados para docentes.
- **ROLE_RECTOR**: Acceso administrativo con permisos adicionales.

La configuración de seguridad en `WebSecurityConfig.java` define qué roles pueden acceder a qué endpoints:
- `/api/docente/**`: Accesible para usuarios con rol DOCENTE
- `/api/rector/**`: Accesible para usuarios con rol RECTOR
- `/api/users/**`: Accesible para usuarios con rol RECTOR

## Registro y Autenticación

### Registro de Usuario
El endpoint `/api/auth/register` permite crear nuevos usuarios con los siguientes pasos:
1. Valida que el nombre de usuario no esté ya en uso.
2. Procesa y codifica la contraseña.
3. Asigna roles al usuario según la solicitud.
4. Guarda el usuario en la base de datos.

### Inicio de Sesión
El endpoint `/api/auth/login` maneja la autenticación:
1. Valida las credenciales del usuario.
2. Genera un token JWT con información del usuario.
3. Devuelve el token junto con detalles del usuario autenticado.

## Conclusión

Esta implementación proporciona un sistema de autenticación y autorización seguro utilizando JWT en Spring Boot. La arquitectura está diseñada para ser extensible y fácil de mantener, con una clara separación de responsabilidades entre los diferentes componentes. 