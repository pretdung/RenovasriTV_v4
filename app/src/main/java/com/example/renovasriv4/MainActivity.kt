package com.example.renovasriv4

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.*
import coil.compose.AsyncImage
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.Storage
import kotlinx.serialization.Serializable

class MainActivity : ComponentActivity() {
    private val supabase = createSupabaseClient(
        supabaseUrl = "https://vdzqkyrtnpbpfasqorgh.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InZkenFreXJ0bnBicGZhc3FvcmdoIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzY0NDIxMTYsImV4cCI6MjA5MjAxODExNn0.itTsS1LqrX4xGIRxqwPZJ3id8SvfwWbUnj4dtuKM8Lw"
    ) {
        install(Postgrest)
        install(Storage)
    }

    @OptIn(ExperimentalTvMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HomeLivingTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(0.dp)
                ) {
                    MainScreen(supabase)
                }
            }
        }
    }
}

@Serializable
data class MenuContent(
    val menu_item_id: Long? = null,
    val subtitle: String? = null,
    val headline_primary: String? = null,
    val headline_secondary: String? = null,
    val description: String? = null,
    val show_in_navbar: Boolean? = true
)

@Serializable
data class NavigationItem(
    val id: Long? = null,
    val title: String,
    val slug: String,
    val image_url: String? = null,
    val order_index: Int = 0,
    val is_active: Boolean = true,
    var content: MenuContent? = null
)

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun MainScreen(supabase: SupabaseClient) {
    var navItems by remember { mutableStateOf<List<NavigationItem>>(emptyList()) }
    var selectedSlug by remember { mutableStateOf("home") }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        try {
            // Fetch items and content separately to ensure data is retrieved regardless of join issues
            val items = supabase.from("menu_items").select {
                filter { eq("is_active", true) }
                order("order_index", Order.ASCENDING)
            }.decodeList<NavigationItem>()

            val contents = try {
                supabase.from("menu_content_general").select().decodeList<MenuContent>()
            } catch (e: Exception) {
                Log.e("SUPABASE_ERROR", "Content fetch failed: ${e.message}")
                emptyList()
            }

            // Manually link content to items
            navItems = items.map { item ->
                item.copy(content = contents.find { it.menu_item_id == item.id })
            }
            
            isLoading = false
            Log.d("SUPABASE_DATA", "Mapped ${navItems.size} items. Home has content: ${navItems.find { it.slug == "home" }?.content != null}")
        } catch (e: Exception) {
            Log.e("SUPABASE_ERROR", "Main fetch failed: ${e.message}")
            isLoading = false
        }
    }

    val currentItem = remember(selectedSlug, navItems) {
        navItems.find { it.slug == selectedSlug } ?: navItems.find { it.slug == "home" }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0A0A0A))) {
        // Background Image
        Crossfade(targetState = currentItem?.image_url, label = "background") { url ->
            if (!url.isNullOrBlank()) {
                AsyncImage(
                    model = url,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    onSuccess = { Log.d("IMAGE_LOAD", "Success: $url") },
                    onError = { Log.e("IMAGE_LOAD", "Error: $url") }
                )
            }
        }

        // Gradients for depth and readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Black.copy(alpha = 0.5f), Color.Transparent, Color.Black.copy(alpha = 0.9f))
                    )
                )
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(Color.Black.copy(alpha = 0.7f), Color.Transparent),
                        endX = 1200f
                    )
                )
        )

        // Main UI
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 58.dp)
        ) {
            TopNavigationBar(
                items = navItems,
                selectedSlug = selectedSlug,
                onItemSelected = { selectedSlug = it }
            )

            if (isLoading) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("MEMUAT...", color = Color.White)
                }
            } else {
                Spacer(modifier = Modifier.weight(1f))

                // Content Section
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 40.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = currentItem?.content?.subtitle?.uppercase() ?: "",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFFD4AF37), // Gold accent
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 3.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = currentItem?.content?.headline_primary ?: currentItem?.title ?: "",
                            style = MaterialTheme.typography.displayLarge.copy(fontSize = 72.sp, fontWeight = FontWeight.Black),
                            color = Color.White
                        )
                        if (currentItem?.content?.headline_secondary != null) {
                            Text(
                                text = currentItem.content?.headline_secondary ?: "",
                                style = MaterialTheme.typography.displayLarge.copy(fontSize = 72.sp, fontWeight = FontWeight.Black),
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = currentItem?.content?.description ?: "",
                            style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 28.sp),
                            color = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.width(550.dp)
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Button(onClick = { }) { Text("EKSPLORASI SEKARANG") }
                            Button(
                                onClick = { }, 
                                colors = ButtonDefaults.colors(containerColor = Color.White.copy(alpha = 0.1f))
                            ) {
                                Text("SIMPAN KE WISHLIST")
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TopNavigationBar(
    items: List<NavigationItem>,
    selectedSlug: String,
    onItemSelected: (String) -> Unit
) {
    val allItems = remember(items) {
        val homeItem = items.find { it.slug == "home" }
        val others = items.filter { it.slug != "home" && it.content?.show_in_navbar != false }
        if (homeItem != null) listOf(homeItem) + others else others
    }
    
    val selectedIndex = remember(selectedSlug, allItems) { 
        allItems.indexOfFirst { it.slug == selectedSlug } 
    }

    val homeFocusRequester = remember { FocusRequester() }
    val profileFocusRequester = remember { FocusRequester() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TabRow(
            selectedTabIndex = selectedIndex,
            containerColor = Color.Transparent,
            indicator = { tabPositions, _ ->
                if (selectedIndex >= 0 && selectedIndex < tabPositions.size) {
                    val currentTabPosition = tabPositions[selectedIndex]
                    val animWidth by animateDpAsState(targetValue = (currentTabPosition.right - currentTabPosition.left) * 0.8f, label = "width")
                    val animLeft by animateDpAsState(targetValue = currentTabPosition.left + (currentTabPosition.right - currentTabPosition.left) * 0.1f, label = "offset")
                    
                    Box(
                        Modifier
                            .fillMaxSize()
                            .wrapContentSize(Alignment.BottomStart)
                            .offset { IntOffset(x = animLeft.roundToPx(), y = 0) }
                            .width(animWidth)
                            .height(6.dp)
                            .background(color = Color(0xFFD4AF37), shape = RoundedCornerShape(3.dp))
                    )
                }
            }
        ) {
            allItems.forEach { item ->
                val isHome = item.slug == "home"
                Tab(
                    selected = selectedSlug == item.slug,
                    onFocus = { onItemSelected(item.slug) },
                    onClick = { onItemSelected(item.slug) },
                    modifier = if (isHome) {
                        Modifier
                            .focusRequester(homeFocusRequester)
                            .focusProperties { left = profileFocusRequester }
                    } else Modifier
                ) {
                    // Use a Box to align content to the bottom of the tab height
                    Box(
                        modifier = Modifier
                            .height(60.dp)
                            .padding(start = 16.dp, end = 16.dp, bottom = 4.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        if (isHome) {
                            Text(
                                text = "RENOVASRI",
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                                color = if (selectedSlug == item.slug) Color(0xFFD4AF37) else Color.White,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                        } else {
                            Text(
                                text = item.title.uppercase(),
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = if (selectedSlug == item.slug) Color.White else Color.White.copy(alpha = 0.6f),
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // Right-aligned Profile Menu
        var isProfileFocused by remember { mutableStateOf(false) }
        Surface(
            onClick = { onItemSelected("profile") },
            colors = ClickableSurfaceDefaults.colors(
                containerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                pressedContainerColor = Color.Transparent
            ),
            scale = ClickableSurfaceDefaults.scale(focusedScale = 1.1f),
            modifier = Modifier
                .focusRequester(profileFocusRequester)
                .focusProperties { right = homeFocusRequester }
                .onFocusChanged { 
                    isProfileFocused = it.isFocused
                    if (it.isFocused) onItemSelected("profile")
                }
        ) {
            Box(
                modifier = Modifier
                    .height(60.dp)
                    .padding(start = 16.dp, end = 16.dp, bottom = 4.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                AsyncImage(
                    model = "https://i.pravatar.cc/150?u=renovasri", // Placeholder profile image
                    contentDescription = "Profile",
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .border(
                            width = 2.dp,
                            color = if (isProfileFocused || selectedSlug == "profile") Color(0xFFD4AF37) else Color.Transparent,
                            shape = CircleShape
                        ),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}
