package com.phonerepair.shop.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.GTranslate
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.phonerepair.shop.R
import com.phonerepair.shop.ui.components.*
import com.phonerepair.shop.ui.theme.PhoneRepairTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val isLogin by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val errorMessage by remember { mutableStateOf<String?>(null) }
    val showPassword by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Background pattern
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f))
            )
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .widthIn(min = 320.dp, max = 420.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Logo
                    Icon(
                        painter = painterResource(id = R.drawable.ic_repair_logo),
                        contentDescription = "شعار المحل",
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (isLogin) "مرحباً بعودتك" else "إنشاء حساب جديد",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Text(
                            text = if (isLogin) 
                                "سجل الدخول لإدارة محل الصيانة" 
                            else 
                                "املأ البيانات أدناه للبدء",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }

                    // Form Fields
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (!isLogin) {
                            CustomTextField(
                                label = "الاسم الكامل",
                                value = fullName,
                                onValueChange = { fullName = it },
                                placeholder = "أحمد محمد",
                                leadingIcon = Icons.Filled.Person,
                                keyboardType = KeyboardType.Text
                            )
                            CustomTextField(
                                label = "رقم الهاتف",
                                value = phone,
                                onValueChange = { phone = it },
                                placeholder = "05XXXXXXXX",
                                leadingIcon = Icons.Filled.Phone,
                                keyboardType = KeyboardType.Phone
                            )
                        }
                        
                        CustomTextField(
                            label = "البريد الإلكتروني",
                            value = email,
                            onValueChange = { email = it },
                            placeholder = "email@example.com",
                            leadingIcon = Icons.Filled.Email,
                            keyboardType = KeyboardType.Email
                        )
                        
                        CustomTextField(
                            label = "كلمة المرور",
                            value = password,
                            onValueChange = { password = it },
                            placeholder = "••••••••",
                            leadingIcon = Icons.Filled.Lock,
                            trailingIcon = if (showPassword) 
                                Icons.Filled.Visibility 
                            else 
                                Icons.Filled.VisibilityOff,
                            onTrailingIconClick = { showPassword = !showPassword },
                            visualTransformation = if (showPassword) 
                            else 
                                PasswordVisualTransformation(),
                            isError = isLogin && errorMessage != null
                        )
                        
                        if (!isLogin) {
                            CustomTextField(
                                label = "تأكيد كلمة المرور",
                                value = confirmPassword,
                                onValueChange = { confirmPassword = it },
                                placeholder = "••••••••",
                                leadingIcon = Icons.Filled.Lock,
                                trailingIcon = if (showPassword) 
                                    Icons.Filled.Visibility 
                                else 
                                    Icons.Filled.VisibilityOff,
                                onTrailingIconClick = { showPassword = !showPassword },
                                visualTransformation = if (showPassword) 
                                else 
                                    PasswordVisualTransformation()
                            )
                        }
                    }

                    errorMessage?.let { msg ->
                        Text(
                            text = msg,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Action Buttons
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CustomButton(
                            text = if (isLogin) "تسجيل الدخول" else "إنشاء الحساب",
                            onClick = {
                                if (isLogin) {
                                    if (validateLogin(email, password)) {
                                        isLoading = true
                                        viewModel.login(email, password) { success, error ->
                                            isLoading = false
                                            if (success) onLoginSuccess()
                                            else errorMessage = error
                                        }
                                    }
                                } else {
                                    if (validateRegister(fullName, phone, email, password, confirmPassword)) {
                                        isLoading = true
                                        viewModel.register(fullName, phone, email, password) { success, error ->
                                            isLoading = false
                                            if (success) onLoginSuccess()
                                            else errorMessage = error
                                        }
                                    }
                                }
                            },
                            isLoading = isLoading
                        )
                        
                        if (!isLogin) {
                            CustomOutlinedButton(
                                text = "تسجيل الدخول بحساب موجود",
                                onClick = { isLogin = true; errorMessage = null },
                                icon = Icons.Filled.Login
                            )
                        }
                    }

                    // Divider
                    DividerWithText(text = "أو")

                    // Social Login
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CustomOutlinedButton(
                            text = "Google",
                            onClick = {
                                isLoading = true
                                viewModel.signInWithGoogle { success, error ->
                                    isLoading = false
                                    if (success) onLoginSuccess()
                                    else errorMessage = error
                                }
                            },
                            icon = Icons.Filled.GTranslate,
                            modifier = Modifier.weight(1f)
                        )
                        CustomOutlinedButton(
                            text = "Apple",
                            onClick = {
                                // Apple Sign In
                            },
                            icon = Icons.Filled.Phone,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Switch Mode
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isLogin) "ليس لديك حساب؟" else "لديك حساب بالفعل؟",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        TextButton(
                            onClick = { isLogin = !isLogin; errorMessage = null }
                        ) {
                            Text(
                                text = if (isLogin) "إنشاء حساب" else "تسجيل الدخول",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun validateLogin(email: String, password: String): Boolean {
    return email.isNotBlank() && password.isNotBlank()
}

private fun validateRegister(
    fullName: String,
    phone: String,
    email: String,
    password: String,
    confirmPassword: String
): Boolean {
    return fullName.isNotBlank() && phone.isNotBlank() && email.isNotBlank() && 
           password.length >= 6 && password == confirmPassword
}

// Simple ViewModel for auth
class AuthViewModel : androidx.lifecycle.ViewModel() {
    fun login(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        // TODO: Implement Firebase Auth
        androidx.lifecycle.viewModelScope.launch {
            kotlinx.coroutines.delay(1000)
            callback(true, null)
        }
    }
    
    fun register(fullName: String, phone: String, email: String, password: String, callback: (Boolean, String?) -> Unit) {
        // TODO: Implement Firebase Auth
        androidx.lifecycle.viewModelScope.launch {
            kotlinx.coroutines.delay(1000)
            callback(true, null)
        }
    }
    
    fun signInWithGoogle(callback: (Boolean, String?) -> Unit) {
        // TODO: Implement Google Sign In
        androidx.lifecycle.viewModelScope.launch {
            kotlinx.coroutines.delay(1000)
            callback(true, null)
        }
    }
}