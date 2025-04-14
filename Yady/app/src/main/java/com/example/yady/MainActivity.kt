package com.example.yady

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import com.example.yady.ui.theme.YadyTheme
import com.google.firebase.firestore.FirebaseFirestore
import com.example.yady.models.Group
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContent {
            YadyTheme {
                // Set up NavController for navigation
                val navController = rememberNavController()

                // Define navigation host
                NavHost(navController = navController, startDestination = "landing") {
                    composable("landing") { LandingScreen(navController = navController) }
                    composable("create_group") { CreateGroupScreen(onGroupCreated = {
                        // Navigate back after creating group
                        navController.popBackStack()
                    }) }
                    composable("join_group") {
                        JoinGroupScreen(onGroupJoined = {
                            navController.popBackStack()
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun LandingScreen(navController: NavController) {
    val context = LocalContext.current
    val gradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFFB388FF), // Light purple
            Color(0xFF7C4DFF)  // Deep purple
        ),
        start = Offset(0f, 0f),                 // Top-left
        end = Offset(1000f, 1000f)              // Bottom-right (diagonal)
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = gradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title Text
            Text(
                text = "Yady",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFFFFFFFF),
                modifier = Modifier.padding(bottom = 220.dp),
                fontSize = 72.sp,
            )

            // Welcome Text
            Text(
                text = "Welcome to Yady 👋",
                style = MaterialTheme.typography.headlineMedium,
                color = Color(0xFFFFFFFF),
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Create Group Button
            Button(
                onClick = {
                    navController.navigate("create_group")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFA67748),
                    contentColor = Color(0xFFEAE2DA)
                )
            ) {
                Text("Create Group", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Join Group Button
            Button(
                onClick = {
                    navController.navigate("join_group")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFA67748),
                    contentColor = Color(0xFFEAE2DA)
                )
            ) {
                Text("Join Group", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
fun CreateGroupScreen(onGroupCreated: () -> Unit) {
    val context = LocalContext.current
    var groupName by remember { mutableStateOf("") }
    var inviteUser by remember { mutableStateOf("") }
    val gradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFFFFFFFF),
            // Light purple
            Color(0xFFB388FF)  // Deep purple
        ),
        start = Offset(0f, 0f),                 // Top-left
        end = Offset.Infinite             // Bottom-right (diagonal)
    )
    Box (modifier = Modifier
        .fillMaxSize()
        .background(brush = gradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Create a New Group",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF4A148C),
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Group Name Input
            OutlinedTextField(
                value = groupName,
                onValueChange = { groupName = it },
                label = {
                    Text(
                        text = "Group Name",
                        color = Color (0xFF3E2A60)
                    ) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Invite User Input
            OutlinedTextField(
                value = inviteUser,
                onValueChange = { inviteUser = it },
                label = {
                    Text(
                        text = "Invite User (Email/Phone)",
                        color = Color(0xFF3E2A60)
                    )},
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Create Group Button
            Button(
                onClick = {
                    val db = FirebaseFirestore.getInstance()
                    val group = Group(name = groupName, invitedUser = inviteUser)

                    db.collection("groups")
                        .add(group)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Group created!", Toast.LENGTH_SHORT).show()
                            onGroupCreated() // navigate back or forward
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(context, "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF7C4DFF),
                    contentColor = Color.White
                )
            ) {
                Text("Create Group", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
@Composable
fun JoinGroupScreen(onGroupJoined: () -> Unit) {
    val context = LocalContext.current
    var groupSearch by remember { mutableStateOf("") }
    var inviteCode by remember { mutableStateOf("") }
    val gradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFFFFFFFF), // Light purple
            Color(0xFFB388FF)  // Deep purple
        ),
        start = Offset(0f, 0f),                 // Top-left
        end = Offset.Infinite             // Bottom-right (diagonal)
    )
    Box (modifier = Modifier
        .fillMaxSize()
        .background(brush = gradient)
            ) {Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Join a Group",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF4A148C),
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Group Name Search
        OutlinedTextField(
            value = groupSearch,
            onValueChange = { groupSearch = it },
            label = {
                Text(
                    text = "Search for Group",
                    color = Color(0xFF3E2A60)
                ) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Invite Code
        OutlinedTextField(
            value = inviteCode,
            onValueChange = { inviteCode = it },
            label = {
                Text(
                    text = "Invite Code",
                    color = Color(0xFF3E2A60)
                ) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Join Group Button
        Button(
            onClick = {
                Toast.makeText(context, "Attempting to join group...", Toast.LENGTH_SHORT).show()
                onGroupJoined()
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF7C4DFF),
                contentColor = Color.White
            )
        ) {
            Text("Join Group", style = MaterialTheme.typography.titleMedium)
        }
    }}
}