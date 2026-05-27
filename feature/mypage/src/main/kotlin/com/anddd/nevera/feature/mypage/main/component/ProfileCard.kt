package com.anddd.nevera.feature.mypage.main.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
        if (profile.hasWish) {
            ProfileWithWish(
                modifier = modifier,
                profileImage = profile.profileImage,
                nickname = profile.nickname,
                email = profile.email,
            )
        } else {
            Profile(
                modifier = modifier,
                profileImage = profile.profileImage,
                nickname = profile.nickname,
                email = profile.email,
            )
        }
    }
}

@Composable
private fun Profile(
    modifier: Modifier = Modifier,
    profileImage: String?,
    nickname: String,
    email: String,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(NeveraTheme.spacing.padding16),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(NeveraTheme.spacing.gap12)
    ) {
        if (profileImage == null) {
            DefaultProfileImage()
        } else {
            AsyncImage(
                model = profileImage,
                contentDescription = stringResource(MyPageR.string.mypage_profile_image_desc),
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                error = painterResource(R.drawable.ic_info),
                placeholder = painterResource(R.drawable.ic_info),
            )
        }

        NicknameWithEmailColumn(
            nickname = nickname,
            email = email,
        )
    }
}


@Composable
private fun ProfileWithWish(
    modifier: Modifier = Modifier,
    profileImage: String?,
    nickname: String,
    email: String,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                top = NeveraTheme.spacing.padding4,
                bottom = NeveraTheme.spacing.padding16,
                start = NeveraTheme.spacing.padding16,
                end = NeveraTheme.spacing.padding16
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (profileImage == null) {
            DefaultProfileImage()
        } else {
            Box(modifier = Modifier.size(62.dp)) {
                AsyncImage(
                    model = profileImage,
                    contentDescription = stringResource(MyPageR.string.mypage_profile_image_desc),
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .align(Alignment.BottomStart),
                    contentScale = ContentScale.Crop,
                    error = painterResource(R.drawable.ic_info),
                    placeholder = painterResource(R.drawable.ic_info),
                )

                Image(
                    painter = painterResource(MyPageR.drawable.ic_avatar_wish),
                    contentDescription = "Has Wish Status",
                    modifier = Modifier
                        .size(40.dp)
                        .align(Alignment.TopEnd)
                )
            }
        }

        NicknameWithEmailColumn(
            nickname = nickname,
            email = email,
        )
    }
}

@Composable
private fun DefaultProfileImage() {
    Image(
        painter = painterResource(R.drawable.ic_info),
        contentDescription = stringResource(MyPageR.string.mypage_profile_image_desc)
    )
}

@Composable
private fun NicknameWithEmailColumn(
    nickname: String,
    email: String,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(NeveraTheme.spacing.gap2)
    ) {
        Text(
            text = nickname,
            style = NeveraTheme.typography.titleLarge,
            color = NeveraTheme.colors.textSecondary
        )
        Text(
            text = email,
            style = NeveraTheme.typography.captionLarge,
            color = NeveraTheme.colors.textCaption
        )
    }
}

@Preview(
    name = "ProfileContent",
    showBackground = true,
    widthDp = 360
)
@Composable
private fun ProfileContentPreview() {
    NeveraTheme {
        ProfileContent(
            profile = ProfileUiModel(
                nickname = "김푸드",
                email = "anddd@email.com",
                profileImage = null,
                hasWish = false,
            )
        )
    }
}

@Preview(
    name = "ProfileContent - HasWish",
    showBackground = true,
    widthDp = 360
)
@Composable
private fun ProfileContentHasWishPreview() {
    NeveraTheme {
        ProfileContent(
            profile = ProfileUiModel(
                nickname = "김푸드",
                email = "anddd@email.com",
                profileImage = null,
                hasWish = true,
            )
        )
    }
}

@Preview(
    name = "ProfileContent - With Image",
    showBackground = true,
    widthDp = 360
)
@Composable
private fun ProfileContentWithImagePreview() {
    NeveraTheme {
        ProfileContent(
            profile = ProfileUiModel(
                nickname = "김푸드",
                email = "anddd@email.com",
                profileImage = "https://picsum.photos/200",
                hasWish = false,
            )
        )
    }
}

@Preview(
    name = "ProfileContent - HasWish With Image",
    showBackground = true,
    widthDp = 360
)
@Composable
private fun ProfileContentHasWishWithImagePreview() {
    NeveraTheme {
        ProfileContent(
            profile = ProfileUiModel(
                nickname = "김푸드",
                email = "anddd@email.com",
                profileImage = "https://picsum.photos/200",
                hasWish = true,
            )
        )
    }
}

