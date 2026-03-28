package com.example.airline.ui.screens.profile

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Hardcoded palette — admin hero uses a deeper, more authoritative midnight-to-blue-purple
private val AdminHdrTop    = Color(0xFF060D1F)
private val AdminHdrMid    = Color(0xFF0A1628)
private val AdminHdrBottom = Color(0xFF1A237E)   // deep indigo — distinct from passenger blue
private val AdminOnHdr     = Color(0xFFE8EAF6)
private val AdminOnHdrMuted = Color(0xFF9FA8DA)

@Composable
fun AdminProfileScreen(
    outerPadding: PaddingValues = PaddingValues(),
    onLogout: () -> Unit
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            // ── Hero header ───────────────────────────────────────────────────
            // Deep indigo gradient distinguishes admin from passenger (blue).
            // Edge-to-edge: gradient extends behind status bar; content is padded
            // down by outerPadding.calculateTopPadding().
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(listOf(AdminHdrTop, AdminHdrMid, AdminHdrBottom))
                    )
                    .padding(
                        top    = outerPadding.calculateTopPadding() + 28.dp,
                        bottom = 36.dp,
                        start  = 24.dp,
                        end    = 24.dp
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Account icon with glowing circle background
                    Box(
                        modifier         = Modifier
                            .size(108.dp)
                            .background(Color.White.copy(alpha = 0.10f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector        = Icons.Filled.AccountCircle,
                            contentDescription = "Profile",
                            tint               = Color.White,
                            modifier           = Modifier.size(96.dp)
                        )
                    }

                    // Email — only User model fields shown (ID, Email, Role)
                    Text(
                        text       = "admin@airline.com",
                        style      = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color      = AdminOnHdr
                    )

                    // Role badge — amber tint for admin authority
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = Color(0xFFE65100).copy(alpha = 0.22f)
                    ) {
                        Text(
                            text          = "● ADMINISTRATOR",
                            modifier      = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            style         = MaterialTheme.typography.labelMedium,
                            fontWeight    = FontWeight.Bold,
                            color         = Color(0xFFFFCC80),
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            // ── Content sections ──────────────────────────────────────────────
            Column(
                modifier            = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                // Administration section
                AdminProfileSection(title = "Administration") {
                    AdminProfileMenuItem(
                        icon    = Icons.Filled.AdminPanelSettings,
                        label   = "System Settings",
                        onClick = { Toast.makeText(context, "System Settings", Toast.LENGTH_SHORT).show() }
                    )
                    Divider(color = MaterialTheme.colorScheme.outlineVariant)
                    AdminProfileMenuItem(
                        icon    = Icons.Filled.Group,
                        label   = "Manage Users",
                        onClick = { Toast.makeText(context, "Manage Users", Toast.LENGTH_SHORT).show() }
                    )
                    Divider(color = MaterialTheme.colorScheme.outlineVariant)
                    AdminProfileMenuItem(
                        icon    = Icons.Filled.History,
                        label   = "Access Logs",
                        onClick = { Toast.makeText(context, "Access Logs", Toast.LENGTH_SHORT).show() }
                    )
                    Divider(color = MaterialTheme.colorScheme.outlineVariant)
                    AdminProfileMenuItem(
                        icon    = Icons.Filled.Analytics,
                        label   = "Analytics Dashboard",
                        onClick = { Toast.makeText(context, "Analytics Dashboard", Toast.LENGTH_SHORT).show() }
                    )
                }

                // System section
                AdminProfileSection(title = "System") {
                    AdminProfileMenuItem(
                        icon    = Icons.Filled.Notifications,
                        label   = "Notification Settings",
                        onClick = { Toast.makeText(context, "Notification Settings", Toast.LENGTH_SHORT).show() }
                    )
                    Divider(color = MaterialTheme.colorScheme.outlineVariant)
                    AdminProfileMenuItem(
                        icon    = Icons.Filled.Lock,
                        label   = "Security & Permissions",
                        onClick = { Toast.makeText(context, "Security & Permissions", Toast.LENGTH_SHORT).show() }
                    )
                    Divider(color = MaterialTheme.colorScheme.outlineVariant)
                    AdminProfileMenuItem(
                        icon    = Icons.Filled.Settings,
                        label   = "App Configuration",
                        onClick = { Toast.makeText(context, "App Configuration", Toast.LENGTH_SHORT).show() }
                    )
                    Divider(color = MaterialTheme.colorScheme.outlineVariant)
                    AdminProfileMenuItem(
                        icon    = Icons.Filled.Help,
                        label   = "Help & Support",
                        onClick = { Toast.makeText(context, "Help & Support", Toast.LENGTH_SHORT).show() }
                    )
                }

                // Logout button
                OutlinedButton(
                    onClick  = onLogout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape    = RoundedCornerShape(16.dp),
                    border   = BorderStroke(1.5.dp, MaterialTheme.colorScheme.error),
                    colors   = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(imageVector = Icons.Filled.Logout, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text       = "Logout",
                        style      = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Bottom clearance above the curved nav bar
                Spacer(modifier = Modifier.height(outerPadding.calculateBottomPadding() + 16.dp))
            }
        }
    }
}

// ─── Helpers ──────────────────────────────────────────────────────────────────

@Composable
private fun AdminProfileSection(
    title:   String,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text       = title,
            style      = MaterialTheme.typography.labelLarge,
            color      = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold,
            modifier   = Modifier.padding(start = 4.dp)
        )
        Surface(
            shape          = RoundedCornerShape(16.dp),
            tonalElevation = 2.dp,
            modifier       = Modifier.fillMaxWidth()
        ) {
            Column { content() }
        }
    }
}

@Composable
private fun AdminProfileMenuItem(
    icon:    ImageVector,
    label:   String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = LocalIndication.current,
                onClick           = onClick
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector        = icon,
            contentDescription = null,
            tint               = MaterialTheme.colorScheme.primary,
            modifier           = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text     = label,
            style    = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector        = Icons.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint               = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier           = Modifier.size(20.dp)
        )
    }
}
