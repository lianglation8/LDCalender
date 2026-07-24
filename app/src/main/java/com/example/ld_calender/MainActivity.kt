package com.example.ld_calender

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ld_calender.ui.theme.LDCalenderTheme
import java.util.Calendar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LDCalenderTheme {
                CalendarApp()
            }
        }
    }
}

enum class ViewMode { YEAR, MONTH, WEEK, DAY }

@Composable
fun CalendarApp() {
    val today = Calendar.getInstance()
    val currentYear = today.get(Calendar.YEAR)
    val currentMonth = today.get(Calendar.MONTH) + 1
    val currentDay = today.get(Calendar.DAY_OF_MONTH)

    var selectedYear by remember { mutableStateOf(currentYear) }
    var selectedMonth by remember { mutableStateOf(currentMonth) }
    var selectedDay by remember { mutableStateOf(currentDay) }
    var viewMode by remember { mutableStateOf(ViewMode.DAY) }
    var slideDirection by remember { mutableStateOf(0) } // 1 = next day (from right), -1 = prev day (from left)
    var showYearPicker by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var weekStartMonday by remember { mutableStateOf(true) } // true=周一, false=周日
    var cardHeightFraction by remember { mutableFloatStateOf(0.85f) } // card height fraction, adjustable by drag

    val context = LocalContext.current
    PlanStorage.initialize(context)
    fun dateKey(y: Int, m: Int, d: Int) = "${y}-${String.format("%02d", m)}-${String.format("%02d", d)}"
    val currentKey = dateKey(selectedYear, selectedMonth, selectedDay)

    // Load plan for current date from storage
    var planText by remember { mutableStateOf(PlanStorage.getPlan(context, currentKey)) }

    // When date changes, reload plan
    val prevKey = remember { mutableStateOf(currentKey) }
    if (prevKey.value != currentKey) {
        prevKey.value = currentKey
        planText = PlanStorage.getPlan(context, currentKey)
    }

    val lunarString = LunarCalendar.getLunarString(selectedYear, selectedMonth, selectedDay)
    val weekdayChinese = LunarCalendar.getWeekdayChinese(selectedYear, selectedMonth, selectedDay)

    // Helper to go to next day
    fun goToNextDay() {
        slideDirection = 1
        val cal = Calendar.getInstance().apply {
            set(selectedYear, selectedMonth - 1, selectedDay)
            add(Calendar.DAY_OF_MONTH, 1)
        }
        selectedYear = cal.get(Calendar.YEAR)
        selectedMonth = cal.get(Calendar.MONTH) + 1
        selectedDay = cal.get(Calendar.DAY_OF_MONTH)
    }

    // Helper to go to previous day
    fun goToPrevDay() {
        slideDirection = -1
        val cal = Calendar.getInstance().apply {
            set(selectedYear, selectedMonth - 1, selectedDay)
            add(Calendar.DAY_OF_MONTH, -1)
        }
        selectedYear = cal.get(Calendar.YEAR)
        selectedMonth = cal.get(Calendar.MONTH) + 1
        selectedDay = cal.get(Calendar.DAY_OF_MONTH)
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .padding(top = 24.dp, bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
            // Year view: fixed title header + scrollable 12-month cards
            if (viewMode == ViewMode.YEAR) {
                // Fixed year title
                val deepGreen = Color(0xFF2E7D32)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFD4EDDA), RoundedCornerShape(16.dp))
                            .clickable { showYearPicker = true }
                            .padding(horizontal = 24.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${selectedYear}年",
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            color = deepGreen,
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    // Weekday header row (moved here from individual month cards)
                    val headerLabels = if (weekStartMonday) listOf("一", "二", "三", "四", "五", "六", "日")
                                       else listOf("日", "一", "二", "三", "四", "五", "六")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        headerLabels.forEach { name ->
                            Text(
                                text = name,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = deepGreen.copy(alpha = 0.6f),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Scrollable month cards
                YearView(
                    year = selectedYear,
                    currentMonth = currentMonth,
                    currentYear = currentYear,
                    weekStartMonday = weekStartMonday,
                    onDayClick = { month, day ->
                        selectedMonth = month
                        selectedDay = day
                        viewMode = ViewMode.DAY
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )

                Spacer(modifier = Modifier.height(8.dp))
            } else if (viewMode == ViewMode.MONTH) {
                // Month view
                MonthViewDetail(
                    year = selectedYear,
                    month = selectedMonth,
                    currentYear = currentYear,
                    currentMonth = currentMonth,
                    currentDay = currentDay,
                    weekStartMonday = weekStartMonday,
                    onDayClick = { day ->
                        selectedDay = day
                        viewMode = ViewMode.DAY
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )

                Spacer(modifier = Modifier.height(8.dp))
            } else if (viewMode == ViewMode.WEEK) {
                // Week view
                val contextForWeek = context
                WeekViewDetail(
                    year = selectedYear,
                    month = selectedMonth,
                    day = selectedDay,
                    currentYear = currentYear,
                    currentMonth = currentMonth,
                    currentDay = currentDay,
                    weekStartMonday = weekStartMonday,
                    onDayClick = { y, m, d ->
                        selectedYear = y
                        selectedMonth = m
                        selectedDay = d
                        viewMode = ViewMode.DAY
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )

                Spacer(modifier = Modifier.height(8.dp))
            } else {
                // Day view card with swipe gesture + slide animation
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(cardHeightFraction)
                            .pointerInput(Unit) {
                                var totalDrag = 0f
                                detectHorizontalDragGestures(
                                    onDragEnd = {
                                        if (totalDrag < -100f) {
                                            goToNextDay()
                                        } else if (totalDrag > 100f) {
                                            goToPrevDay()
                                        }
                                        totalDrag = 0f
                                    },
                                    onHorizontalDrag = { _, dragAmount ->
                                        totalDrag += dragAmount
                                    }
                                )
                            }
                    ) {
                        val dateKey = "${selectedYear}-${selectedMonth}-${selectedDay}"
                        val dir = slideDirection
                        AnimatedContent(
                            targetState = dateKey,
                            transitionSpec = {
                                if (dir > 0) {
                                    (slideInHorizontally { it } togetherWith
                                            slideOutHorizontally { -it })
                                } else {
                                    (slideInHorizontally { -it } togetherWith
                                            slideOutHorizontally { it })
                                }
                            },
                            label = "dateTransition"
                        ) {
                            CurvedCalendarCard(
                                year = selectedYear,
                                month = selectedMonth,
                                day = selectedDay,
                                weekdayChinese = weekdayChinese,
                                lunarString = lunarString,
                                planText = planText,
                                onPlanTextChange = { newText ->
                                    planText = newText
                                    PlanStorage.setPlan(context, currentKey, newText)
                                },
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    // Drag handle for resizing
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(20.dp)
                            .pointerInput(Unit) {
                                detectVerticalDragGestures { _, dragAmount ->
                                    val delta = -dragAmount / 700f  // convert px to fraction
                                    cardHeightFraction = (cardHeightFraction + delta).coerceIn(0.45f, 0.95f)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .width(40.dp)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color(0xFFCCCCCC))
                        )
                    }
                }

                // Plan management row
                PlanManagementRow(
                    hasPlan = planText.isNotEmpty(),
                    onExport = { /* TODO */ },
                    onDelete = {
                        PlanStorage.deletePlan(context, currentKey)
                        planText = ""
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            // Bottom control bar
            BottomControlBar(
                viewMode = viewMode,
                onViewModeChange = { viewMode = it },
                onLongPress = {
                    // Jump to today
                    selectedYear = currentYear
                    selectedMonth = currentMonth
                    selectedDay = currentDay
                },
                onSettingsClick = { showSettings = true },
                onPrevious = {
                    when (viewMode) {
                        ViewMode.YEAR -> selectedYear--
                        ViewMode.MONTH -> {
                            if (selectedMonth == 1) {
                                selectedMonth = 12
                                selectedYear--
                            } else {
                                selectedMonth--
                            }
                        }
                        ViewMode.WEEK -> {
                            val cal = Calendar.getInstance().apply {
                                set(selectedYear, selectedMonth - 1, selectedDay)
                                add(Calendar.DAY_OF_MONTH, -7)
                            }
                            selectedYear = cal.get(Calendar.YEAR)
                            selectedMonth = cal.get(Calendar.MONTH) + 1
                            selectedDay = cal.get(Calendar.DAY_OF_MONTH)
                        }
                        ViewMode.DAY -> goToPrevDay()
                    }
                },
                onNext = {
                    when (viewMode) {
                        ViewMode.YEAR -> selectedYear++
                        ViewMode.MONTH -> {
                            if (selectedMonth == 12) {
                                selectedMonth = 1
                                selectedYear++
                            } else {
                                selectedMonth++
                            }
                        }
                        ViewMode.WEEK -> {
                            val cal = Calendar.getInstance().apply {
                                set(selectedYear, selectedMonth - 1, selectedDay)
                                add(Calendar.DAY_OF_MONTH, 7)
                            }
                            selectedYear = cal.get(Calendar.YEAR)
                            selectedMonth = cal.get(Calendar.MONTH) + 1
                            selectedDay = cal.get(Calendar.DAY_OF_MONTH)
                        }
                        ViewMode.DAY -> goToNextDay()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        } // end Column

        // Year Picker Dialog (full-screen overlay)
        if (showYearPicker) {
            YearPickerDialog(
                currentYear = selectedYear,
                onYearSelected = { year ->
                    selectedYear = year
                    showYearPicker = false
                    viewMode = ViewMode.YEAR
                },
                onDismiss = { showYearPicker = false }
            )
        }

        // Settings Dialog (full-screen overlay)
        if (showSettings) {
            SettingsDialog(
                weekStartMonday = weekStartMonday,
                onWeekStartChange = { weekStartMonday = it },
                onDismiss = { showSettings = false }
            )
        }
    } // end Box
    }
}

// ========== YEAR VIEW ==========

@Composable
fun YearView(
    year: Int,
    currentMonth: Int,
    currentYear: Int,
    weekStartMonday: Boolean,
    onDayClick: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    // Scroll to current month on initial composition (only for current year)
    LaunchedEffect(year) {
        if (year == currentYear) {
            // Scroll so current month is at top showing as the first card
            listState.scrollToItem(currentMonth - 1)
        } else {
            listState.scrollToItem(0)
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        itemsIndexed((1..12).toList()) { index, month ->
            MonthCard(
                year = year,
                month = month,
                isCurrentMonth = (year == currentYear && month == currentMonth),
                weekStartMonday = weekStartMonday,
                onDayClick = { day -> onDayClick(month, day) }
            )
        }
    }
}

@Composable
fun MonthCard(
    year: Int,
    month: Int,
    isCurrentMonth: Boolean,
    weekStartMonday: Boolean,
    onDayClick: (Int) -> Unit
) {
    val shape = RoundedCornerShape(20.dp)
    val cardBg = if (isCurrentMonth) Color(0xFFE8F5E9) else Color(0xFFF7F8FA)
    val deepGreen = Color(0xFF2E7D32)
    val borderColor = if (isCurrentMonth) deepGreen else Color(0xFFDDDDDD)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(cardBg, shape)
            .border(2.dp, borderColor, shape)
            .padding(20.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Month title
            Text(
                text = "${month}月",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = deepGreen,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Mini calendar grid for this month
            MiniMonthGrid(year = year, month = month, deepGreen = deepGreen, weekStartMonday = weekStartMonday, onDayClick = onDayClick)
        }
    }
}

@Composable
fun MiniMonthGrid(year: Int, month: Int, deepGreen: Color, weekStartMonday: Boolean, onDayClick: (Int) -> Unit = {}) {
    val cal = Calendar.getInstance()
    cal.set(year, month - 1, 1)
    val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) // Sunday=1 Monday=2 ... Saturday=7
    val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    // Monday-based offset: Monday=0 Tuesday=1 ... Sunday=6
    val weekStartOffset = if (weekStartMonday) {
        (firstDayOfWeek - 2 + 7) % 7  // Monday-based: Monday=0
    } else {
        (firstDayOfWeek - 1 + 7) % 7  // Sunday-based: Sunday=0
    }

    // Build a flat list of all cells (empty headers + day numbers)
    val cells = mutableListOf<Int?>() // null = empty cell, Int = day number
    // Add empty cells before the 1st (Monday-first)
    for (i in 0 until weekStartOffset) {
        cells.add(null)
    }
    for (d in 1..daysInMonth) {
        cells.add(d)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Day cells in 7-column grid — ensure every row has exactly 7 cells
        val rows = cells.chunked(7)
        rows.forEachIndexed { rowIndex, row ->
            val fullRow = if (row.size < 7) {
                row + List(7 - row.size) { null }
            } else {
                row
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                fullRow.forEach { cell ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (cell != null) {
                            Text(
                                text = cell.toString(),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = deepGreen,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.clickable { onDayClick(cell) }
                            )
                        }
                    }
                }
            }
        }
    }
}

// ========== DAY VIEW CARD ==========

@Composable
fun CurvedCalendarCard(
    year: Int,
    month: Int,
    day: Int,
    weekdayChinese: String,
    lunarString: String,
    planText: String,
    onPlanTextChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(28.dp)
    val cardBg = Color(0xFFF7F8FA)
    val deepGreen = Color(0xFF2E7D32)
    val weekdayBg = Color(0xFFD4EDDA) // light green

    Box(
        modifier = modifier
            .clip(shape)
            .background(cardBg, shape)
            .border(2.dp, deepGreen, shape)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Row 1: Year + Month
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.15f)
                    .padding(horizontal = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${year}年${month}月",
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold,
                    color = deepGreen,
                    textAlign = TextAlign.Center
                )
            }
            // Row 2: Day number
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.45f)
                    .padding(horizontal = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = day.toString(),
                    fontSize = 130.sp,
                    fontWeight = FontWeight.Bold,
                    color = deepGreen,
                    textAlign = TextAlign.Center
                )
            }
            // Row 3: Weekday badge
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.2f)
                    .padding(horizontal = 28.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(weekdayBg, RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "周$weekdayChinese",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF2D6A4F),
                        textAlign = TextAlign.Center
                    )
                }
            }
            // Row 4: Lunar date
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.2f)
                    .padding(horizontal = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = lunarString,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = deepGreen,
                    textAlign = TextAlign.Center
                )
            }

            // Divider
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 24.dp),
                thickness = 1.dp,
                color = deepGreen
            )

            // Bottom section: plan input
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.55f)
                    .padding(horizontal = 24.dp)
                    .padding(top = 16.dp, bottom = 16.dp)
            ) {
                Text(
                    text = "今日计划",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = deepGreen
                )
                Spacer(modifier = Modifier.height(12.dp))
                BasicTextField(
                    value = planText,
                    onValueChange = onPlanTextChange,
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        color = deepGreen,
                        lineHeight = 22.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    cursorBrush = androidx.compose.ui.graphics.SolidColor(deepGreen),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFFE8E8E8), RoundedCornerShape(12.dp))
                        .padding(16.dp),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.TopStart
                        ) {
                            if (planText.isEmpty()) {
                                Text(
                                    text = "添加今日计划...",
                                    fontSize = 16.sp,
                                    color = deepGreen.copy(alpha = 0.4f)
                                )
                            }
                            innerTextField()
                        }
                    }
                )
            }
        }
    }
}

