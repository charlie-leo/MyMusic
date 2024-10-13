package com.assignment.mymusic.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.assignment.mymusic.R

/**
 * Created by Charles Raj I on 13/10/24
 * @project MyMusic
 * @author Charles Raj
 */
@Composable
fun MusicListScreen() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
    ) {
        LazyColumn {
            items(20) {
                Card(
                    onClick = {
                    },
                    colors = CardColors(
                        contentColor = Color.White,
                        containerColor = Color.Transparent,
                        disabledContentColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .padding(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .padding(12.dp)
                        ,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Text(text = "THis is a Song One")
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "00:00:00")
                        }
                        HorizontalDivider(
                            color = Color.LightGray,
                            thickness = 1.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }
                }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max)
                .padding(10.dp)
                .background(color = Color.LightGray, shape = RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {


                Text(
                    text = "",
                    modifier = Modifier
                        .fillMaxWidth()
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                ) {
                    Slider(
                        value = 0f,
                        onValueChange = {
                        },
                        valueRange = 0f..10f
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "",
                            modifier = Modifier
                        )
                        Text(
                            text = "",
                            modifier = Modifier
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = Color.Transparent)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.previous_icon),
                        contentDescription = "",
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                            }
                    )
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .background(color = Color.Gray, shape = CircleShape)
                            .clickable {
                            }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.play_icon),
                            contentDescription = "",
                            modifier = Modifier
                                .align(Alignment.Center)
                        )
                    }

                    Icon(
                        painter = painterResource(id = R.drawable.next_icon),
                        contentDescription = "",
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                            }
                    )
                }
            }
        }
    }
}


@Preview(showSystemUi = true, showBackground = true)
@Composable
fun MusicListPreview() {
    MusicListScreen()
}