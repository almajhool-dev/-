package com.example.ui.screens

import android.content.ClipboardManager
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AccountAuditResult
import com.example.ui.components.AccountHeaderCard
import com.example.ui.components.CyberBackground
import com.example.ui.components.CyberSecurityGauge
import com.example.ui.components.DiscoveredIssuesSection
import com.example.ui.components.FactorsBreakdownSection
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.SecurityDisclaimerCard
import com.example.ui.components.StatsGrid
import com.example.ui.components.StrengthsSection
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.SecurityGreen
import com.example.ui.theme.SecurityOrange
import com.example.ui.theme.SecurityRed
import com.example.ui.theme.SecurityYellow
import com.example.ui.viewmodel.AuditUiState
import com.example.ui.viewmodel.MainViewModel

enum class MainTab {
    AUDIT,
    HISTORY,
    ABOUT
}

@Composable
fun MainScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var currentTab by remember { mutableStateOf(MainTab.AUDIT) }
    val uiState by viewModel.uiState.collectAsState()
    val inputQuery by viewModel.inputQuery.collectAsState()
    val historyList by viewModel.historyList.collectAsState()

    // Handle back button when on sub-screens
    BackHandler(enabled = currentTab != MainTab.AUDIT) {
        currentTab = MainTab.AUDIT
    }

    Box(modifier = modifier.fillMaxSize().background(CyberBackground)) {
        // High-tech Cyber Canvas in Background
        CyberBackground()

        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                NavigationBar(
                    containerColor = CyberSurface.copy(alpha = 0.95f),
                    contentColor = Color.White,
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = NeonCyan.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                        )
                ) {
                    NavigationBarItem(
                        selected = currentTab == MainTab.AUDIT,
                        onClick = { currentTab = MainTab.AUDIT },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = "فحص الحساب",
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = { Text("فحص الحساب", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NeonCyan,
                            selectedTextColor = NeonCyan,
                            indicatorColor = NeonCyan.copy(alpha = 0.18f),
                            unselectedIconColor = Color(0xFF64748B),
                            unselectedTextColor = Color(0xFF64748B)
                        ),
                        modifier = Modifier.testTag("tab_audit")
                    )

                    NavigationBarItem(
                        selected = currentTab == MainTab.HISTORY,
                        onClick = { currentTab = MainTab.HISTORY },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "السجل",
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = { Text("السجل (${historyList.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NeonCyan,
                            selectedTextColor = NeonCyan,
                            indicatorColor = NeonCyan.copy(alpha = 0.18f),
                            unselectedIconColor = Color(0xFF64748B),
                            unselectedTextColor = Color(0xFF64748B)
                        ),
                        modifier = Modifier.testTag("tab_history")
                    )

                    NavigationBarItem(
                        selected = currentTab == MainTab.ABOUT,
                        onClick = { currentTab = MainTab.ABOUT },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "حول التطبيق",
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = { Text("حول التطبيق", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NeonCyan,
                            selectedTextColor = NeonCyan,
                            indicatorColor = NeonCyan.copy(alpha = 0.18f),
                            unselectedIconColor = Color(0xFF64748B),
                            unselectedTextColor = Color(0xFF64748B)
                        ),
                        modifier = Modifier.testTag("tab_about")
                    )
                }
            }
        ) { innerPadding ->
            when (currentTab) {
                MainTab.AUDIT -> {
                    AuditView(
                        viewModel = viewModel,
                        uiState = uiState,
                        inputQuery = inputQuery,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
                MainTab.HISTORY -> {
                    HistoryScreen(
                        historyList = historyList,
                        onSelectAccount = { user ->
                            viewModel.analyzeAccount(user)
                            currentTab = MainTab.AUDIT
                        },
                        onDeleteAccount = { id -> viewModel.deleteHistoryItem(id) },
                        onClearAll = { viewModel.clearAllHistory() },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
                MainTab.ABOUT -> {
                    AboutScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun AuditView(
    viewModel: MainViewModel,
    uiState: AuditUiState,
    inputQuery: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val sampleUsers = listOf("tiktok", "khaby.lame", "cristiano", "nasa", "mrbeast")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("audit_view_column"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Top Bar Branding
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0F172A))
                        .border(1.5.dp, NeonCyan.copy(alpha = 0.5f), CircleShape)
                        .padding(3.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.tk_sabr_logo_1792462968602),
                        contentDescription = "TK_SABR Logo",
                        modifier = Modifier.fillMaxSize().clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "TK_SABR",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            letterSpacing = 1.sp
                        )
                    )
                    Text(
                        text = "فحص وتدقيق حسابات تيك توك السيبراني",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = NeonCyan,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }

        // Search Input Terminal Card
        item {
            GlassmorphicCard(
                borderColor = NeonCyan.copy(alpha = 0.45f),
                modifier = Modifier.testTag("search_card")
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "أدخل اسم المستخدم (Username) أو رابط الحساب:",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    )

                    OutlinedTextField(
                        value = inputQuery,
                        onValueChange = { viewModel.onQueryChanged(it) },
                        placeholder = {
                            Text(
                                text = "مثال: @username أو الرابط...",
                                color = Color(0xFF64748B),
                                fontSize = 13.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "بحث",
                                tint = NeonCyan
                            )
                        },
                        trailingIcon = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (inputQuery.isNotEmpty()) {
                                    IconButton(
                                        onClick = { viewModel.onQueryChanged("") },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "مسح",
                                            tint = Color(0xFF94A3B8),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clipText = clipboard.primaryClip?.getItemAt(0)?.text?.toString() ?: ""
                                        if (clipText.isNotBlank()) {
                                            viewModel.onQueryChanged(clipText)
                                        }
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentPaste,
                                        contentDescription = "لصق من الحافظة",
                                        tint = NeonBlue,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                keyboardController?.hide()
                                viewModel.analyzeAccount()
                            }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("username_input_field"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF09111D),
                            unfocusedContainerColor = Color(0xFF09111D),
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = Color(0xFF1E293B),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    // Big Glowing "فحص الحساب" Action Button
                    Button(
                        onClick = {
                            keyboardController?.hide()
                            viewModel.analyzeAccount()
                        },
                        enabled = uiState !is AuditUiState.Loading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .shadow(8.dp, RoundedCornerShape(12.dp), spotColor = NeonCyan)
                            .testTag("btn_analyze_account"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonCyan,
                            contentColor = Color(0xFF031622),
                            disabledContainerColor = Color(0xFF1E293B),
                            disabledContentColor = Color(0xFF64748B)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "أيقونة الفحص",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "فحص الحساب",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp
                            )
                        )
                    }

                    // Suggested profiles for instant check
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "حسابات مقترحة للفحص السريع:",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(sampleUsers) { sample ->
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFF0F1A2A), RoundedCornerShape(20.dp))
                                        .border(1.dp, NeonBlue.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                                        .clickable {
                                            keyboardController?.hide()
                                            viewModel.analyzeAccount(sample)
                                        }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "@$sample",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = NeonCyan,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Dynamic State Content
        when (val state = uiState) {
            is AuditUiState.Idle -> {
                item {
                    AuditIdleCard()
                }
            }

            is AuditUiState.Loading -> {
                item {
                    AuditLoadingRadar(username = state.username)
                }
            }

            is AuditUiState.Error -> {
                item {
                    AuditErrorCard(
                        errorMessage = state.message,
                        onRetry = { viewModel.analyzeAccount(state.lastQuery) }
                    )
                }
            }

            is AuditUiState.Success -> {
                val result = state.result
                val account = result.account

                // 1. Account Header Card (Avatar, Verified, Name, Username, Open in TikTok)
                item {
                    AccountHeaderCard(account = account)
                }

                // 2. Stats Grid (Followers, Following, Likes, Videos)
                item {
                    StatsGrid(
                        followers = account.followerCount,
                        following = account.followingCount,
                        likes = account.likesCount,
                        videos = account.videoCount
                    )
                }

                // 3. Additional Calculated Metrics Card (Engagement, Avg likes per video, Profile completeness)
                item {
                    CalculatedMetricsCard(result = result)
                }

                // 4. Large Circular Security Gauge (نسبة أمان الحساب من 0 إلى 100%)
                item {
                    GlassmorphicCard(
                        borderColor = when (result.safetyScore) {
                            in 90..100 -> SecurityGreen
                            in 70..89 -> SecurityYellow
                            in 40..69 -> SecurityOrange
                            else -> SecurityRed
                        }.copy(alpha = 0.35f)
                    ) {
                        CyberSecurityGauge(
                            score = result.safetyScore,
                            ratingText = result.ratingLevel
                        )
                    }
                }

                // 5. Discovered Issues Section (المشاكل المكتشفة: Problem, Cause, Solution)
                item {
                    DiscoveredIssuesSection(issues = result.issues)
                }

                // 6. Strengths Section (نقاط القوة)
                item {
                    StrengthsSection(strengths = result.strengths)
                }

                // 7. Factors Points Breakdown
                item {
                    FactorsBreakdownSection(factors = result.factors)
                }

                // 8. Mandatory Security Disclaimer
                item {
                    SecurityDisclaimerCard()
                }

                // Reset / Scan another account button
                item {
                    OutlinedButton(
                        onClick = { viewModel.resetToIdle() },
                        modifier = Modifier.fillMaxWidth().height(46.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF94A3B8)
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "فحص حساب آخر",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("فحص حساب جديد", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun AuditIdleCard() {
    GlassmorphicCard(borderColor = NeonBlue.copy(alpha = 0.25f)) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Speed,
                contentDescription = null,
                tint = NeonCyan,
                modifier = Modifier.size(48.dp)
            )

            Text(
                text = "جاهز لبدء الفحص والتدقيق الرقمي",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 16.sp
                ),
                textAlign = TextAlign.Center
            )

            Text(
                text = "اكتب اسم أي حساب تيك توك في الحقل أعلاه واضغط (فحص الحساب) لاستخراج البيانات الحقيقية وحساب نسبة الأمان وصحة المؤشرات التفاعلية بدون تسجيل دخول.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF94A3B8),
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                ),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
fun AuditLoadingRadar(username: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "radar_anim")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radar_sweep"
    )

    GlassmorphicCard(
        borderColor = NeonCyan.copy(alpha = 0.5f),
        modifier = Modifier.testTag("loading_radar_card")
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier.size(120.dp),
                contentAlignment = Alignment.Center
            ) {
                // Radar Rings
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = this.center
                    val maxR = size.minDimension / 2

                    drawCircle(
                        color = NeonCyan.copy(alpha = 0.15f),
                        radius = maxR,
                        style = Stroke(1.5f)
                    )
                    drawCircle(
                        color = NeonCyan.copy(alpha = 0.25f),
                        radius = maxR * 0.65f,
                        style = Stroke(1f)
                    )
                    drawCircle(
                        color = NeonCyan.copy(alpha = 0.35f),
                        radius = maxR * 0.3f,
                        style = Stroke(1f)
                    )
                }

                // Rotating Scanner Sweep
                Canvas(
                    modifier = Modifier.fillMaxSize().rotate(rotation)
                ) {
                    drawArc(
                        brush = Brush.sweepGradient(
                            0.0f to Color.Transparent,
                            0.75f to NeonCyan.copy(alpha = 0.1f),
                            1.0f to NeonCyan.copy(alpha = 0.6f)
                        ),
                        startAngle = 0f,
                        sweepAngle = 90f,
                        useCenter = true
                    )
                }

                CircularProgressIndicator(
                    color = NeonCyan,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(40.dp)
                )
            }

            Text(
                text = "جاري تحليل الحساب",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    fontSize = 18.sp
                )
            )

            Text(
                text = "استخراج المؤشرات العامة لحساب @$username والتدقيق في نمط التفاعل...",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = NeonCyan,
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp
                ),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
fun AuditErrorCard(
    errorMessage: String,
    onRetry: () -> Unit
) {
    GlassmorphicCard(
        borderColor = SecurityRed.copy(alpha = 0.5f),
        modifier = Modifier.testTag("error_card")
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = "خطأ",
                tint = SecurityRed,
                modifier = Modifier.size(48.dp)
            )

            Text(
                text = "تعذر إتمام الفحص",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 16.sp
                )
            )

            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFFCBD5E1),
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                ),
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Button(
                onClick = onRetry,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SecurityRed,
                    contentColor = Color.White
                ),
                modifier = Modifier.testTag("btn_retry")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("إعادة المحاولة", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun CalculatedMetricsCard(
    result: AccountAuditResult
) {
    GlassmorphicCard(borderColor = NeonBlue.copy(alpha = 0.25f)) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "بيانات التفاعل والحساب المحسوبة",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 14.sp
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "اكتمال الملف الشخصي:",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF94A3B8))
                )
                Text(
                    text = "${result.profileCompletenessPercent}%",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan
                    )
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "متوسط الإعجابات لكل مقطع:",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF94A3B8))
                )
                Text(
                    text = "${result.avgLikesPerVideo}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "نسبة التفاعل الإجمالية:",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF94A3B8))
                )
                Text(
                    text = String.format(java.util.Locale.US, "%.2f%%", result.engagementRatePercent),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = SecurityGreen
                    )
                )
            }
        }
    }
}
