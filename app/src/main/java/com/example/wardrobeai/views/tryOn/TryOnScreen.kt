package com.example.wardrobeai.views.tryOn

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

private val AccentColor = Color(0xFF4F46E5) // Refined Indigo Accent
private val AccentLight = Color(0xFFEEF2FF)
private val LightBg = Color(0xFFF8FAFC)
private val TextMain = Color(0xFF0F172A)
private val TextMuted = Color(0xFF64748B)
private val BorderColor = Color(0xFFE2E8F0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TryOnScreen(
    viewModel: TryOnViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }

    val userBitmap by viewModel.userPhotoBitmap.collectAsStateWithLifecycle()
    val clothingBitmap by viewModel.clothingPhotoBitmap.collectAsStateWithLifecycle()
    val resultBitmap by viewModel.resultBitmap.collectAsStateWithLifecycle()
    val isGenerating by viewModel.isGenerating.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val successMessage by viewModel.successMessage.collectAsStateWithLifecycle()

    // Activity Result Launchers for Image Picker
    val userPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.setUserPhoto(context, it) }
    }

    val clothingPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.setClothingPhoto(context, it) }
    }

    // Handle Error and Success snackbars / toasts
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(message = it, duration = SnackbarDuration.Short)
            viewModel.clearError()
        }
    }

    LaunchedEffect(successMessage) {
        successMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearSuccess()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = LightBg
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // RESULT VIEW (Step 4)
            if (resultBitmap != null) {
                ResultView(
                    resultBitmap = resultBitmap!!,
                    onDownload = { viewModel.saveImageToGallery(context) },
                    onTryAnother = { viewModel.tryAnotherOutfit() }
                )
            } else {
                // INPUT & GENERATE FLOW (Steps 1, 2, 3)
                MainTryOnFlow(
                    userBitmap = userBitmap,
                    clothingBitmap = clothingBitmap,
                    isGenerating = isGenerating,
                    onSelectUserPhoto = { userPickerLauncher.launch("image/*") },
                    onClearUserPhoto = { viewModel.clearUserPhoto() },
                    onSelectClothing = { clothingPickerLauncher.launch("image/*") },
                    onClearClothing = { viewModel.clearClothingPhoto() },
                    onTryOn = { viewModel.generateTryOn() }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "WardrobeAI MVP • Powered by Gemini 3.6 Flash",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Main input and generation view (Steps 1, 2, 3)
 */
@Composable
private fun MainTryOnFlow(
    userBitmap: android.graphics.Bitmap?,
    clothingBitmap: android.graphics.Bitmap?,
    isGenerating: Boolean,
    onSelectUserPhoto: () -> Unit,
    onClearUserPhoto: () -> Unit,
    onSelectClothing: () -> Unit,
    onClearClothing: () -> Unit,
    onTryOn: () -> Unit
) {
    // Header
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(bottom = 28.dp)
    ) {
        Surface(
            color = AccentLight,
            shape = RoundedCornerShape(50),
            modifier = Modifier.padding(bottom = 10.dp)
        ) {
            Text(
                text = "GEMINI 3.6 FLASH AI",
                color = AccentColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
            )
        }

        Text(
            text = "AI VIRTUAL WARDROBE",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.5).sp
            ),
            color = TextMain,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Try clothes on yourself using AI",
            style = MaterialTheme.typography.bodyLarge,
            color = TextMuted,
            textAlign = TextAlign.Center
        )
    }

    // Step 1: Upload Your Photo Card
    UploadCard(
        stepNumber = "Step 1",
        title = "Upload Your Photo",
        hint = "Full or upper body photo works best",
        bitmap = userBitmap,
        emptyIcon = Icons.Default.Person,
        selectButtonText = "+ Select Image",
        onSelect = onSelectUserPhoto,
        onRemove = onClearUserPhoto
    )

    // Downward Arrow Divider
    Box(
        modifier = Modifier.padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = CircleShape,
            color = Color.White,
            border = BorderStroke(1.dp, BorderColor),
            shadowElevation = 2.dp,
            modifier = Modifier.size(40.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.ArrowDownward,
                    contentDescription = "Next Step",
                    tint = AccentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }

    // Step 2: Select Clothing Card
    UploadCard(
        stepNumber = "Step 2",
        title = "Select Clothing",
        hint = "T-shirt, Shirt, Jacket, Hoodie, or Dress",
        bitmap = clothingBitmap,
        emptyIcon = Icons.Default.Checkroom,
        selectButtonText = "+ Select Image",
        onSelect = onSelectClothing,
        onRemove = onClearClothing
    )

    Spacer(modifier = Modifier.height(32.dp))

    // Step 3: Try On Button & Loading State
    if (isGenerating) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, BorderColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    color = AccentColor,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Creating your virtual try-on...",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextMain
                    )
                    Text(
                        text = "Gemini AI is fitting the clothing realistically",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
            }
        }
    } else {
        Button(
            onClick = onTryOn,
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = AccentColor,
                contentColor = Color.White
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(
                text = "TRY ON",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * Reusable Upload Card for User Photo & Clothing Photo
 */
@Composable
private fun UploadCard(
    stepNumber: String,
    title: String,
    hint: String,
    bitmap: android.graphics.Bitmap?,
    emptyIcon: androidx.compose.ui.graphics.vector.ImageVector,
    selectButtonText: String,
    onSelect: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    color = Color(0xFFF1F5F9),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(
                        text = stepNumber,
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextMain
                )
            }

            Text(
                text = hint,
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
                modifier = Modifier.padding(top = 2.dp, bottom = 14.dp)
            )

            if (bitmap != null) {
                // Preview State
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF0F172A))
                ) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )

                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.65f))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TextButton(
                            onClick = onSelect,
                            colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Replace", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }

                        TextButton(
                            onClick = onRemove,
                            colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFFF6B6B))
                        ) {
                            Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Remove", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            } else {
                // Empty State Drop Area
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFF8FAFC),
                    border = BorderStroke(1.5.dp, Color(0xFFCBD5E1)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clickable { onSelect() }
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White,
                            shadowElevation = 2.dp,
                            modifier = Modifier.size(56.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = emptyIcon,
                                    contentDescription = null,
                                    tint = AccentColor,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = selectButtonText,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentColor
                        )

                        Text(
                            text = "JPG, PNG, WEBP",
                            fontSize = 12.sp,
                            color = TextMuted,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Result View (Step 4)
 */
@Composable
private fun ResultView(
    resultBitmap: android.graphics.Bitmap,
    onDownload: () -> Unit,
    onTryAnother: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Surface(
            color = Color(0xFFECFDF5),
            shape = RoundedCornerShape(50),
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Text(
                text = "TRY-ON COMPLETE",
                color = Color(0xFF059669),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
            )
        }

        Text(
            text = "YOUR VIRTUAL TRY-ON",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.5).sp
            ),
            color = TextMain,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Here is how the outfit looks on you",
            style = MaterialTheme.typography.bodyLarge,
            color = TextMuted,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Large Result Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, BorderColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 360.dp, max = 520.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF0F172A)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    bitmap = resultBitmap.asImageBitmap(),
                    contentDescription = "Virtual try-on result",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Actions: Download & Try Another Outfit
        Button(
            onClick = onDownload,
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = AccentColor,
                contentColor = Color.White
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
        ) {
            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Download Image",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedButton(
            onClick = onTryAnother,
            shape = RoundedCornerShape(50),
            border = BorderStroke(1.5.dp, BorderColor),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextMain),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Try Another Outfit",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}