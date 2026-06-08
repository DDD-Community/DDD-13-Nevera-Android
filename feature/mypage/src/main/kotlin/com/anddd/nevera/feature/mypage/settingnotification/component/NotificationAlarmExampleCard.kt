package com.anddd.nevera.feature.mypage.settingnotification.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.feature.mypage.R as MyPageR

@Composable
internal fun NotificationAlarmExampleCard(
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = NeveraTheme.spacing.padding16),
        shape = RoundedCornerShape(NeveraTheme.radius.medium),
        color = NeveraTheme.colors.backgroundSecondary,
    ) {
        Image(
            painter = painterResource(MyPageR.drawable.img_alarmex_01),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth(),
        )
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun NotificationAlarmExampleCardPreview() {
    NeveraTheme {
        NotificationAlarmExampleCard()
    }
}
