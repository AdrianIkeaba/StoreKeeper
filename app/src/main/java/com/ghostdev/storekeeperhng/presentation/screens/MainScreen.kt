package com.ghostdev.storekeeperhng.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun MainScreen(
    onAddClick: () -> Unit,
    onItemClick: (Long) -> Unit,
    onEditClick: (Long) -> Unit,
    onResetToSplash: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            androidx.compose.foundation.layout.Column {
                androidx.compose.material3.Divider(color = Color(0xFFE5E7EB))
                NavigationBar(containerColor = Color.White) {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = { Icon(Icons.Default.Home, contentDescription = null, tint = if (selectedTab == 0) Color.Black else Color.Black) },
                        label = { Text("Home", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Medium) },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color(0xFFF3F4F6),
                            selectedIconColor = Color.Black,
                            selectedTextColor = Color.Black,
                            unselectedIconColor = Color.Black,
                            unselectedTextColor = Color.Black
                        )
                    )
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = { Icon(Icons.Default.Person, contentDescription = null, tint = if (selectedTab == 1) Color.Black else Color.Black) },
                        label = { Text("Profile", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Medium) },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color(0xFFF3F4F6),
                            selectedIconColor = Color.Black,
                            selectedTextColor = Color.Black,
                            unselectedIconColor = Color.Black,
                            unselectedTextColor = Color.Black
                        )
                    )
                }
            }
        },
        containerColor = Color.White
    ) { padding ->
        when (selectedTab) {
            0 -> androidx.compose.foundation.layout.Box(
                modifier = Modifier.padding(bottom = padding.calculateBottomPadding())
            ) {
                HomeScreen(onAddClick = onAddClick, onItemClick = onItemClick, onEditClick = onEditClick)
            }
            else -> ProfileScreen(padding, onResetToSplash)
        }
    }
}
