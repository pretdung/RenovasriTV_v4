package com.example.renovasriv4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.*
import coil.compose.AsyncImage
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.tv.material3.Surface
import androidx.tv.material3.TabRow

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalTvMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HomeLivingTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(0.dp)
                ) {
                    MainScreen()
                }
            }
        }
    }
}

data class NavigationItem(val title: String, val icon: String? = null)
data class InspirationCard(val title: String, val category: String, val imageUrl: String)

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun MainScreen() {
    val navItems = listOf(
        NavigationItem("GALERI"),
        NavigationItem("KALKULATOR"),
        NavigationItem("DESAINER"),
        NavigationItem("RIWAYAT"),
        NavigationItem("KONSULTASI")
    )

    val inspirations = listOf(
        InspirationCard("Marmer & Perunggu", "DAPUR", "https://lh3.googleusercontent.com/aida-public/AB6AXuBVlYceLNUM6mb6neD8NkuyF2TOUZD2drlxYFXtGxApH1grqkmYSP_7KCavVJdJZyh61-OwluwQLxBXs5TMKM7l2qnDpCip6DMZmTVNufZzVvgQFxXwcf2HOPKYC3GoC86G6JtoRmgsvmwUkkA-80YfMeV6BU81MZo-LEJj6iXknkLe9JaHJq6VYvQRB-q6Mlq8MCTJLgocV6AfmbvHd4vtA0Mgv_rUGTNaL0oW63S8yYsjiIu-iXW_V1f4tESSupRJrOs1SSo9okTR"),
        InspirationCard("Tangga Geometris", "STRUKTUR", "https://lh3.googleusercontent.com/aida-public/AB6AXuAwTiH2mESI-D4SQ0MGqjdL7z2wdAZvh048FMm0eV-QgC-P7zRHIUrpCVP7-HKV3WxlIyLLWPbg8GdVnmH6UmePH4LcDl5-i8IMnB5pnpB0dPahy8LyIoweWqPwARDesESglYI7JNLHTL4olhrKf93dwtyN52StivFfd87s-ETIVOwNnb-kDOz99uCcuoxGc3u2Ajwcb20Sgf0OuOPqKaRPzs8WyOLOWR9XX1vsxxgqKC3WhnCHUZJ07rcxcu-8q1CZBNd0ZUcYmaKe"),
        InspirationCard("Ketenangan Abu-abu", "PRIVAT", "https://lh3.googleusercontent.com/aida-public/AB6AXuB-0bwDW2zRBvac_ead5sWCn2DEOu5JcyEs5OpEs1dezAVGXgiqM6sTU6XIZyerVzhWgkLKPHh1OrVyjEAqozBxWr0YAZBzV7sSL7pKQfwD9Biu7S3OtGoincx2n_dR7Nudz8ezUeRx1DYbrJIZ5kY-a0fyP4hPKn8gc1c9K5B2rA6w36SBmVrD7MXAf4BwEyR2xzsuBLq0DUkxw9fc078UcsrXyVPt56p8sEIfDpMHGFZir97zGYEenDH2Q12K2k43fyHQtuQRdPiC")
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Background Image
        AsyncImage(
            model = "https://lh3.googleusercontent.com/aida-public/AB6AXuDeFPuuEFT-D8DhxEUKLEIWO8fcl8d82Qr4T4bz9jY_DPwkP-oTNRtLxPRhB78kqzXtPGCrnviNNsPQoVWKoQpyyzwSHnV5HfZgpvORJlFPCMgHcWArFpOAYLQz7tqsrbJQ7uKk9B2BkhNpgwFNcr9Hmc1Z6dvoEKmPCVUBo0QiRfaQCYwFhJ7EgokRk6oGzcPWIua9AORsQJRPZE_rjlC02C9gp9Eo5tBtbaD9K0zSCzTHio1d1Q_L6OMOgbtpdxZCbvRU9E8bIxkx",
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Gradient Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                        startY = 300f
                    )
                )
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(Color.Black.copy(alpha = 0.6f), Color.Transparent),
                        endX = 1000f
                    )
                )
        )

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 58.dp)
        ) {
            TopNavigationBar(navItems)

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 60.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                // Left Content
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .width(40.dp)
                                .height(2.dp)
                                .background(MaterialTheme.colorScheme.tertiary)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "EDISI TERBATAS: LOFT INDUSTRIAL",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.tertiary,
                            letterSpacing = 2.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "RUANG",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 80.sp,
                            fontWeight = FontWeight.Black,
                            lineHeight = 70.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "ABSTRAK.",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 80.sp,
                            fontWeight = FontWeight.Black,
                            lineHeight = 70.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Menjelajahi pertemuan antara beton kasar dan kehangatan kayu oak. Kurasi desain pilihan untuk gaya hidup kontemporer yang mengutamakan tekstur dan kejujuran material.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.width(400.dp)
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Button(
                            onClick = { },
                            colors = ButtonDefaults.colors(
                                containerColor = MaterialTheme.colorScheme.tertiary,
                                contentColor = Color(0xFF472A06)
                            ),
                            shape = ButtonDefaults.shape(RoundedCornerShape(4.dp))
                        ) {
                            Text("LIHAT DETAIL", fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = { },
                            colors = ButtonDefaults.colors(
                                containerColor = Color.White.copy(alpha = 0.1f),
                                contentColor = MaterialTheme.colorScheme.onSurface
                            ),
                            shape = ButtonDefaults.shape(RoundedCornerShape(4.dp))
                        ) {
                            Text("SIMPAN INSPIRASI", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Right Content: Inspiration Cards
                Column(modifier = Modifier.weight(1.2f)) {
                    Text(
                        text = "INSPIRASI TERKAIT",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 8.dp, bottom = 16.dp)
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        items(inspirations) { inspiration ->
                            InspirationCardItem(inspiration)
                        }
                    }
                }
            }
            
            Footer()
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TopNavigationBar(items: List<NavigationItem>) {
    var selectedIndex by remember { mutableIntStateOf(0) }
    var isLogoFocused by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                onClick = { selectedIndex = -1 },
                modifier = Modifier
                    .onFocusChanged { 
                        isLogoFocused = it.isFocused
                        if (it.isFocused) selectedIndex = -1
                    },
                scale = ClickableSurfaceDefaults.scale(focusedScale = 1.1f),
                colors = ClickableSurfaceDefaults.colors(
                    containerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    pressedContainerColor = Color.Transparent
                )
            ) {
                Text(
                    text = "RENOVASRI",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-1).sp
                    ),
                    color = if (isLogoFocused) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            Spacer(modifier = Modifier.width(40.dp))
            TabRow(
                selectedTabIndex = if (selectedIndex == -1) 0 else selectedIndex,
                separator = { Spacer(modifier = Modifier.width(24.dp)) },
                indicator = { tabPositions, doesTabRowHaveFocus ->
                    if (selectedIndex >= 0 && selectedIndex < tabPositions.size) {
                        val currentTabPosition = tabPositions[selectedIndex]
                        val width = currentTabPosition.right - currentTabPosition.left
                        val animWidth by animateDpAsState(targetValue = width, label = "width")
                        val animLeft by animateDpAsState(targetValue = currentTabPosition.left, label = "offset")

                        Box(
                            Modifier
                                .fillMaxWidth()
                                .wrapContentSize(Alignment.BottomStart)
                                .offset { IntOffset(x = animLeft.roundToPx(), y = 48.dp.roundToPx()) }
                                .width(animWidth)
                                .height(6.dp)
                                .background(
                                    color = if (doesTabRowHaveFocus) MaterialTheme.colorScheme.tertiary else Color.Transparent,
                                    shape = RoundedCornerShape(3.dp)
                                )
                        )
                    }
                }
            ) {
                items.forEachIndexed { index, item ->
                    val isSelected = selectedIndex == index
                    Tab(
                        selected = isSelected,
                        onFocus = { 
                            selectedIndex = index
                        },
                        onClick = { selectedIndex = index },
                        colors = TabDefaults.underlinedIndicatorTabColors(
                            selectedContentColor = MaterialTheme.colorScheme.tertiary,
                            focusedContentColor = MaterialTheme.colorScheme.tertiary,
                            contentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(if (isSelected) Color.White.copy(alpha = 0.1f) else Color.Transparent)
                                .padding(horizontal = 18.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            )
                        }
                    }
                }
            }
        }
        
        IconButton(onClick = { }) {
            Icon(
                painter = painterResource(id = android.R.drawable.ic_menu_my_calendar), // Replace with person icon
                contentDescription = "Profile",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun InspirationCardItem(inspiration: InspirationCard) {
    Card(
        onClick = { },
        modifier = Modifier
            .width(200.dp)
            .height(300.dp),
        shape = CardDefaults.shape(RoundedCornerShape(8.dp)),
        scale = CardDefaults.scale(focusedScale = 1.05f),
        border = CardDefaults.border(
            focusedBorder = Border(border = BorderStroke(2.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f)))
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = inspiration.imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                            startY = 400f
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = inspiration.category,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = inspiration.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun Footer() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.tertiary)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "SEKARANG: KURASI MUSIM DINGIN",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp
            )
        }
        
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(Color.White.copy(alpha = 0.05f))
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "1.2k Views", style = MaterialTheme.typography.labelSmall, color = Color.White)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Bagikan", style = MaterialTheme.typography.labelSmall, color = Color.White)
            }
        }
    }
}
