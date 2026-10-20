package com.example.cinemaster

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.cinemaster.data.Movie
import com.example.cinemaster.data.MovieStatus

fun MovieStatus.label(): String = when (this) {
    MovieStatus.WANT -> "Muốn xem"
    MovieStatus.WATCHING -> "Đang xem"
    MovieStatus.WATCHED -> "Đã xem"
}

fun MovieStatus.icon(): ImageVector = when (this) {
    MovieStatus.WANT -> Icons.Default.Bookmark
    MovieStatus.WATCHING -> Icons.Default.Visibility
    MovieStatus.WATCHED -> Icons.Default.CheckCircle
}

@Composable
fun MovieStatus.containerColor(): Color = when (this) {
    MovieStatus.WANT -> MaterialTheme.colorScheme.primaryContainer
    MovieStatus.WATCHING -> MaterialTheme.colorScheme.tertiaryContainer
    MovieStatus.WATCHED -> MaterialTheme.colorScheme.secondaryContainer
}

@Composable
fun MovieStatus.contentColor(): Color = when (this) {
    MovieStatus.WANT -> MaterialTheme.colorScheme.onPrimaryContainer
    MovieStatus.WATCHING -> MaterialTheme.colorScheme.onTertiaryContainer
    MovieStatus.WATCHED -> MaterialTheme.colorScheme.onSecondaryContainer
}

/**
 * Hiệu ứng shimmer (vệt sáng lấp lánh) khi đang tải dữ liệu
 */
@Composable
fun shimmerBrush(showShimmer: Boolean = true): Brush {
    return if (showShimmer) {
        val shimmerColors = listOf(
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        )
        val transition = rememberInfiniteTransition(label = "shimmerTransition")
        val translateAnim by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1000f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "shimmerTranslate"
        )
        Brush.linearGradient(
            colors = shimmerColors,
            start = Offset(translateAnim - 200f, translateAnim - 200f),
            end = Offset(translateAnim, translateAnim)
        )
    } else {
        Brush.linearGradient(
            listOf(Color.Transparent, Color.Transparent)
        )
    }
}

/**
 * Thẻ phim dạng danh sách (Elevated Card)
 */
@Composable
fun MovieRow(
    movie: Movie,
    modifier: Modifier = Modifier,
    trailing: @Composable (() -> Unit)? = null
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = movie.posterPath?.let { "https://image.tmdb.org/t/p/w185$it" },
                contentDescription = movie.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(width = 75.dp, height = 110.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )

            Spacer(Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (movie.rating != null && movie.rating > 0f) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFFB800).copy(alpha = 0.15f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFFFB800),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "%.1f".format(movie.rating),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFD97706)
                            )
                        }
                    }
                }

                Text(
                    text = movie.overview.ifBlank { "Chưa có thông tin tóm tắt." },
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (trailing != null) {
                Spacer(Modifier.width(8.dp))
                trailing()
            }
        }
    }
}

/**
 * Thẻ phim dạng lưới (Grid Poster Card) chuẩn phong cách Cinema/Streaming
 */
@Composable
fun MovieGridCard(
    movie: Movie,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.68f)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                AsyncImage(
                    model = movie.posterPath?.let { "https://image.tmdb.org/t/p/w500$it" },
                    contentDescription = movie.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Rating Badge trên poster
                if (movie.rating != null && movie.rating > 0f) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Black.copy(alpha = 0.7f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFFFB800),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(Modifier.width(3.dp))
                            Text(
                                text = "%.1f".format(movie.rating),
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier.padding(10.dp)
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * Khung Shimmer Skeleton mô phỏng danh sách đang tải
 */
@Composable
fun ShimmerMovieItem(modifier: Modifier = Modifier) {
    val brush = shimmerBrush()
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(width = 75.dp, height = 110.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(brush)
            )
            Spacer(Modifier.width(14.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(18.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(brush)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .height(14.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(brush)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(14.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(brush)
                )
            }
        }
    }
}

/**
 * Thanh chấm sao tương tác (Interactive 5-star rating bar)
 * 5 ngôi sao tương ứng với thang điểm 0 - 10 (mỗi sao là 2 điểm)
 */
@Composable
fun StarRatingBar(
    rating: Float,
    onRatingChanged: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..5) {
            val starThreshold = i * 2f
            val halfThreshold = starThreshold - 1f

            val (icon, tint) = when {
                rating >= starThreshold -> Pair(Icons.Default.Star, Color(0xFFFFB800))
                rating >= halfThreshold -> Pair(Icons.AutoMirrored.Filled.StarHalf, Color(0xFFFFB800))
                else -> Pair(Icons.Default.StarBorder, MaterialTheme.colorScheme.outline)
            }

            IconButton(
                onClick = {
                    // Chạm lần 1 = đầy sao (i*2), nếu bấm lại cùng sao đó thì giảm nửa sao (i*2-1)
                    val newRating = if (rating == starThreshold) halfThreshold else starThreshold
                    onRatingChanged(newRating)
                },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = "Chấm $i sao",
                    tint = tint,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Spacer(Modifier.width(8.dp))
        Text(
            text = if (rating > 0f) "%.1f / 10".format(rating) else "Chưa chấm",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = if (rating > 0f) Color(0xFFD97706) else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}