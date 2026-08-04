package com.anddd.nevera.feature.fridge.edit

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.anddd.nevera.core.designsystem.component.datepicker.NeveraDatePickerDialog
import com.anddd.nevera.core.ui.component.bottomsheet.CategoryBottomSheet
import com.anddd.nevera.core.ui.component.bottomsheet.StorageLocationBottomSheet
import com.anddd.nevera.feature.fridge.edit.component.EditFridgeIngredientContent
import com.anddd.nevera.feature.fridge.edit.model.EditFridgeIngredientIntent
import com.anddd.nevera.feature.fridge.edit.model.EditFridgeIngredientSideEffect
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun EditFridgeIngredientScreen(
    onNavigateBack: () -> Unit,
    viewModel: EditFridgeIngredientViewModel = hiltViewModel(),
) {
    val uiState by viewModel.collectAsState()
    val context = LocalContext.current

    var showCategorySheet by remember { mutableStateOf(false) }
    var showStorageLocationSheet by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    viewModel.collectSideEffect { effect ->
        when (effect) {
            EditFridgeIngredientSideEffect.NavigateBack -> onNavigateBack()
            EditFridgeIngredientSideEffect.ShowUpdateFailedToast ->
                Toast.makeText(context, "수정에 실패했습니다.", Toast.LENGTH_SHORT).show()
            EditFridgeIngredientSideEffect.ShowCategorySheet -> showCategorySheet = true
            EditFridgeIngredientSideEffect.ShowStorageLocationSheet -> showStorageLocationSheet = true
            EditFridgeIngredientSideEffect.ShowDatePicker -> showDatePicker = true
        }
    }

    EditFridgeIngredientContent(
        uiState = uiState,
        onIntent = viewModel::handleIntent,
    )

    if (showCategorySheet) {
        CategoryBottomSheet(
            selectedCategory = uiState.category,
            onCategorySelected = { viewModel.handleIntent(EditFridgeIngredientIntent.UpdateCategory(it)) },
            onDismiss = { showCategorySheet = false },
        )
    }

    if (showStorageLocationSheet) {
        StorageLocationBottomSheet(
            selectedLocation = uiState.storageLocation,
            onLocationSelected = { viewModel.handleIntent(EditFridgeIngredientIntent.UpdateStorageLocation(it)) },
            onDismiss = { showStorageLocationSheet = false },
        )
    }

    if (showDatePicker) {
        NeveraDatePickerDialog(
            selectedDate = uiState.expiryDate,
            onDateSelected = { viewModel.handleIntent(EditFridgeIngredientIntent.UpdateExpiryDate(it)) },
            onDismiss = { showDatePicker = false },
        )
    }
}
