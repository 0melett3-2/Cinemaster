package com.example.cinemaster

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.cinemaster.data.Movie
import com.example.cinemaster.data.MovieStatus

private val sortLabels = listOf("Mới thêm gần đây", "Tên phim (A-Z)", "Điểm đánh giá cao")

private fun List<Movie>.applyFilter(query: String, sortIndex: Int): List<Movie> {
    val filtered = filter { it.title.contains(query.trim(), ignoreCase = true) }
    return when (sortIndex) {
        1 -> filtered.sortedBy { it.title.lowercase() }
        2 -> filtered.sortedByDescending { it.rating ?: -1f }
        else -> filtered.sortedByDescending { it.addedAt }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onMovieClick: (Int) -> Unit,
    onSearchClick: () -> Unit,
    vm: MovieListViewModel = viewModel(factory = MovieListViewModel.Factory)
) {
    var tab by rememberSaveable { mutableIntStateOf(0) }
    var query by rememberSaveable { mutableStateOf("") }
    var sortIndex by rememberSaveable { mutableIntStateOf(0) }
    var menuOpen by remember { mutableStateOf(false) }
    var isGridView by rememberSaveable { mutableStateOf(false) }
    var randomMovieCandidate by remember { mutableStateOf<Movie?>(null) }

    val want by vm.wantList.collectAsState()
    val watching by vm.watchingList.collectAsState()
    val watched by vm.watchedList.collectAsState()

    val statuses = listOf(MovieStatus.WANT, MovieStatus.WATCHING, MovieStatus.WATCHED)
    val lists = listOf(want, watching, watched)
    val movies = lists[tab].applyFilter(query, sortIndex)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Movie,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Cinemaster",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    // Nút chuyển đổi giao diện Lưới (Grid) <-> Danh sách (List)
                    IconButton(onClick = { isGridView = !isGridView }) {
                        Icon(
                            imageVector = if (isGridView) Icons.AutoMirrored.Filled.ViewList else Icons.Default.GridView,
                            contentDescription = if (isGridView) "Dạng danh sách" else "Dạng lưới poster"
                        )
                    }
                    // Nút tìm kiếm nhanh
                    IconButton(onClick = onSearchClick) {
                        Icon(Icons.Default.Search, contentDescription = "Tìm phim")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onSearchClick,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Thêm phim") },
                shape = RoundedCornerShape(16.dp)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Thanh chuyển đổi Tab hiện đại (SecondaryTabRow)
            SecondaryTabRow(selectedTabIndex = tab) {
                statuses.forEachIndexed { index, status ->
                    Tab(
                        selected = tab == index,
                        onClick = { tab = index },
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(status.label())
                                Badge(
                                    containerColor = if (tab == index)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Text("${lists[index].size}")
                                }
                            }
                        }
                    )
                }
            }

            // Thanh tìm kiếm và bộ lọc sắp xếp
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Lọc nhanh trong danh sách...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null)
                    },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { query = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Xóa lọc")
                            }
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )

                Spacer(Modifier.width(8.dp))

                Box {
                    FilledTonalIconButton(
                        onClick = { menuOpen = true },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.size(52.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = "Sắp xếp")
                    }

                    DropdownMenu(
                        expanded = menuOpen,
                        onDismissRequest = { menuOpen = false }
                    ) {
                        sortLabels.forEachIndexed { i, label ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = label,
                                        fontWeight = if (sortIndex == i) FontWeight.Bold else FontWeight.Normal,
                                        color = if (sortIndex == i) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                },
                                onClick = {
                                    sortIndex = i
                                    menuOpen = false
                                }
                            )
                        }
                    }
                }
            }

            // Banner "🎲 Không biết xem gì?" (chỉ xuất hiện ở Tab Muốn xem)
            AnimatedVisibility(
                visible = tab == 0 && want.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .clickable { randomMovieCandidate = want.random() },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Casino,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = "Phân vân chưa biết xem gì? Chạm để chọn ngẫu nhiên!",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            // Vùng hiển thị danh sách phim hoặc Trạng thái trống (Empty State)
            if (movies.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Movie,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            modifier = Modifier.size(72.dp)
                        )
                        Text(
                            text = if (query.isNotBlank())
                                "Không tìm thấy phim nào khớp với \"$query\""
                            else
                                "Chưa có bộ phim nào trong mục ${statuses[tab].label().lowercase()}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        if (query.isBlank()) {
                            Button(
                                onClick = onSearchClick,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Khám phá & Thêm phim ngay")
                            }
                        }
                    }
                }
            } else {
                if (isGridView) {
                    // Dạng lưới Poster rạp chiếu (2 cột)
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(movies, key = { it.id }) { movie ->
                            MovieGridCard(
                                movie = movie,
                                onClick = { onMovieClick(movie.id) }
                            )
                        }
                    }
                } else {
                    // Dạng danh sách thông tin chi tiết
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(movies, key = { it.id }) { movie ->
                            MovieRow(
                                movie = movie,
                                modifier = Modifier.clickable { onMovieClick(movie.id) }
                            )
                        }
                    }
                }
            }
        }

        // Hộp thoại popup "Gợi ý phim ngẫu nhiên hôm nay"
        randomMovieCandidate?.let { candidate ->
            Dialog(onDismissRequest = { randomMovieCandidate = null }) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "🎲 Gợi ý xem phim hôm nay",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        AsyncImage(
                            model = candidate.posterPath?.let { "https://image.tmdb.org/t/p/w500$it" },
                            contentDescription = candidate.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(width = 130.dp, height = 185.dp)
                                .clip(RoundedCornerShape(14.dp))
                        )

                        Text(
                            text = candidate.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = candidate.overview.ifBlank { "Không có mô tả." },
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    val remaining = want.filter { it.id != candidate.id }
                                    randomMovieCandidate = if (remaining.isNotEmpty()) remaining.random() else null
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Đổi phim")
                            }

                            Button(
                                onClick = {
                                    val id = candidate.id
                                    randomMovieCandidate = null
                                    onMovieClick(id)
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Xem ngay")
                            }
                        }
                    }
                }
            }
        }
    }
}