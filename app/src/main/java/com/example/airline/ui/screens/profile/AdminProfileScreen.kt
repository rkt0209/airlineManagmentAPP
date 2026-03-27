package com.example.airline.ui.screens.profile

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProfileScreen(
    outerPadding: PaddingValues = PaddingValues(),
    onLogout: () -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = { Text("Admin Profile") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // ── Profile Header ────────────────────────────────────────────
            Card(
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    MaterialTheme.colorScheme.surface
                                )
                            )
                        )
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = "Profile Picture",
                            modifier = Modifier.size(88.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "System Administrator",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "admin@airline.com",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        // Role badge — primary color to distinguish from Passenger badge
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "ADMINISTRATOR",
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
            }

            // ── Administration Section ────────────────────────────────────
            AdminProfileSection(title = "Administration") {
                AdminProfileMenuItem(
                    icon = Icons.Filled.AdminPanelSettings,
                    label = "System Settings",
                    onClick = { Toast.makeText(context, "System Settings", Toast.LENGTH_SHORT).show() }
                )
                Divider(color = MaterialTheme.colorScheme.outlineVariant)
                AdminProfileMenuItem(
                    icon = Icons.Filled.Group,
                    label = "Manage Users",
                    onClick = { Toast.makeText(context, "Manage Users", Toast.LENGTH_SHORT).show() }
                )
                Divider(color = MaterialTheme.colorScheme.outlineVariant)
                AdminProfileMenuItem(
                    icon = Icons.Filled.History,
                    label = "Access Logs",
                    onClick = { Toast.makeText(context, "Access Logs", Toast.LENGTH_SHORT).show() }
                )
                Divider(color = MaterialTheme.colorScheme.outlineVariant)
                AdminProfileMenuItem(
                    icon = Icons.Filled.Analytics,
                    label = "Analytics Dashboard",
                    onClick = { Toast.makeText(context, "Analytics Dashboard", Toast.LENGTH_SHORT).show() }
                )
            }

            // ── System Section ────────────────────────────────────────────
            AdminProfileSection(title = "System") {
                AdminProfileMenuItem(
                    icon = Icons.Filled.Notifications,
                    label = "Notification Settings",
                    onClick = { Toast.makeText(context, "Notification Settings", Toast.LENGTH_SHORT).show() }
                )
                Divider(color = MaterialTheme.colorScheme.outlineVariant)
                AdminProfileMenuItem(
                    icon = Icons.Filled.Lock,
                    label = "Security & Permissions",
                    onClick = { Toast.makeText(context, "Security & Permissions", Toast.LENGTH_SHORT).show() }
                )
                Divider(color = MaterialTheme.colorScheme.outlineVariant)
                AdminProfileMenuItem(
                    icon = Icons.Filled.Settings,
                    label = "App Configuration",
                    onClick = { Toast.makeText(context, "App Configuration", Toast.LENGTH_SHORT).show() }
                )
                Divider(color = MaterialTheme.colorScheme.outlineVariant)
                AdminProfileMenuItem(
                    icon = Icons.Filled.Help,
                    label = "Help & Support",
                    onClick = { Toast.makeText(context, "Help & Support", Toast.LENGTH_SHORT).show() }
                )
            }

            // ── Logout Button ─────────────────────────────────────────────
            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.error),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.Logout,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Logout",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Push content above the outer bottom nav bar
            Spacer(modifier = Modifier.height(outerPadding.calculateBottomPadding() + 8.dp))
        }
    }
}

@Composable
private fun AdminProfileSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 4.dp)
        )
        Surface(
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column { content() }
        }
    }
}

@Composable
private fun AdminProfileMenuItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = LocalIndication.current,
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
    }
}
