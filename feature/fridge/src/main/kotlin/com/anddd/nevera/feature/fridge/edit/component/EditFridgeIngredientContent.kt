package com.anddd.nevera.feature.fridge.edit.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.anddd.nevera.core.designsystem.component.appbar.NeveraAppBar
import com.anddd.nevera.core.designsystem.component.appbar.NeveraAppBarNavigation
import com.anddd.nevera.core.designsystem.component.button.NeveraButtonSize
import com.anddd.nevera.core.designsystem.component.button.NeveraFilledButton
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.core.ui.component.field.CostFieldRow
import com.anddd.nevera.core.ui.component.field.DropdownFieldRow
import com.anddd.nevera.core.ui.component.field.ExpiryDateFieldRow
import com.anddd.nevera.core.ui.component.field.QuantityFieldRow
import com.anddd.nevera.core.ui.displayName
import com.anddd.nevera.feature.fridge.R
import com.anddd.nevera.feature.fridge.edit.model.EditFridgeIngredientIntent
import com.anddd.nevera.feature.fridge.edit.model.EditFridgeIngredientUiState
import com.anddd.nevera.core.ui.R as CoreUiR

@Composable
internal fun EditFridgeIngredientContent(
    uiState: EditFridgeIngredientUiState,
    onIntent: (EditFridgeIngredientIntent) -> Unit,
    modifier: Modifier = Modifier,
) {

    Scaffold(
        modifier = modifier,
        topBar = {
            NeveraAppBar(
                title = stringResource(R.string.edit_fridge_ingredient_title),
                navigation = NeveraAppBarNavigation.Close(onClick = {
                    onIntent(EditFridgeIngredientIntent.CloseClick)
                }),
            )
        },
        bottomBar = {
            NeveraFilledButton(
                label = stringResource(R.string.edit_fridge_ingredient_confirm),
                onClick = { onIntent(EditFridgeIngredientIntent.ConfirmClick) },
                size = NeveraButtonSize.Large,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            )
        },
        containerColor = NeveraTheme.colors.surfacePrimary,
        contentWindowInsets = WindowInsets.navigationBars,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(vertical = 16.dp),
        ) {
            Text(
                text = uiState.name,
                style = NeveraTheme.typography.headlineLarge,
                color = NeveraTheme.colors.textPrimary,
                modifier = Modifier.padding(horizontal = 16.dp),
            )

            Spacer(modifier = Modifier.height(24.dp))

            QuantityFieldRow(
                quantity = uiState.quantity,
                onQuantityChanged = { onIntent(EditFridgeIngredientIntent.UpdateQuantity(it)) },
            )

            Spacer(modifier = Modifier.height(NeveraTheme.spacing.gap8))

            CostFieldRow(
                cost = uiState.cost,
                onCostChanged = { onIntent(EditFridgeIngredientIntent.UpdateCost(it)) },
            )

            Spacer(modifier = Modifier.height(NeveraTheme.spacing.gap8))

            DropdownFieldRow(
                label = stringResource(CoreUiR.string.field_label_category),
                value = uiState.category.displayName(),
                onClick = { onIntent(EditFridgeIngredientIntent.CategoryFieldClick) },
            )

            Spacer(modifier = Modifier.height(NeveraTheme.spacing.gap8))

            DropdownFieldRow(
                label = stringResource(CoreUiR.string.field_label_storage_location),
                value = uiState.storageLocation.displayName(),
                onClick = { onIntent(EditFridgeIngredientIntent.StorageLocationFieldClick) },
            )

            Spacer(modifier = Modifier.height(NeveraTheme.spacing.gap8))

            ExpiryDateFieldRow(
                expiryDate = uiState.expiryDate,
                onClick = { onIntent(EditFridgeIngredientIntent.ExpiryDateFieldClick) },
            )
        }
    }
}

