package com.anddd.nevera.feature.main.home

import android.widget.Toast
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.anddd.nevera.core.ui.component.ReceiptCaptureModeBottomSheet
import com.anddd.nevera.feature.main.R
import com.anddd.nevera.feature.main.home.component.CreateWishBottomSheet
import com.anddd.nevera.feature.main.home.component.GreetingBottomSheet
import com.anddd.nevera.feature.main.home.component.HomeContent
import com.anddd.nevera.feature.main.home.component.SetNicknameBottomSheet
import com.anddd.nevera.feature.main.home.component.UpdateWishBottomSheet
import com.anddd.nevera.feature.main.home.model.HomeIntent
import com.anddd.nevera.feature.main.home.model.HomeSideEffect
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToCamera: () -> Unit,
    onNavigateToGallery: () -> Unit,
    onNavigateToNotification: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val state = viewModel.collectAsState().value

    val wishCreatedMessage = stringResource(R.string.home_wish_created_toast)
    val wishUpdatedMessage = stringResource(R.string.home_wish_updated_toast)

    val captureModeSheetState = rememberModalBottomSheetState()
    var showCaptureModeBottomSheet by rememberSaveable { mutableStateOf(false) }
    var showSetNicknameBottomSheet by rememberSaveable { mutableStateOf(false) }
    var showGreetingBottomSheet by rememberSaveable { mutableStateOf(false) }
    var showCreateWishBottomSheet by rememberSaveable { mutableStateOf(false) }
    var showUpdateWishBottomSheet by rememberSaveable { mutableStateOf(false) }

    viewModel.collectSideEffect { effect ->
        when (effect) {
            is HomeSideEffect.ShowError -> Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()

            HomeSideEffect.ShowCaptureModeBottomSheet -> showCaptureModeBottomSheet = true

            HomeSideEffect.ShowSetNicknameBottomSheet -> showSetNicknameBottomSheet = true

            HomeSideEffect.ShowGreetingBottomSheet -> showGreetingBottomSheet = true

            HomeSideEffect.ShowCreateWishBottomSheet -> showCreateWishBottomSheet = true

            HomeSideEffect.ShowUpdateWishBottomSheet -> showUpdateWishBottomSheet = true

            HomeSideEffect.ShowWishCreatedToast -> Toast.makeText(context, wishCreatedMessage, Toast.LENGTH_SHORT).show()

            HomeSideEffect.ShowWishUpdatedToast -> Toast.makeText(context, wishUpdatedMessage, Toast.LENGTH_SHORT).show()

            HomeSideEffect.NavigateToNotification -> onNavigateToNotification()
        }
    }

    HomeContent(
        uiState = state,
        onIntent = viewModel::handleIntent,
    )

    if (showCaptureModeBottomSheet) {
        ReceiptCaptureModeBottomSheet(
            sheetState = captureModeSheetState,
            onReceiptScan = {
                showCaptureModeBottomSheet = false
                onNavigateToCamera()
            },
            onOnlineCapture = {
                showCaptureModeBottomSheet = false
                onNavigateToGallery()
            },
            onDismiss = { showCaptureModeBottomSheet = false },
        )
    }
    if (showSetNicknameBottomSheet) {
        SetNicknameBottomSheet(
            onNicknameConfirmed = { nickname ->
                showSetNicknameBottomSheet = false
                viewModel.handleIntent(HomeIntent.UpdateNicknameClick(nickname))
            },
        )
    }
    if (showGreetingBottomSheet) {
        GreetingBottomSheet(
            onCreateWishClick = {
                showGreetingBottomSheet = false
                viewModel.handleIntent(HomeIntent.CreateWishClick)
            },
            onSkipClick = { showGreetingBottomSheet = false },
        )
    }
    if (showCreateWishBottomSheet) {
        CreateWishBottomSheet(
            onWishCreated = { name, amount ->
                showCreateWishBottomSheet = false
                viewModel.handleIntent(HomeIntent.CreateWishConfirmed(name, amount))
            },
            onDismissRequest = { showCreateWishBottomSheet = false },
        )
    }
    if (showUpdateWishBottomSheet) {
        state.wish?.let { wish ->
            UpdateWishBottomSheet(
                wishName = wish.name,
                goalAmount = wish.goalAmount.toLong(),
                onWishUpdated = { name, amount ->
                    showUpdateWishBottomSheet = false
                    viewModel.handleIntent(HomeIntent.UpdateWishConfirmed(wish.id, name, amount))
                },
                onDismissRequest = { showUpdateWishBottomSheet = false },
            )
        }
    }
}
