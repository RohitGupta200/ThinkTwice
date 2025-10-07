package com.app.thinktwice.dashboard.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.app.thinktwice.dashboard.components.*

/**
 * Dashboard Profile screen content
 * Shows user profile, goals, friend invitations, and personal details
 */
@Composable
fun DashboardProfileContent(
    modifier: Modifier = Modifier
) {
    val userName = "Kunal"
    var showGoalEdit by remember { mutableStateOf(false) }
    var showGoalView by remember { mutableStateOf(false) }

    if (showGoalEdit) {
        GoalEditScreen(
            onNavigateBack = { showGoalEdit = false },
            onSaveGoal = { name, amount, deadline ->
                // TODO: Save goal to database
                showGoalEdit = false
            }
        )
    } else if (showGoalView) {
        GoalViewScreen(
            goalName = "Bali Trip",
            targetAmount = 5000.00,
            currentAmount = 1000.00,
            deadline = "10/08/2026",
            status = "Ongoing",
            onNavigateBack = { showGoalView = false },
            onEdit = {
                showGoalView = false
                showGoalEdit = true
            }
        )
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 15.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Header
        item {
            ProfileHeader(userName = userName)
        }

        // Friend invitation section
        item {
            FriendInvitationSection()
        }

        // Goals section
        item {
            GoalsSection(
                onAddGoal = { showGoalEdit = true },
                onEditGoal = { showGoalEdit = true },
                onViewGoal = { showGoalView = true }
            )
        }

        // User details sections
        item {
            UserDetailsSection()
        }

        // Payday section
        item {
            PaydaySection()
        }

        // Current plan section
        item {
            CurrentPlanSection()
        }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun ProfileHeader(
    userName: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { /* TODO: Navigate back */ }) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back"
            )
        }

        Text(
            text = "$userName's profile",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun FriendInvitationSection(
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF454554) // Dark background
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Invite your friends!",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Invite up to 2 of your friends to unlock\nmore customization options for your avatar!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Reward items
                    InviteRewardItem(
                        emoji = "👗",
                        title = "Black dress for avatar",
                        status = "Claimed",
                        isCompleted = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    InviteRewardItem(
                        emoji = "👜",
                        title = "New Handbag for avatar",
                        status = "Next up",
                        isCompleted = false
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { /* TODO: Invite friends */ },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFAD89C)
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Invite your friends!",
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InviteRewardItem(
    emoji: String,
    title: String,
    status: String,
    isCompleted: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = emoji,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isCompleted) Color.Gray else Color.Black
            )
        }

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (isCompleted) Color(0xFF4CAF50) else Color(0xFF2196F3)
        ) {
            Text(
                text = status,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun GoalsSection(
    onAddGoal: () -> Unit,
    onEditGoal: () -> Unit,
    onViewGoal: () -> Unit,
    modifier: Modifier = Modifier
) {
    DashboardSectionCard(
        title = "Your goals",
        modifier = modifier,
        action = {
            Row {
                IconButton(onClick = onAddGoal) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add goal"
                    )
                }
                IconButton(onClick = onEditGoal) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit goal"
                    )
                }
            }
        }
    ) {
        GoalItem(
            title = "Bali Trip",
            status = "Ongoing",
            targetAmount = 5000.00,
            deadline = "10/08/2026",
            onClick = onViewGoal
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "View more",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable(onClick = onViewGoal)
        )
    }
}

@Composable
private fun GoalItem(
    title: String,
    status: String,
    targetAmount: Double,
    deadline: String,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Goal",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF2196F3)
            ) {
                Text(
                    text = status,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Target Amount",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Text(
                    text = "$${targetAmount.toInt()}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Column {
                Text(
                    text = "Deadline (DD/MM/YYYY)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Text(
                    text = deadline,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun UserDetailsSection(
    modifier: Modifier = Modifier
) {
    DashboardSectionCard(
        title = "Your details",
        modifier = modifier,
        action = {
            IconButton(onClick = { /* TODO: Navigate to details */ }) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "View details"
                )
            }
        }
    ) {
        // Content handled by action button
    }
}

@Composable
private fun PaydaySection(
    modifier: Modifier = Modifier
) {
    DashboardSectionCard(
        title = "Your payday",
        modifier = modifier,
        action = {
            IconButton(onClick = { /* TODO: Navigate to payday */ }) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "View payday"
                )
            }
        }
    ) {
        // Content handled by action button
    }
}

@Composable
private fun CurrentPlanSection(
    modifier: Modifier = Modifier
) {
    DashboardSectionCard(
        title = "Your current plan",
        modifier = modifier,
        action = {
            IconButton(onClick = { /* TODO: Navigate to plan */ }) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "View plan"
                )
            }
        }
    ) {
        // Content handled by action button
    }
}