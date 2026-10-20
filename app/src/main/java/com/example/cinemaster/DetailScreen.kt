package com.example.cinemaster

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.cinemaster.data.MovieStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    movieId: Int,
    onBack: () -> Unit,
    vm: DetailViewModel = viewModel(
        key = "detail_$movieId",
        factory = DetailViewModel.factory(movieId)
    )
) {
    val movie by vm.movie.collectAsState()
    val context = LocalContext.current
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    Scaffold { padding ->
        movie?.let { m ->
            var rating by remember(m.id) { mutableFloatStateOf(m.rating ?: 0f) }
            var note by remember(m.id) { mutableStateOf(m.note) }

            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = padding.calculateBottomPadding())
                        .verticalScroll(rememberScrollState())
                ) {
                    // --- CINEMATIC HEADER (Backdrop + Floating Poster) ---
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                    ) {
                        // Ảnh nền mờ (Backdrop)
                        AsyncImage(
                            model = m.posterPath?.let { "https://image.tmdb.org/t/p/w500$it" },
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .blur(18.dp)
                        )

                        // Lớp phủ Gradient làm tối từ trên xuống dưới
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Black.copy(alpha = 0.4f),
                                            MaterialTheme.colorScheme.background.copy(alpha = 0.8f),
                                            MaterialTheme.colorScheme.background
                                        )
                                    )
                                )
                        )

                        // Poster nổi phía trước và tiêu đề vắn tắt
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            ElevatedCard(
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
                                modifier = Modifier
                                    .size(width = 115.dp, height = 165.dp)
                            ) {
                                AsyncImage(
                                    model = m.posterPath?.let { "https://image.tmdb.org/t/p/w500$it" },
                                    contentDescription = m.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            Spacer(Modifier.width(16.dp))

                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = m.title,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 3
                                )
                                SuggestionChip(
                                    onClick = {},
                                    label = { Text(m.status.label()) },
                                    icon = {
                                        Icon(
                                            imageVector = m.status.icon(),
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    },
                                    colors = SuggestionChipDefaults.suggestionChipColors(
                                        containerColor = m.status.containerColor(),
                                        labelColor = m.status.contentColor(),
                                        iconContentColor = m.status.contentColor()
                                    ),
                                    border = null
                                )
                            }
                        }
                    }

                    // --- NỘI DUNG CHI TIẾT ---
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // Mô tả nội dung
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Nội dung phim",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text = m.overview.ifBlank { "Chưa có nội dung tóm tắt cho phim này." },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
                                )
                            }
                        }

                        // Trạng thái theo dõi
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Trạng thái theo dõi",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                MovieStatus.values().forEach { s ->
                                    FilterChip(
                                        selected = m.status == s,
                                        onClick = { vm.setStatus(m, s) },
                                        label = { Text(s.label()) },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = s.icon(),
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        },
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                }
                            }
                        }

                        // Đánh giá sao (Interactive Star Rating)
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Đánh giá của bạn",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(Modifier.height(8.dp))

                                StarRatingBar(
                                    rating = rating,
                                    onRatingChanged = { rating = it }
                                )

                                Spacer(Modifier.height(8.dp))
                                Slider(
                                    value = rating,
                                    onValueChange = { rating = it },
                                    valueRange = 0f..10f,
                                    steps = 19
                                )
                            }
                        }

                        // Ghi chú / Nhận xét cá nhân
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Ghi chú & Nhận xét cá nhân",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            OutlinedTextField(
                                value = note,
                                onValueChange = { note = it },
                                placeholder = { Text("Cảm nghĩ hoặc lưu ý sau khi xem...") },
                                leadingIcon = {
                                    Icon(Icons.Default.Edit, contentDescription = null)
                                },
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 3
                            )
                        }

                        // Nút Lưu thay đổi
                        Button(
                            onClick = {
                                vm.saveReview(m, rating.takeIf { it > 0f }, note)
                                Toast.makeText(context, "Đã lưu đánh giá & ghi chú!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Lưu thay đổi", fontWeight = FontWeight.SemiBold)
                        }

                        // Nút Xóa phim
                        OutlinedButton(
                            onClick = { showDeleteConfirmDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Xóa khỏi danh sách")
                        }
                    }
                }

                // Nút quay lại nổi ở góc trên bên trái
                Surface(
                    onClick = onBack,
                    shape = CircleShape,
                    color = Color.Black.copy(alpha = 0.5f),
                    contentColor = Color.White,
                    modifier = Modifier
                        .padding(start = 16.dp, top = padding.calculateTopPadding() + 8.dp)
                        .size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Hộp thoại xác nhận xóa phim
            if (showDeleteConfirmDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteConfirmDialog = false },
                    title = { Text("Xác nhận xóa") },
                    text = { Text("Bạn có chắc chắn muốn xóa phim \"${m.title}\" khỏi danh sách theo dõi?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showDeleteConfirmDialog = false
                                vm.delete(m)
                                onBack()
                            },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Xóa")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteConfirmDialog = false }) {
                            Text("Hủy")
                        }
                    }
                )
            }
        }
    }
}