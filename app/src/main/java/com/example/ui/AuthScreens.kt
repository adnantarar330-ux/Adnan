package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Profile
import com.example.data.Repository
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ==========================================
// 1. Splash Screen
// ==========================================
@Composable
fun SplashScreen(onSplashComplete: () -> Unit) {
    var startAnimation by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(key1 = true) {
        startAnimation = true
        // Animate progress bar
        while (progress < 1f) {
            delay(30)
            progress += 0.02f
        }
        delay(300)
        onSplashComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(DeepSlateBackground, DarkGreyBackground)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Animated Glowing Logo
            AnimatedVisibility(
                visible = startAnimation,
                enter = fadeIn(animationSpec = tween(1200)) + scaleIn(animationSpec = tween(1200))
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(PrimaryAccent, Color.Transparent)
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = "Logo Icon",
                        tint = RosePremium,
                        modifier = Modifier.size(64.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Animated App Name
            Text(
                text = "CINESTREAM",
                color = WhitePrimary,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 6.sp,
                style = MaterialTheme.typography.headlineLarge
            )

            Text(
                text = "Stream the Future in Premium 4K",
                color = GraySecondary,
                fontSize = 14.sp,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Premium Cinematic Loader
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .width(200.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = RosePremium,
                    trackColor = CardSlateBackground,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${(progress * 100).toInt()}% Initializing Cinema Engine",
                    color = GraySecondary,
                    fontSize = 11.sp,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

// ==========================================
// 2. Login Screen
// ==========================================
@Composable
fun LoginScreen(
    repository: Repository,
    onLoginSuccess: () -> Unit,
    onNavigateToSignup: () -> Unit,
    onNavigateToForgotPassword: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var emailOrUser by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(true) }
    var loginError by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSlateBackground),
        contentAlignment = Alignment.Center
    ) {
        // Decorative background glow
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(300.dp)
                .offset(x = 100.dp, y = (-50).dp)
                .background(Brush.radialGradient(listOf(SecondaryAccent.copy(alpha = 0.15f), Color.Transparent)))
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 400.dp)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title
            Text(
                text = "Welcome Back",
                color = WhitePrimary,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Sign in to access your cinematic sanctuary",
                color = GraySecondary,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 32.dp)
            )

            if (loginError.isNotEmpty()) {
                Text(
                    text = loginError,
                    color = RosePremium,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Username/Email Field
            OutlinedTextField(
                value = emailOrUser,
                onValueChange = { emailOrUser = it; loginError = "" },
                label = { Text("Email or Username", color = GraySecondary) },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = "EmailIcon", tint = PrimaryAccent) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_username_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = WhitePrimary,
                    unfocusedTextColor = WhitePrimary,
                    focusedBorderColor = PrimaryAccent,
                    unfocusedBorderColor = CardSlateBackground
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password Field
            OutlinedTextField(
                value = password,
                onValueChange = { password = it; loginError = "" },
                label = { Text("Password", color = GraySecondary) },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "PasswordIcon", tint = PrimaryAccent) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_password_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = WhitePrimary,
                    unfocusedTextColor = WhitePrimary,
                    focusedBorderColor = PrimaryAccent,
                    unfocusedBorderColor = CardSlateBackground
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Forgot password & Remember me Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { rememberMe = it },
                        colors = CheckboxDefaults.colors(checkedColor = PrimaryAccent)
                    )
                    Text("Remember me", color = GraySecondary, fontSize = 13.sp)
                }

                Text(
                    text = "Forgot Password?",
                    color = PrimaryAccent,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .clickable { onNavigateToForgotPassword() }
                        .testTag("forgot_password_link")
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Login Button
            Button(
                onClick = {
                    if (emailOrUser.isBlank() || password.isBlank()) {
                        loginError = "Please enter both credentials."
                        return@Button
                    }
                    isLoading = true
                    coroutineScope.launch {
                        val success = repository.login(emailOrUser, password)
                        isLoading = false
                        if (success) {
                            onLoginSuccess()
                        } else {
                            loginError = "Invalid email/username or password."
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("login_button"),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryAccent),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = DeepSlateBackground, modifier = Modifier.size(24.dp))
                } else {
                    Text("Sign In", color = DeepSlateBackground, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Social Sign Ins
            Text("Or connect using", color = GraySecondary, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Google
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            repository.currentUser.value = com.example.data.UserAccount(
                                email = "google.user@gmail.com",
                                phone = "+123456789",
                                name = "Google Cinephile",
                                username = "google_cinephile",
                                password = "123",
                                isPremium = true
                            )
                            onLoginSuccess()
                        }
                    },
                    modifier = Modifier
                        .size(52.dp)
                        .background(CardSlateBackground, shape = RoundedCornerShape(12.dp))
                        .testTag("google_login")
                ) {
                    Icon(Icons.Default.AccountCircle, contentDescription = "Google", tint = PrimaryAccent)
                }

                // Facebook
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            repository.currentUser.value = com.example.data.UserAccount(
                                email = "fb.user@facebook.com",
                                phone = "+987654321",
                                name = "Facebook Cinephile",
                                username = "fb_cinephile",
                                password = "123",
                                isPremium = false
                            )
                            onLoginSuccess()
                        }
                    },
                    modifier = Modifier
                        .size(52.dp)
                        .background(CardSlateBackground, shape = RoundedCornerShape(12.dp))
                        .testTag("facebook_login")
                ) {
                    Icon(Icons.Default.Share, contentDescription = "Facebook", tint = PrimaryAccent)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Register redirect
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("New to CineStream?", color = GraySecondary, fontSize = 14.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Sign Up Now",
                    color = PrimaryAccent,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable { onNavigateToSignup() }
                        .testTag("signup_link")
                )
            }
        }
    }
}

