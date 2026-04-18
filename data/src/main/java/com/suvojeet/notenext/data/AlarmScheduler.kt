package com.suvojeet.notenext.data

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.suvojeet.notenext.data.RepeatOption
import java.util.Calendar

interface AlarmScheduler {
    fun schedule(note: Note)
    fun cancel(note: Note)
    fun scheduleTodo(todo: TodoItem)
    fun cancelTodo(todo: TodoItem)
}

class AlarmSchedulerImpl(private val context: Context) : AlarmScheduler {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    // Bug H2 fix: unified scheduling so Notes and Todos fall back the same way when
    // SCHEDULE_EXACT_ALARM is denied. Previously Notes silently failed while Todos
    // fell back to inexact alarms — now both degrade gracefully to setAndAllowWhileIdle.
    private fun scheduleAlarm(triggerAtMillis: Long, pendingIntent: PendingIntent) {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
                } else {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            }
        } catch (e: SecurityException) {
            // Permission revoked between check and call — log and fall back to inexact.
            android.util.Log.w("AlarmScheduler", "Exact alarm denied, scheduling inexact", e)
            try {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            } catch (e2: SecurityException) {
                android.util.Log.e("AlarmScheduler", "Alarm scheduling failed entirely", e2)
            }
        }
    }

    override fun schedule(note: Note) {
        val reminderTime = note.reminderTime ?: return
        if (reminderTime <= System.currentTimeMillis()) return

        val intent = Intent().apply {
            component = ComponentName(context, "com.suvojeet.notenext.util.ReminderBroadcastReceiver")
            putExtra("NOTE_ID", note.id)
            putExtra("NOTE_TITLE", note.title)
            putExtra("NOTE_CONTENT", note.content)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            note.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        scheduleAlarm(reminderTime, pendingIntent)
    }

    override fun cancel(note: Note) {
        val intent = Intent().apply {
            component = ComponentName(context, "com.suvojeet.notenext.util.ReminderBroadcastReceiver")
            putExtra("NOTE_ID", note.id)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            note.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    override fun scheduleTodo(todo: TodoItem) {
        val reminderTime = todo.reminderTime ?: return
        if (reminderTime <= System.currentTimeMillis()) return

        val intent = Intent().apply {
            component = ComponentName(context, "com.suvojeet.notenext.util.ReminderBroadcastReceiver")
            putExtra("TODO_ID", todo.id)
            putExtra("TODO_TITLE", todo.title)
            putExtra("TODO_CONTENT", todo.description)
            putExtra("TYPE", "TODO")
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            todoRequestCode(todo.id),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        scheduleAlarm(reminderTime, pendingIntent)
    }

    // Bug M4 fix: instead of additive offset (todo.id + 1_000_000) which collides once
    // note IDs grow past a million, set the high bit to mark a Todo request code.
    // This gives a disjoint namespace for Note and Todo PendingIntents.
    private fun todoRequestCode(todoId: Int): Int = todoId or 0x4000_0000

    override fun cancelTodo(todo: TodoItem) {
        val intent = Intent().apply {
            component = ComponentName(context, "com.suvojeet.notenext.util.ReminderBroadcastReceiver")
            putExtra("TODO_ID", todo.id)
            putExtra("TYPE", "TODO")
        }
        // Cancel with both new and legacy request codes to cover alarms scheduled
        // before the M4 fix (pre-upgrade) as well as new ones.
        listOf(todoRequestCode(todo.id), todo.id + 1_000_000).forEach { code ->
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                code,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
        }
    }
}
