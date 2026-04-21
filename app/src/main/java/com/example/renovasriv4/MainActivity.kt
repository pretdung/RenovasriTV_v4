package com.example.renovasriv4

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.*
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.Storage
import kotlinx.serialization.SerialName
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
    @SerialName("menu_item_id") val menuItemId: Long? = null,
    val subtitle: String? = null,
    @SerialName("headline_primary") val headlinePrimary: String? = null,
    @SerialName("headline_secondary") val headlineSecondary: String? = null,
    val description: String? = null,
    @SerialName("show_in_navbar") val showInNavbar: Boolean? = true
)

@Serializable
data class NavigationItem(
    val id: Long? = null,
    val title: String,
    val slug: String,
    @SerialName("destination_page") val destinationPage: String? = null,
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("order_index") val orderIndex: Int = 0,
    @SerialName("is_active") val isActive: Boolean = true,
    var content: MenuContent? = null
)

@Serializable
data class GalleryItem(
    val id: Long? = null,
    val title: String = "",
    val subtitle: String? = null,
    val description: String? = null,
    @SerialName("image_url") val imageUrl: String = "",
    val category: String = "Semua",
    @SerialName("order_index") val orderIndex: Int = 0
)

@Serializable
data class GalleryDetail(
    val id: Long? = null,
    val category: String? = null,
    val title: String? = null,
    val description: String? = null,
    val name: String? = null, // from r.name
    val philosophy: String? = null,
    @SerialName("signature_materials") val signatureMaterials: String? = null,
    val vibe: String? = null,
    @SerialName("color_1") val color1: String? = null,
    @SerialName("color_2") val color2: String? = null,
    @SerialName("color_3") val color3: String? = null,
    @SerialName("color_4") val color4: String? = null,
    @SerialName("budget_avg") val budgetAvg: Double? = null // Using budget_avg directly
)

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun MainScreen(supabase: SupabaseClient) {
    var navItems by remember { mutableStateOf<List<NavigationItem>>(emptyList()) }
    var selectedSlug by remember { mutableStateOf("home") }
    var isLoading by remember { mutableStateOf(true) }
    var focusedGalleryImageUrl by remember { mutableStateOf<String?>(null) }
    var selectedGalleryItem by remember { mutableStateOf<GalleryItem?>(null) }

    LaunchedEffect(Unit) {
        try {
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

            navItems = items.map { item ->
                item.copy(content = contents.find { it.menuItemId == item.id })
            }
            isLoading = false
        } catch (e: Exception) {
            Log.e("SUPABASE_ERROR", "Main fetch failed: ${e.message}")
            isLoading = false
        }
    }

    val currentItem = remember(selectedSlug, navItems) {
        navItems.find { it.slug == selectedSlug } ?: navItems.find { it.slug == "home" }
    }

    val backgroundImageUrl = remember(selectedSlug, focusedGalleryImageUrl, currentItem) {
        if (selectedSlug == "galeri" || selectedSlug == "gallery") {
            focusedGalleryImageUrl ?: currentItem?.imageUrl
        } else {
            currentItem?.imageUrl
        } ?: "https://vdzqkyrtnpbpfasqorgh.supabase.co/storage/v1/object/public/image_pages/living_room.png"
    }

    val isGallery = remember(selectedSlug, currentItem) {
        selectedSlug.lowercase().contains("galeri") || 
        selectedSlug.lowercase().contains("gallery") ||
        currentItem?.destinationPage?.contains("gallery", ignoreCase = true) == true
    }

    Log.d("APP_DEBUG", "MainScreen state: selectedSlug=$selectedSlug, isGallery=$isGallery, navItems=${navItems.size}")

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF222222))) {
        Crossfade(targetState = backgroundImageUrl, label = "background", animationSpec = tween(1000)) { url ->
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(url)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                error = ColorPainter(Color(0xFF1A1A1A)),
                placeholder = ColorPainter(Color.Black)
            )
        }

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

        if (selectedGalleryItem == null) {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.padding(horizontal = 28.dp)) {
                    TopNavigationBar(
                        items = navItems,
                        selectedSlug = selectedSlug,
                        onItemSelected = {
                            selectedSlug = it
                            focusedGalleryImageUrl = null
                        }
                    )
                }

                if (isLoading) {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("MEMUAT...", color = Color.White)
                    }
                } else if (navItems.isEmpty()) {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("GAGAL MEMUAT DATA", color = Color.White)
                    }
                } else {
                    if (isGallery) {
                        Box(modifier = Modifier.weight(1f)) {
                            GalleryDashboard(
                                supabase = supabase,
                                onFocusItem = { focusedImageUrl ->
                                    focusedGalleryImageUrl = focusedImageUrl
                                },
                                onSelectItem = { item ->
                                    selectedGalleryItem = item
                                }
                            )
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(horizontal = 58.dp)
                                .padding(bottom = 60.dp),
                            verticalArrangement = Arrangement.Bottom
                        ) {
                            Text(
                                text = currentItem?.content?.subtitle?.uppercase() ?: "",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color(0xFFD4AF37),
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 3.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = currentItem?.content?.headlinePrimary ?: currentItem?.title ?: "",
                                style = MaterialTheme.typography.displayLarge.copy(fontSize = 72.sp, fontWeight = FontWeight.Black),
                                color = Color.White
                            )
                            if (currentItem?.content?.headlineSecondary != null) {
                                Text(
                                    text = currentItem.content?.headlineSecondary ?: "",
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

                            }
                        }
                    }
                }
            }
        } else {
            GalleryDetailScreen(
                supabase = supabase,
                item = selectedGalleryItem!!,
                onBack = { selectedGalleryItem = null }
            )
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun GalleryDashboard(
    supabase: SupabaseClient, 
    onFocusItem: (String) -> Unit,
    onSelectItem: (GalleryItem) -> Unit
) {
    var selectedCategory by remember { mutableStateOf("Semua") }
    var allItems by remember { mutableStateOf<List<GalleryItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    
    LaunchedEffect(Unit) {
        isLoading = true
        try {
            allItems = supabase.from("gallery_dashboard")
                .select()
                .decodeList<GalleryItem>()
        } catch (e: Exception) {
            Log.e("GALLERY_ERROR", "Fetch failed: ${e.message}")
        } finally {
            isLoading = false
        }
    }

    val galleryItems = remember(selectedCategory, allItems) {
        if (selectedCategory == "Semua") allItems else allItems.filter { it.category == selectedCategory }
    }

    val categories = remember(allItems) {
        listOf("Semua") + allItems.map { it.category }.distinct().sorted()
    }

    Row(modifier = Modifier.fillMaxSize().padding(top = 24.dp)) {
        Column(
            modifier = Modifier
                .width(220.dp)
                .padding(start = 58.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            categories.forEach { category ->
                val isSelected = selectedCategory == category
                Surface(
                    onClick = { selectedCategory = category },
                    colors = ClickableSurfaceDefaults.colors(
                        containerColor = if (isSelected) Color(0xFFD4AF37) else Color.Transparent,
                        focusedContainerColor = if (isSelected) Color(0xFFD4AF37) else Color.White.copy(alpha = 0.1f)
                    ),
                    shape = ClickableSurfaceDefaults.shape(RoundedCornerShape(12.dp)),
                    scale = ClickableSurfaceDefaults.scale(focusedScale = 1.05f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = category,
                        color = if (isSelected) Color.Black else Color.White.copy(alpha = 0.5f),
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                            letterSpacing = 1.sp
                        ),
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(24.dp))

        Column(modifier = Modifier.weight(1f).padding(end = 58.dp)) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("MEMUAT...", color = Color.White)
                }
            } else if (allItems.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("GALERI KOSONG", color = Color.White.copy(alpha = 0.5f))
                }
            } else {
                GalleryGrid(
                    items = galleryItems,
                    onFocusItem = { item -> onFocusItem(item.imageUrl) },
                    onSelectItem = onSelectItem
                )
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun GalleryDetailScreen(supabase: SupabaseClient, item: GalleryItem, onBack: () -> Unit) {
    val backFocusRequester = remember { FocusRequester() }
    var details by remember { mutableStateOf<GalleryDetail?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    val formatCurrency: (Double?) -> String = { amount ->
        if (amount != null) "Rp %,d".format(amount.toLong()).replace(',', '.') + ",-"
        else "-"
    }

    LaunchedEffect(item.id) {
        if (item.id != null) {
            try {
                details = supabase.from("gallery_details")
                    .select { filter { eq("id", item.id) } }
                    .decodeSingleOrNull<GalleryDetail>()
            } catch (e: Exception) {
                Log.e("SUPABASE_ERROR", "Failed to fetch details: ${e.message}")
            }
        }
        isLoading = false
    }

    BackHandler(onBack = onBack)

    LaunchedEffect(Unit) {
        backFocusRequester.requestFocus()
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        AsyncImage(
            model = item.imageUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            error = ColorPainter(Color(0xFF1A1A1A)),
            placeholder = ColorPainter(Color.Black)
        )

        Surface(
            onClick = onBack,
            modifier = Modifier
                .padding(32.dp)
                .align(Alignment.TopStart)
                .focusRequester(backFocusRequester),
            colors = ClickableSurfaceDefaults.colors(
                containerColor = Color.Black.copy(alpha = 0.5f),
                focusedContainerColor = Color.White.copy(alpha = 0.2f)
            ),
            shape = ClickableSurfaceDefaults.shape(CircleShape)
        ) {
            Box(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("← back", color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .background(Color(0xFFF5F3ED).copy(alpha = 0.85f))
                .drawBehind { 
                    drawLine(Color.Black.copy(alpha = 0.8f), start = Offset(0f, 0f), end = Offset(size.width, 0f), strokeWidth = 3f)
                    drawLine(Color.Black.copy(alpha = 0.8f), start = Offset(0f, size.height), end = Offset(size.width, size.height), strokeWidth = 3f)
                }
                .padding(vertical = 40.dp, horizontal = 58.dp)
        ) {
            if (isLoading) {
                Text("Memuat data...", color = Color.Black, modifier = Modifier.align(Alignment.Center))
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1.2f).padding(end = 40.dp)) {
                        Text(
                            text = details?.title ?: item.title,
                            style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Black, fontSize = 64.sp),
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = details?.description ?: item.description ?: "",
                            style = MaterialTheme.typography.headlineSmall.copy(fontStyle = FontStyle.Italic, lineHeight = 36.sp),
                            color = Color.Black.copy(alpha = 0.8f)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(6.dp)
                            .fillMaxHeight()
                            .background(Color.Black)
                    )

                    Column(modifier = Modifier.weight(1f).padding(start = 40.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                        Column {
                            Text("Tipe Ruangan", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.Black)
                            val roomTypes = listOfNotNull(details?.name, details?.vibe, details?.signatureMaterials).joinToString(", ")
                            Text(if (roomTypes.isNotBlank()) roomTypes else item.category, style = MaterialTheme.typography.titleMedium, fontStyle = FontStyle.Italic, color = Color.Black.copy(alpha = 0.8f))
                        }
                        
                        Column {
                            Text("Warna Palet :", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.Black)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row {
                                val colors = listOfNotNull(details?.color1, details?.color2, details?.color3, details?.color4)
                                if (colors.isNotEmpty()) {
                                    colors.forEachIndexed { index, hex ->
                                        val color = try { Color(android.graphics.Color.parseColor(hex)) } catch(e: Exception) { Color.Gray }
                                        Box(
                                            modifier = Modifier
                                                .offset(x = (-12 * index).dp)
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(color)
                                                .border(2.dp, Color.White, CircleShape)
                                        )
                                    }
                                } else {
                                    Text("-", color = Color.Black)
                                }
                            }
                        }
                        
                        Column {
                            Text("Filosofis", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.Black)
                            Text(details?.philosophy ?: "-", style = MaterialTheme.typography.titleMedium, fontStyle = FontStyle.Italic, color = Color.Black.copy(alpha = 0.8f))
                        }
                        
                        Column {
                            Text("Estimasi Biaya", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.Black)
                            val budgetAvg = details?.budgetAvg
                            val minCost = formatCurrency(budgetAvg?.times(0.75))
                            val maxCost = formatCurrency(budgetAvg?.times(1.5))
                            Text("$minCost  s.d.  $maxCost", style = MaterialTheme.typography.titleMedium, color = Color.Black.copy(alpha = 0.8f))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun GalleryGrid(
    items: List<GalleryItem>, 
    onFocusItem: (GalleryItem) -> Unit,
    onSelectItem: (GalleryItem) -> Unit
) {
    val scrollState = rememberScrollState()
    
    Box(modifier = Modifier.fillMaxSize()) {
        if (items.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Tidak ada item ditemukan", color = Color.White.copy(alpha = 0.5f))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(end = 48.dp, bottom = 40.dp)
            ) {
                val rows = items.chunked(3)
                rows.forEach { rowItems ->
                    Row(modifier = Modifier.fillMaxWidth().height(380.dp)) {
                        rowItems.forEachIndexed { colIndex, item ->
                            GalleryCard(
                                item = item,
                                onFocus = { onFocusItem(item) },
                                onSelect = { onSelectItem(item) },
                                modifier = Modifier.weight(1f).fillMaxHeight()
                            )
                            if (colIndex < rowItems.size - 1) {
                                Spacer(modifier = Modifier.width(28.dp))
                            }
                        }
                        repeat(3 - rowItems.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                    Spacer(modifier = Modifier.height(28.dp))
                }
            }
        }
        
        if (items.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxHeight(0.9f)
                    .width(12.dp)
                    .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(6.dp))
            ) {
                val scrollPercentage by remember {
                    derivedStateOf {
                        if (scrollState.maxValue > 0) scrollState.value.toFloat() / scrollState.maxValue else 0f
                    }
                }
                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val thumbHeight = maxHeight * 0.2f
                    val trackHeight = maxHeight - thumbHeight
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(thumbHeight)
                            .offset(y = trackHeight * scrollPercentage)
                            .background(Color.White.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun GalleryCard(
    item: GalleryItem, 
    onFocus: () -> Unit, 
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Surface(
        onClick = onSelect,
        scale = ClickableSurfaceDefaults.scale(focusedScale = 1.05f),
        shape = ClickableSurfaceDefaults.shape(RoundedCornerShape(16.dp)),
        modifier = modifier.onFocusChanged { if (it.isFocused) onFocus() }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(item.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                error = ColorPainter(Color(0xFF2A2A2A)),
                placeholder = ColorPainter(Color(0xFF1A1A1A))
            )
            
            Box(
                modifier = Modifier
                    .width(48.dp)
                    .height(8.dp)
                    .background(
                        color = Color(0xFFD4AF37),
                        shape = RoundedCornerShape(bottomEnd = 8.dp)
                    )
                    .align(Alignment.TopStart)
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f)),
                            startY = 400f
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(20.dp)
            ) {
                Text(
                    text = item.category.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD4AF37)
                    )
                )
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        lineHeight = 28.sp
                    ),
                    color = Color.White
                )
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
        val others = items.filter { it.slug != "home" && it.content?.showInNavbar != false }
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
            selectedTabIndex = if (selectedIndex >= 0) selectedIndex else 0,
            containerColor = Color.Transparent,
            indicator = { tabPositions, _ ->
                if (selectedIndex >= 0 && selectedIndex < tabPositions.size) {
                    val currentTabPosition = tabPositions[selectedIndex]
                    val isHomeSelected = selectedIndex == 0
                    val widthMultiplier = if (isHomeSelected) 0.85f else 0.8f
                    val offsetMultiplier = if (isHomeSelected) 0.0f else 0.1f
                    
                    val animWidth by animateDpAsState(targetValue = (currentTabPosition.right - currentTabPosition.left) * widthMultiplier, label = "width")
                    val animLeft by animateDpAsState(targetValue = currentTabPosition.left + (currentTabPosition.right - currentTabPosition.left) * offsetMultiplier, label = "offset")
                    
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
                    Box(
                        modifier = Modifier
                            .height(60.dp)
                            .padding(start = if (isHome) 0.dp else 16.dp, end = 16.dp, bottom = 4.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        if (isHome) {
                            Text(
                                text = "RENOVASRI",
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                                color = Color.White,
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
                    .padding(start = 16.dp, end = 0.dp, bottom = 4.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                AsyncImage(
                    model = "https://i.pravatar.cc/150?u=renovasri",
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