// ==========================================
// 3. Signup Screen
// ==========================================
@Composable
fun SignupScreen(
    repository: Repository,
    onSignupSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var referralCode by remember { mutableStateOf("") }
    var acceptTerms by remember { mutableStateOf(false) }

    var signupError by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSlateBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 400.dp)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Join CineStream",
                color = WhitePrimary,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Unleash infinite catalog of premium movies & anime",
                color = GraySecondary,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            if (signupError.isNotEmpty()) {
                Text(signupError, color = RosePremium, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Form Fields
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name", color = GraySecondary) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = WhitePrimary, unfocusedTextColor = WhitePrimary, focusedBorderColor = PrimaryAccent, unfocusedBorderColor = CardSlateBackground)
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username", color = GraySecondary) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = WhitePrimary, unfocusedTextColor = WhitePrimary, focusedBorderColor = PrimaryAccent, unfocusedBorderColor = CardSlateBackground)
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address", color = GraySecondary) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = WhitePrimary, unfocusedTextColor = WhitePrimary, focusedBorderColor = PrimaryAccent, unfocusedBorderColor = CardSlateBackground)
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone Number", color = GraySecondary) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = WhitePrimary, unfocusedTextColor = WhitePrimary, focusedBorderColor = PrimaryAccent, unfocusedBorderColor = CardSlateBackground)
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password", color = GraySecondary) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = WhitePrimary, unfocusedTextColor = WhitePrimary, focusedBorderColor = PrimaryAccent, unfocusedBorderColor = CardSlateBackground)
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password", color = GraySecondary) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = WhitePrimary, unfocusedTextColor = WhitePrimary, focusedBorderColor = PrimaryAccent, unfocusedBorderColor = CardSlateBackground)
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = referralCode,
                onValueChange = { referralCode = it },
                label = { Text("Referral Code (Optional)", color = GraySecondary) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = WhitePrimary, unfocusedTextColor = WhitePrimary, focusedBorderColor = PrimaryAccent, unfocusedBorderColor = CardSlateBackground)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Acceptance Box
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = acceptTerms,
                    onCheckedChange = { acceptTerms = it },
                    colors = CheckboxDefaults.colors(checkedColor = PrimaryAccent)
                )
                Text(
                    text = "I agree to CineStream's Terms and Privacy Policies",
                    color = GraySecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.clickable { acceptTerms = !acceptTerms }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (name.isBlank() || username.isBlank() || email.isBlank() || phone.isBlank() || password.isBlank()) {
                        signupError = "All major fields are required."
                        return@Button
                    }
                    if (password != confirmPassword) {
                        signupError = "Passwords do not match."
                        return@Button
                    }
                    if (!acceptTerms) {
                        signupError = "You must accept terms and conditions."
                        return@Button
                    }
                    isLoading = true
                    coroutineScope.launch {
                        val success = repository.signUp(email, phone, name, username, password, referralCode)
                        isLoading = false
                        if (success) {
                            onSignupSuccess()
                        } else {
                            signupError = "Email or Username already exists!"
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("signup_button"),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryAccent),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = DeepSlateBackground, modifier = Modifier.size(24.dp))
                } else {
                    Text("Register Now", color = DeepSlateBackground, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row {
                Text("Already a cinephile?", color = GraySecondary, fontSize = 14.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Sign In",
                    color = PrimaryAccent,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable { onNavigateToLogin() }
                        .testTag("login_redirect_link")
                )
            }
        }
    }
}

