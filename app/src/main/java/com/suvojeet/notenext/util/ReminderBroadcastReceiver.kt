package com.suvojeet.notenext.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.suvojeet.notenext.MainActivity
import com.suvojeet.notenext.R
import com.suvojeet.notenext.data.AlarmScheduler
import com.suvojeet.notenext.data.NoteRepository
import com.suvojeet.notenext.data.RepeatOption
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject

@AndroidEntryPoint
class ReminderBroadcastReceiver : BroadcastReceiver() {

    @Inject
    lateinit var repository: NoteRepository

    @Inject
    lateinit var alarmScheduler: AlarmScheduler

    override fun onReceive(context: Context?, intent: Intent?) {
        val pendingResult = goAsync()
        val job = SupervisorJob()
        val scope = CoroutineScope(job + Dispatchers.IO)
        
        scope.launch {
            try {
                if (context != null) {
                    val type = intent?.getStringExtra("TYPE") ?: "NOTE"
                    
                    if (type == "TODO") {
                        val todoId = intent?.getIntExtra("TODO_ID", -1) ?: -1
                        val todoTitle = intent?.getStringExtra("TODO_TITLE") ?: "Todo Reminder"
                        val todoContent = intent?.getStringExtra("TODO_CONTENT") ?: ""

                        createNotificationChannel(context)
                        showNotification(context, todoId, todoTitle, todoContent, isTodo = true)
                    } else if (type == "EXPIRY") {
                        // Self-destruct: per-note exact alarm fires here. Re-check the
                        // note's current state — if the user extended or removed the
                        // timer, do nothing. Otherwise delete the note. The hourly
                        // AutoDeleteWorker remains as a safety net for missed alarms.
                        val noteId = intent?.getIntExtra("NOTE_ID", -1) ?: -1
                        if (noteId == -1) return@launch
                        val current = repository.getNoteById(noteId)?.note ?: return@launch
                        val expiry = current.expiryTime
                        if (expiry != null && expiry <= System.currentTimeMillis()) {
                            repository.deleteNote(current)
                        } else if (expiry != null) {
                            // Timer was extended — re-arm.
                            alarmScheduler.scheduleExpiry(current)
                        }
                    } else {
                        val noteId = intent?.getIntExtra("NOTE_ID", -1) ?: -1
                        if (noteId == -1) return@launch

                        // Always re-read from DB. The alarm intent only carries the ID —
                        // this prevents leaking ciphertext via PendingIntent extras and
                        // ensures we never show a deleted / edited / locked note's stale
                        // content in the notification.
                        val noteWithAttachments = repository.getNoteById(noteId)
                        val note = noteWithAttachments?.note ?: return@launch

                        // For locked / encrypted notes, never decrypt off-screen. Show a
                        // privacy-safe placeholder so the user is reminded without leaking
                        // contents to the lockscreen / notification shade.
                        val safeTitle: String
                        val safeContent: String
                        if (note.isLocked || note.isEncrypted) {
                            safeTitle = "Reminder"
                            safeContent = "You have a reminder for a locked note. Open the app to view."
                        } else {
                            safeTitle = note.title.ifBlank { "Reminder" }
                            val plain = HtmlConverter.htmlToPlainText(note.content)
                            safeContent = if (plain.length > 150) plain.substring(0, 150) + "..." else plain
                        }

                        createNotificationChannel(context)
                        showNotification(context, noteId, safeTitle, safeContent, isTodo = false)

                        // Handle Repeat Logic
                        val repeatOptionStr = note.repeatOption
                        if (repeatOptionStr != null && repeatOptionStr != RepeatOption.NEVER.name) {
                            try {
                                val repeatOption = RepeatOption.valueOf(repeatOptionStr)
                                val nextTime = calculateNextReminderTime(note.reminderTime ?: System.currentTimeMillis(), repeatOption)

                                if (nextTime > System.currentTimeMillis()) {
                                    val updatedNote = note.copy(reminderTime = nextTime)
                                    repository.updateNote(updatedNote)
                                    alarmScheduler.schedule(updatedNote)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            } finally {
                pendingResult.finish()
                job.cancel()
            }
        }
    }

    private fun calculateNextReminderTime(currentMillis: Long, repeatOption: RepeatOption): Long {
        val currentDateTime = Instant.ofEpochMilli(currentMillis).atZone(ZoneId.systemDefault())
        
        val nextDateTime = when (repeatOption) {
            RepeatOption.DAILY -> currentDateTime.plusDays(1)
            RepeatOption.WEEKLY -> currentDateTime.plusWeeks(1)
            RepeatOption.MONTHLY -> currentDateTime.plusMonths(1)
            RepeatOption.YEARLY -> currentDateTime.plusYears(1)
            RepeatOption.NEVER -> currentDateTime
        }
        
        return nextDateTime.toInstant().toEpochMilli()
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Reminder Channel"
            val descriptionText = "Channel for Note and Todo Reminders"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("reminder_channel_id", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(context: Context, id: Int, title: String, content: String, isTodo: Boolean) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            if (isTodo) {
                putExtra("TODO_ID", id)
            } else {
                putExtra("NOTE_ID", id)
            }
        }
        
        val notificationId = if (isTodo) id + 1000000 else id
        
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, "reminder_channel_id")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && notificationManager.canUseFullScreenIntent()) {
            builder.setFullScreenIntent(pendingIntent, true)
        }

        with(NotificationManagerCompat.from(context)) {
            try {
                 notify(notificationId, builder.build())
            } catch (e: SecurityException) {
            }
        }
    }
}
