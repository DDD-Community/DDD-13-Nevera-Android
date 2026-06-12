package com.anddd.nevera.core.ui.component.bottomsheet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.anddd.nevera.core.designsystem.component.bottomsheet.NeveraActionBottomSheet
import com.anddd.nevera.core.designsystem.icon.NeveraIcons
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.core.ui.R
import com.anddd.nevera.core.ui.displayName
import com.anddd.nevera.domain.model.ingredient.StorageLocation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StorageLocationBottomSheet(
    selectedLocation: StorageLocation?,
    onLocationSelected: (StorageLocation) -> Unit,
    onDismiss: () -> Unit,
) {
    var tempSelected by remember(selectedLocation) { mutableStateOf(selectedLocation) }
    val sheetState = rememberModalBottomSheetState()

    NeveraActionBottomSheet(
        sheetState = sheetState,
        title = stringResource(R.string.ingredient_storage_location_title),
        confirmLabel = stringResource(R.string.ingredient_storage_location_confirm),
        onConfirm = {
            tempSelected?.let(onLocationSelected)
            onDismiss()
        },
        onDismissRequest = onDismiss,
    ) {
        StorageLocationList(
            selectedLocation = tempSelected,
            onLocationClick = { tempSelected = it },
        )
    }
}

@Composable
private fun StorageLocationList(
    selectedLocation: StorageLocation?,
    onLocationClick: (StorageLocation) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        StorageLocation.entries.forEach { location ->
            StorageLocationItem(
                location = location,
                isSelected = location == selectedLocation,
                onClick = { onLocationClick(location) },
            )
        }
    }
}

@Composable
private fun StorageLocationItem(
    location: StorageLocation,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = isSelected,
                onClick = onClick,
                role = Role.RadioButton,
            )
            .padding(horizontal = NeveraTheme.spacing.padding20)
            .heightIn(min = 48.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = location.displayName(),
            style = NeveraTheme.typography.bodyLarge,
            color = NeveraTheme.colors.textSecondary,
            modifier = Modifier.weight(1f),
        )

        Icon(
            painter = NeveraIcons.Check,
            contentDescription = null,
            tint = if (isSelected) NeveraTheme.colors.primaryNormal
            else NeveraTheme.colors.iconDisabled,
            modifier = Modifier.size(NeveraTheme.iconSize.medium),
        )
    }
}