// ==========================================
// 4. Forgot Password Screen
// ==========================================
@Composable
fun ForgotPasswordScreen(
    repository: Repository,
    onResetComplete: () -> Unit,
    onBackToLogin: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var email by remember { mutableStateOf("") }
    var step by remember { mutableIntStateOf(1) } // 1: Email Request, 2: OTP Entry, 3: Reset Password
    var otpCode by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmNewPassword by remember { mutableStateOf("") }
    var feedbackError by remember { mutableStateOf("") }
    var isSending by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSlateBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 400.dp)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "ForgotLock",
                tint = PrimaryAccent,
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (step == 1) "Forgot Password?" else if (step == 2) "OTP Verification" else "Reset Password",
                color = WhitePrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = if (step == 1) {
                    "Enter your CineStream account email. We'll send you a custom verification OTP."
                } else if (step == 2) {
                    "A secret CineStream OTP has been sent. Type it below to proceed."
                } else {
                    "Create a strong password that you do not use on other platforms."
                },
                color = GraySecondary,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
            )

            if (feedbackError.isNotEmpty()) {
                Text(feedbackError, color = RosePremium, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
            }

            when (step) {
                1 -> {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Account Email", color = GraySecondary) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = WhitePrimary, unfocusedTextColor = WhitePrimary, focusedBorderColor = PrimaryAccent, unfocusedBorderColor = CardSlateBackground)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (email.isBlank() || !email.contains("@")) {
                                feedbackError = "Please enter a valid email address."
                                return@Button
                            }
                            isSending = true
                            coroutineScope.launch {
                                delay(1200) // Simulated network
                                isSending = false
                                step = 2
                                feedbackError = ""
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("send_otp_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryAccent)
                    ) {
                        if (isSending) {
                            CircularProgressIndicator(color = DeepSlateBackground, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Send Verification OTP", color = DeepSlateBackground, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                2 -> {
                    OutlinedTextField(
                        value = otpCode,
                        onValueChange = { otpCode = it },
                        label = { Text("6-Digit OTP", color = GraySecondary) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = WhitePrimary, unfocusedTextColor = WhitePrimary, focusedBorderColor = PrimaryAccent, unfocusedBorderColor = CardSlateBackground)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (otpCode.length < 4) {
                                feedbackError = "Please enter the valid OTP."
                                return@Button
                            }
                            isSending = true
                            coroutineScope.launch {
                                delay(800)
                                isSending = false
                                step = 3
                                feedbackError = ""
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("verify_otp_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryAccent)
                    ) {
                        if (isSending) {
                            CircularProgressIndicator(color = DeepSlateBackground, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Verify & Continue", color = DeepSlateBackground, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                3 -> {
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("New Password", color = GraySecondary) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = WhitePrimary, unfocusedTextColor = WhitePrimary, focusedBorderColor = PrimaryAccent, unfocusedBorderColor = CardSlateBackground)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = confirmNewPassword,
                        onValueChange = { confirmNewPassword = it },
                        label = { Text("Confirm New Password", color = GraySecondary) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = WhitePrimary, unfocusedTextColor = WhitePrimary, focusedBorderColor = PrimaryAccent, unfocusedBorderColor = CardSlateBackground)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (newPassword.isBlank()) {
                                feedbackError = "Please enter your new password."
                                return@Button
                            }
                            if (newPassword != confirmNewPassword) {
                                feedbackError = "Passwords do not match."
                                return@Button
                            }
                            isSending = true
                            coroutineScope.launch {
                                val success = repository.resetPassword(email, newPassword)
                                isSending = false
                                if (success) {
                                    onResetComplete()
                                } else {
                                    feedbackError = "No account registered with this email."
                                    step = 1
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("reset_password_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryAccent)
                    ) {
                        if (isSending) {
                            CircularProgressIndicator(color = DeepSlateBackground, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Reset & Sign In", color = DeepSlateBackground, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Back to Login",
                color = PrimaryAccent,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable { onBackToLogin() }
                    .testTag("back_to_login_link")
            )
        }
    }
}

// ==========================================
// 5. Profile Selection Screen ("Who's watching?")
// ==========================================
@Composable
fun ProfileSelectionScreen(
    repository: Repository,
    onProfileSelected: (Profile) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val profilesList by repository.profiles.collectAsState(initial = emptyList())
    var showAddProfileDialog by remember { mutableStateOf(false) }
    var newProfileName by remember { mutableStateOf("") }
    var newProfileType by remember { mutableStateOf("Adult") } // Adult or Kids

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSlateBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = "Who's watching?",
                color = WhitePrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Grid of profiles
            LazyVerticalGrid(
                columns = GridCells.Adaptive(110.dp),
                horizontalArrangement = Arrangement.Center,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 480.dp)
                    .weight(1f, fill = false)
            ) {
                items(profilesList) { profile ->
                    ProfileCard(
                        profile = profile,
                        onSelect = { onProfileSelected(profile) }
                    )
                }

                item {
                    // Add Profile Action Card
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable { showAddProfileDialog = true }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .border(2.dp, CardSlateBackground, RoundedCornerShape(16.dp))
                                .background(CardSlateBackground.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Profile",
                                tint = PrimaryAccent,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Add Profile",
                            color = GraySecondary,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }

    // Add Profile Dialog
    if (showAddProfileDialog) {
        AlertDialog(
            onDismissRequest = { showAddProfileDialog = false },
            title = { Text("Create Profile", color = WhitePrimary, fontWeight = FontWeight.Bold) },
            containerColor = CardSlateBackground,
            text = {
                Column {
                    OutlinedTextField(
                        value = newProfileName,
                        onValueChange = { newProfileName = it },
                        label = { Text("Profile Name", color = GraySecondary) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = WhitePrimary, unfocusedTextColor = WhitePrimary, focusedBorderColor = PrimaryAccent, unfocusedBorderColor = DeepSlateBackground)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Profile Type", color = WhitePrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = newProfileType == "Adult",
                            onClick = { newProfileType = "Adult" },
                            label = { Text("Adult") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryAccent,
                                selectedLabelColor = DeepSlateBackground
                            )
                        )

                        FilterChip(
                            selected = newProfileType == "Kids",
                            onClick = { newProfileType = "Kids" },
                            label = { Text("Kids / Children") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryAccent,
                                selectedLabelColor = DeepSlateBackground
                            )
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newProfileName.isNotBlank()) {
                            coroutineScope.launch {
                                val avatar = if (newProfileType == "Kids") "avatar_kids" else "avatar_adult"
                                repository.addProfile(newProfileName, avatar, newProfileType)
                                showAddProfileDialog = false
                                newProfileName = ""
                            }
                        }
                    }
                ) {
                    Text("Create", color = PrimaryAccent, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddProfileDialog = false }) {
                    Text("Cancel", color = GraySecondary)
                }
            }
        )
    }
}

@Composable
fun ProfileCard(profile: Profile, onSelect: () -> Unit) {
    // Generate color based on profile name or ID for a beautiful visual effect
    val profileColor = when (profile.avatarUrl) {
        "avatar_kids" -> RosePremium
        "avatar_guest" -> GraySecondary
        else -> PrimaryAccent
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(8.dp)
            .clickable { onSelect() }
            .testTag("profile_item_${profile.name}")
    ) {
        Box(
            modifier = Modifier
                .size(90.dp)
                .background(profileColor, shape = RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (profile.type == "Kids") Icons.Default.Face else Icons.Default.Person,
                contentDescription = profile.name,
                tint = DeepSlateBackground,
                modifier = Modifier.size(54.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = profile.name,
            color = WhitePrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
    }
}