// ========== BOTTOM CONTROL BAR ==========

@Composable
fun BottomControlBar(
    viewMode: ViewMode,
    onViewModeChange: (ViewMode) -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onLongPress: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(56.dp)
            .padding(start = 0.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Left: left arrow
        IconButton(onClick = onNext) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "下一天",
                tint = Color(0xFF666666),
                modifier = Modifier.size(26.dp)
            )
        }

        // Center: right arrow
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = onPrevious) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "上一天",
                    tint = Color(0xFF666666),
                    modifier = Modifier.size(26.dp)
                )
            }
        }

        // Gear icon for settings
        IconButton(onClick = onSettingsClick) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "设置",
                tint = Color(0xFF888888),
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // View mode buttons
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            ViewModeButton("年", viewMode == ViewMode.YEAR,
                onClick = { onViewModeChange(ViewMode.YEAR) },
                onLongClick = onLongPress)
            ViewModeButton("月", viewMode == ViewMode.MONTH,
                onClick = { onViewModeChange(ViewMode.MONTH) },
                onLongClick = onLongPress)
            ViewModeButton("周", viewMode == ViewMode.WEEK,
                onClick = { onViewModeChange(ViewMode.WEEK) },
                onLongClick = onLongPress)
            ViewModeButton("日", viewMode == ViewMode.DAY,
                onClick = { onViewModeChange(ViewMode.DAY) },
                onLongClick = onLongPress)
        }
    }
}

