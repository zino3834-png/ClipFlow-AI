package com.zino.clipflow

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { ClipFlowApp() }
    }
}

private val Ink = Color(0xFF10131A)
private val Panel = Color(0xFF1B2230)
private val Accent = Color(0xFF7C5CFC)

@Composable
private fun ClipFlowApp() {
    var selectedVideo by remember { mutableStateOf<Uri?>(null) }
    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> selectedVideo = uri }

    MaterialTheme(colorScheme = darkColorScheme(
        primary = Accent,
        background = Ink,
        surface = Panel
    )) {
        Surface(modifier = Modifier.fillMaxSize(), color = Ink) {
            Column(
                modifier = Modifier.fillMaxSize().padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("CLIPFLOW AI", style = MaterialTheme.typography.titleLarge, color = Color.White)
                        Text("Mobile video studio", color = Color.LightGray)
                    }
                    Text("BETA", color = Accent, style = MaterialTheme.typography.labelLarge)
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Panel),
                    shape = RoundedCornerShape(22.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Your video workspace", color = Color.White, style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(12.dp))
                        if (selectedVideo == null) {
                            Box(
                                modifier = Modifier.fillMaxWidth().height(190.dp)
                                    .background(Color(0xFF252D3B), RoundedCornerShape(16.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Choose a video to start", color = Color.LightGray)
                            }
                        } else {
                            VideoPreview(uri = selectedVideo!!)
                        }
                        Spacer(Modifier.height(14.dp))
                        Button(onClick = { picker.launch("video/*") }, modifier = Modifier.fillMaxWidth()) {
                            Text(if (selectedVideo == null) "Import video" else "Choose another video")
                        }
                    }
                }

                Text("Create", color = Color.White, style = MaterialTheme.typography.titleMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    FeatureCard("Editor", "Trim • Join • Text", Modifier.weight(1f))
                    FeatureCard("AI Clips", "Find highlights", Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    FeatureCard("Captions", "Arabic • English", Modifier.weight(1f))
                    FeatureCard("Publish", "Social platforms", Modifier.weight(1f))
                }
                Spacer(Modifier.weight(1f))
                Text(
                    "Early preview • Editing, AI and publishing modules are being built in stages.",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun FeatureCard(title: String, subtitle: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = Panel)) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(title, color = Color.White, style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(5.dp))
            Text(subtitle, color = Color.LightGray, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun VideoPreview(uri: Uri) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val player = remember(uri) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(uri))
            prepare()
            playWhenReady = false
        }
    }
    DisposableEffect(player) {
        onDispose { player.release() }
    }
    AndroidView(
        factory = { ctx -> PlayerView(ctx).apply { this.player = player } },
        modifier = Modifier.fillMaxWidth().height(210.dp)
    )
}
