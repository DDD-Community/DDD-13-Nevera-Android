package com.anddd.nevera.feature.main.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.domain.model.ingredient.FoodCategory
import com.anddd.nevera.core.ui.backgroundColor
import com.anddd.nevera.core.ui.iconRes
import com.anddd.nevera.feature.main.home.model.IngredientUiModel

private val CategoryImageSize = 56.dp
private val CategoryImageBorderWidth = 1.dp

@Composable
internal fun IngredientItem(
    ingredient: IngredientUiModel,
    modifier: Modifier = Modifier,
) {
    val imageShape = RoundedCornerShape(NeveraTheme.radius.small)

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(NeveraTheme.spacing.gap12),
    ) {
        Image(
            painter = painterResource(ingredient.category.iconRes()),
            contentDescription = null,
            modifier = Modifier
                .size(CategoryImageSize)
                .background(ingredient.category.backgroundColor(), imageShape)
                .clip(imageShape)
                .border(CategoryImageBorderWidth, NeveraTheme.colors.borderNormal, imageShape),
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(NeveraTheme.spacing.gap2),
        ) {
            Text(
                text = ingredient.name,
                style = NeveraTheme.typography.titleXSmall,
                color = NeveraTheme.colors.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "${ingredient.categoryName} · ${ingredient.quantity}개",
                style = NeveraTheme.typography.captionLarge,
                color = NeveraTheme.colors.textCaption,
            )
        }
        Text(
            text = "+%,d원".format(ingredient.cost),
            style = NeveraTheme.typography.titleXSmall,
            color = NeveraTheme.colors.textSecondary,
        )
    }
}

@Preview(name = "IngredientItem - Vegetable", showBackground = true, widthDp = 360)
@Composable
private fun IngredientItemVegetablePreview() {
    NeveraTheme {
        IngredientItem(
            ingredient = IngredientUiModel(
                id = 1L,
                name = "깐마늘",
                category = FoodCategory.Veg,
                categoryName = "채소",
                quantity = 1,
                cost = 5600,
            ),
        )
    }
}

@Preview(name = "IngredientItem - Fruit", showBackground = true, widthDp = 360)
@Composable
private fun IngredientItemFruitPreview() {
    NeveraTheme {
        IngredientItem(
            ingredient = IngredientUiModel(
                id = 2L,
                name = "썬키스트 고당도 오렌지",
                category = FoodCategory.Fruit,
                categoryName = "과일",
                quantity = 1,
                cost = 8000,
            ),
        )
    }
}

@Preview(name = "IngredientItem - MeatEgg", showBackground = true, widthDp = 360)
@Composable
private fun IngredientItemMeatEggPreview() {
    NeveraTheme {
        IngredientItem(
            ingredient = IngredientUiModel(
                id = 3L,
                name = "닭가슴살",
                category = FoodCategory.MeatEggs,
                categoryName = "육류/계란",
                quantity = 2,
                cost = 12000,
            ),
        )
    }
}

@Preview(name = "IngredientItem - Seafood", showBackground = true, widthDp = 360)
@Composable
private fun IngredientItemSeafoodPreview() {
    NeveraTheme {
        IngredientItem(
            ingredient = IngredientUiModel(
                id = 4L,
                name = "참치(생)",
                category = FoodCategory.Sea,
                categoryName = "해산물",
                quantity = 1,
                cost = 32000,
            ),
        )
    }
}

@Preview(name = "IngredientItem - Dairy", showBackground = true, widthDp = 360)
@Composable
private fun IngredientItemDairyPreview() {
    NeveraTheme {
        IngredientItem(
            ingredient = IngredientUiModel(
                id = 5L,
                name = "매일 바이오 플레인 요구르트",
                category = FoodCategory.Dairy,
                categoryName = "유제품",
                quantity = 2,
                cost = 3000,
            ),
        )
    }
}

@Preview(name = "IngredientItem - Sauce", showBackground = true, widthDp = 360)
@Composable
private fun IngredientItemSaucePreview() {
    NeveraTheme {
        IngredientItem(
            ingredient = IngredientUiModel(
                id = 6L,
                name = "백설 식용유",
                category = FoodCategory.Sauce,
                categoryName = "소스/양념",
                quantity = 1,
                cost = 6500,
            ),
        )
    }
}

@Preview(name = "IngredientItem - Beverage", showBackground = true, widthDp = 360)
@Composable
private fun IngredientItemBeveragePreview() {
    NeveraTheme {
        IngredientItem(
            ingredient = IngredientUiModel(
                id = 7L,
                name = "코카콜라 제로",
                category = FoodCategory.Drink,
                categoryName = "음료",
                quantity = 6,
                cost = 4800,
            ),
        )
    }
}

@Preview(name = "IngredientItem - Processed", showBackground = true, widthDp = 360)
@Composable
private fun IngredientItemProcessedPreview() {
    NeveraTheme {
        IngredientItem(
            ingredient = IngredientUiModel(
                id = 8L,
                name = "참치 통조림",
                category = FoodCategory.Processed,
                categoryName = "가공식품",
                quantity = 3,
                cost = 3500,
            ),
        )
    }
}

@Preview(name = "IngredientItem - Other", showBackground = true, widthDp = 360)
@Composable
private fun IngredientItemOtherPreview() {
    NeveraTheme {
        IngredientItem(
            ingredient = IngredientUiModel(
                id = 9L,
                name = "기타 식재료",
                category = FoodCategory.Etc,
                categoryName = "기타",
                quantity = 1,
                cost = 2000,
            ),
        )
    }
}

@Preview(name = "IngredientItem - 긴 이름 말줄임", showBackground = true, widthDp = 360)
@Composable
private fun IngredientItemLongNamePreview() {
    NeveraTheme {
        IngredientItem(
            ingredient = IngredientUiModel(
                id = 10L,
                name = "아주아주길다란이름의처음보는식재...",
                category = FoodCategory.Veg,
                categoryName = "채소",
                quantity = 1,
                cost = 5600,
            ),
        )
    }
}
