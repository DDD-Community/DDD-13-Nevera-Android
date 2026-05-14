package com.anddd.nevera.feature.mypage.main.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.anddd.nevera.core.designsystem.R
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.feature.mypage.main.model.ProfileUiModel
import com.anddd.nevera.feature.mypage.R as MyPageR

@Composable
internal fun ProfileContent(
    modifier: Modifier = Modifier,
    profile: ProfileUiModel,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(NeveraTheme.spacing.padding16),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(NeveraTheme.spacing.gap12)
    ) {
        if (profile.profileImage == null) {
            Image(
                painter = painterResource(R.drawable.ic_info),
                contentDescription = stringResource(MyPageR.string.mypage_profile_image_desc)
            )
        } else {
            AsyncImage(
                model = profile.profileImage,
                contentDescription = stringResource(MyPageR.string.mypage_profile_image_desc),
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                error = painterResource(R.drawable.ic_info),
                placeholder = painterResource(R.drawable.ic_info),
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(NeveraTheme.spacing.gap2)
        ) {
            Text(
                text = profile.nickname,
                style = NeveraTheme.typography.titleLarge,
                color = NeveraTheme.colors.textSecondary
            )
            Text(
                text = profile.email,
                style = NeveraTheme.typography.captionLarge,
                color = NeveraTheme.colors.textCaption
            )
        }
    }
}

@Preview(
    name = "ProfileContent - 프로필 이미지 없음",
    showBackground = true,
    widthDp = 360
)
@Composable
private fun ProfileContentNoImagePreview() {
    NeveraTheme {
        ProfileContent(
            profile = ProfileUiModel(profileImage = null)
        )
    }
}

@Preview(
    name = "ProfileContent - 프로필 이미지 있음",
    showBackground = true,
    widthDp = 360
)
@Composable
private fun ProfileContentWithImagePreview() {
    NeveraTheme {
        ProfileContent(
            profile = ProfileUiModel()
        )
    }
}