// ========== PLAN MANAGEMENT ROW ==========

@Composable
fun PlanManagementRow(
    hasPlan: Boolean,
    onExport: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val deepGreen = Color(0xFF2E7D32)

    Row(
        modifier = modifier
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "计划管理",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = deepGreen
        )

        Spacer(modifier = Modifier.weight(1f))

        if (hasPlan) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(deepGreen)
                    .clickable(onClick = onDelete)
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "删除今日计划",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
        }
    }
}

// ========== VIEW MODE BUTTON ==========

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ViewModeButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val bgColor = if (isSelected) Color(0xFF4A90D9) else Color.Transparent
    val textColor = if (isSelected) Color.White else Color(0xFF666666)

    Box(
        modifier = Modifier
            .padding(horizontal = 2.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 15.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = textColor
        )
    }
}

// ========== MONTH VIEW DETAIL ==========

@Composable
fun MonthViewDetail(
    year: Int,
    month: Int,
    currentYear: Int,
    currentMonth: Int,
    currentDay: Int,
    weekStartMonday: Boolean,
    onDayClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(20.dp)
    val deepGreen = Color(0xFF2E7D32)
    val cardBg = Color(0xFFF7F8FA)

    val cal = Calendar.getInstance()
    cal.set(year, month - 1, 1)
    val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

    // Compute week-column indexing (Monday-based weeks)
    // Calendar.SUNDAY=1, MONDAY=2, ..., SATURDAY=7
    val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
    // gridCol for the 1st: Monday=0, Tue=1, Wed=2, Thu=3, Fri=4, Sat=5, Sun=6
    val firstDayGridCol = if (weekStartMonday) {
        (firstDayOfWeek - 2 + 7) % 7  // Monday-based
    } else {
        (firstDayOfWeek - 1 + 7) % 7  // Sunday-based
    }
    fun weekIndex(day: Int) = (day - 1 + firstDayGridCol) / 7
    val numWeeks = weekIndex(daysInMonth) + 1

    // Group days by weekday, storing day -> week index (column)
    val weekdayOrder = if (weekStartMonday) listOf(2, 3, 4, 5, 6, 7, 1) // Mon, Tue, Wed, Thu, Fri, Sat, Sun
                       else listOf(1, 2, 3, 4, 5, 6, 7) // Sun, Mon, Tue, Wed, Thu, Fri, Sat
    val weekdayLabels = if (weekStartMonday) listOf("周一", "周二", "周三", "周四", "周五", "周六", "周日")
                        else listOf("周日", "周一", "周二", "周三", "周四", "周五", "周六")

    // Map: weekday -> (column -> day number), column is 0-based week index
    val daysByWeekday = mutableMapOf<Int, MutableMap<Int, Int>>()
    for (wd in weekdayOrder) {
        daysByWeekday[wd] = mutableMapOf()
    }
    for (d in 1..daysInMonth) {
        cal.set(year, month - 1, d)
        val wd = cal.get(Calendar.DAY_OF_WEEK)
        val col = weekIndex(d)
        daysByWeekday[wd]?.put(col, d)
    }

    val isCurrent = (year == currentYear && month == currentMonth)

    Box(
        modifier = modifier
            .clip(shape)
            .background(cardBg, shape)
            .border(2.dp, deepGreen, shape)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // Title item
            item {
                Text(
                    text = "${year}年${month}月",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = deepGreen,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp, bottom = 12.dp)
                )
            }

            // Weekday rows
            weekdayOrder.forEachIndexed { index, wd ->
                val dayMap = daysByWeekday[wd] ?: emptyMap()
                val label = weekdayLabels[index]
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp, horizontal = 12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        // Weekday label on the left
                        Text(
                            text = label,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = deepGreen,
                            modifier = Modifier
                                .width(64.dp)
                                .padding(top = 2.dp)
                        )

                        // Day numbers — one column per week, up to 5 weeks
                        Row(
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.Top
                        ) {
                            for (col in 0 until numWeeks) {
                                val day = dayMap[col]
                                if (day != null) {
                                    val isToday = isCurrent && day == currentDay
                                    val lunarDayName = LunarCalendar.getLunarDayName(year, month, day)

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .then(
                                                if (isToday)
                                                    Modifier
                                                        .background(deepGreen, RoundedCornerShape(8.dp))
                                                        .padding(vertical = 4.dp)
                                                else
                                                    Modifier
                                                        .clickable { onDayClick(day) }
                                                        .padding(vertical = 4.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = day.toString(),
                                                fontSize = 28.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isToday) Color.White else deepGreen,
                                                textAlign = TextAlign.Center
                                            )
                                            Text(
                                                text = lunarDayName,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Normal,
                                                color = if (isToday) Color.White.copy(alpha = 0.85f) else deepGreen.copy(alpha = 0.65f),
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                } else {
                                    // Empty placeholder cell for alignment
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }

            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

// ========== WEEK VIEW DETAIL ==========

@Composable
fun WeekViewDetail(
    year: Int,
    month: Int,
    day: Int,
    currentYear: Int,
    currentMonth: Int,
    currentDay: Int,
    weekStartMonday: Boolean,
    onDayClick: (Int, Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(20.dp)
    val deepGreen = Color(0xFF2E7D32)
    val cardBg = Color(0xFFF7F8FA)
    val context = LocalContext.current

    // Determine the first day of the current week based on setting
    val cal = Calendar.getInstance()
    cal.set(year, month - 1, day)
    val currentDow = cal.get(Calendar.DAY_OF_WEEK) // SUNDAY=1, MONDAY=2, ... SATURDAY=7
    // Go back to week start (Monday=2 or Sunday=1)
    val weekStart = if (weekStartMonday) 2 else 1
    val daysFromStart = (currentDow - weekStart + 7) % 7
    cal.add(Calendar.DAY_OF_MONTH, -daysFromStart)

    // Collect 7 days starting from week start
    val weekDays = mutableListOf<Triple<Int, Int, Int>>() // year, month, day
    for (i in 0..6) {
        weekDays.add(Triple(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH)))
        cal.add(Calendar.DAY_OF_MONTH, 1)
    }

    val weekdayOrderW = if (weekStartMonday) listOf(2, 3, 4, 5, 6, 7, 1) // Mon, Tue, Wed, Thu, Fri, Sat, Sun
                        else listOf(1, 2, 3, 4, 5, 6, 7) // Sun, Mon, Tue, Wed, Thu, Fri, Sat
    val weekdayLabelsW = if (weekStartMonday) listOf("周一", "周二", "周三", "周四", "周五", "周六", "周日")
                         else listOf("周日", "周一", "周二", "周三", "周四", "周五", "周六")

    // Build a map from weekday (2-7,1) to (year, month, day) for the current week
    val dayByWeekday = mutableMapOf<Int, Triple<Int, Int, Int>>()
    for (wd in weekDays) {
        val tempCal = Calendar.getInstance()
        tempCal.set(wd.first, wd.second - 1, wd.third)
        dayByWeekday[tempCal.get(Calendar.DAY_OF_WEEK)] = wd
    }

    // Title: year + month range for the week
    val firstDay = weekDays.first()
    val lastDay = weekDays.last()
    val titleText = if (firstDay.second == lastDay.second) {
        "${firstDay.first}年${firstDay.second}月"
    } else if (firstDay.first == lastDay.first) {
        "${firstDay.first}年${firstDay.second}月-${lastDay.second}月"
    } else {
        "${firstDay.first}年${firstDay.second}月-${lastDay.first}年${lastDay.second}月"
    }

    Box(
        modifier = modifier
            .clip(shape)
            .background(cardBg, shape)
            .border(2.dp, deepGreen, shape)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // Title item
            item {
                Text(
                    text = titleText,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = deepGreen,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, bottom = 4.dp)
                )
            }

            // Weekday rows
            weekdayOrderW.forEachIndexed { index, wd ->
                val date = dayByWeekday[wd]
                val label = weekdayLabelsW[index]
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(androidx.compose.foundation.layout.IntrinsicSize.Min)
                            .padding(vertical = 6.dp, horizontal = 12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        // Weekday label on the left
                        Text(
                            text = label,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = deepGreen,
                            modifier = Modifier
                                .width(64.dp)
                                .padding(top = 4.dp)
                        )

                        // Day cell — wider column for 2-digit numbers
                        Box(
                            modifier = Modifier
                                .weight(0.7f),
                            contentAlignment = Alignment.Center
                        ) {
                            if (date != null) {
                                val isToday = date.first == currentYear &&
                                        date.second == currentMonth &&
                                        date.third == currentDay
                                val lunarDayName = LunarCalendar.getLunarDayName(date.first, date.second, date.third)

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .then(
                                            if (isToday)
                                                Modifier
                                                    .background(deepGreen, RoundedCornerShape(8.dp))
                                                    .padding(vertical = 4.dp)
                                            else
                                                Modifier
                                                    .clickable { onDayClick(date.first, date.second, date.third) }
                                                    .padding(vertical = 4.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = date.third.toString(),
                                            fontSize = 30.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isToday) Color.White else deepGreen,
                                            textAlign = TextAlign.Center
                                        )
                                        Text(
                                            text = lunarDayName,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Normal,
                                            color = if (isToday) Color.White.copy(alpha = 0.85f) else deepGreen.copy(alpha = 0.65f),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // Plan column — narrower, height fills row
                        val dateKey = date?.let { "${it.first}-${String.format("%02d", it.second)}-${String.format("%02d", it.third)}" } ?: ""
                        val plan = remember(dateKey) { PlanStorage.getPlan(context, dateKey) }

                        Box(
                            modifier = Modifier
                                .weight(1.3f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White, RoundedCornerShape(8.dp))
                                .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            contentAlignment = Alignment.TopStart
                        ) {
                            if (plan.isEmpty()) {
                                Text(
                                    text = "",
                                    fontSize = 13.sp,
                                    color = deepGreen.copy(alpha = 0.3f)
                                )
                            } else {
                                Text(
                                    text = plan,
                                    fontSize = 13.sp,
                                    color = deepGreen,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }

                // Divider line between rows
                item {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        thickness = 1.dp,
                        color = Color(0xFFE8E8E8)
                    )
                }
            }

            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

// ========== YEAR PICKER DIALOG ==========

@Composable
fun YearPickerDialog(
    currentYear: Int,
    onYearSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val deepGreen = Color(0xFF2E7D32)
    val lightGreenBg = Color(0xFFD4EDDA)
    val years = (1900..2100).toList()
    val listState = rememberLazyListState()

    // Scroll to the current year position on init
    LaunchedEffect(Unit) {
        val rowsBeforeCurrent = (currentYear - 1900) / 5
        // Account for dividers: every 2 year-rows we have 1 divider
        val dividerCount = rowsBeforeCurrent / 2
        listState.scrollToItem(rowsBeforeCurrent + dividerCount)
    }

    // Full-screen overlay
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
                // Title bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(lightGreenBg)
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "选择年份",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = deepGreen
                    )
                }

                // Year grid
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    val numRows = (years.size + 4) / 5 // ceil division, 5 per row
                    var itemIndex = 0
                    for (row in 0 until numRows) {
                        val startIdx = row * 5
                        val rowYears = years.subList(startIdx, minOf(startIdx + 5, years.size))

                        // Year row
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rowYears.forEach { y ->
                                    val isCurrent = y == currentYear
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(
                                                if (isCurrent) deepGreen else Color(0xFFF0F8F0),
                                                RoundedCornerShape(10.dp)
                                            )
                                            .clickable { onYearSelected(y) }
                                            .padding(vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = y.toString(),
                                            fontSize = 14.sp,
                                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isCurrent) Color.White else deepGreen,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                                // Fill remaining cells with spacers
                                val remaining = 5 - rowYears.size
                                repeat(remaining) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }

                        // Divider every 2 rows (after rows 1, 3, 5, ... i.e. row index 1, 3, 5, ...)
                        if (row % 2 == 1 && row < numRows - 1) {
                            item {
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    thickness = 2.dp,
                                    color = Color(0xFFCCCCCC)
                                )
                            }
                        }
                    }
                }

                // Close button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(deepGreen)
                            .clickable(onClick = onDismiss)
                            .padding(horizontal = 32.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "关闭",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
    }
}

// ========== SETTINGS DIALOG ==========

@Composable
fun SettingsDialog(
    weekStartMonday: Boolean,
    onWeekStartChange: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    val deepGreen = Color(0xFF2E7D32)
    var dropdownExpanded by remember { mutableStateOf(false) }

    // Full-screen overlay
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Settings content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            // Row: Weekly start day
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "每周开始日习惯",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = deepGreen
                )

                // Dropdown
                Box(modifier = Modifier.width(120.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, deepGreen, RoundedCornerShape(8.dp))
                            .clickable { dropdownExpanded = true }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (weekStartMonday) "周一" else "周日",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = deepGreen,
                            textAlign = TextAlign.Center
                        )
                    }

                    DropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false },
                        modifier = Modifier.width(120.dp)
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text("周一", fontSize = 15.sp, color = deepGreen)
                            },
                            onClick = {
                                onWeekStartChange(true)
                                dropdownExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text("周日", fontSize = 15.sp, color = deepGreen)
                            },
                            onClick = {
                                onWeekStartChange(false)
                                dropdownExpanded = false
                            }
                        )
                    }
                }
            }
        }

        // Close button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(deepGreen)
                    .clickable(onClick = onDismiss)
                    .padding(horizontal = 32.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "关闭",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

// ========== PREVIEW ==========

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CalendarAppPreview() {
    LDCalenderTheme {
        CalendarApp()
    }
}