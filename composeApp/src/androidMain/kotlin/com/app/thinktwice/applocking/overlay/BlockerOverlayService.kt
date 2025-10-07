package com.app.thinktwice.applocking.overlay

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Button
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity as AndroidGravity

/**
 * Service that shows a fullscreen overlay to block restricted apps
 * Uses SYSTEM_ALERT_WINDOW permission to show over other apps
 */
class BlockerOverlayService : Service() {

    private var windowManager: WindowManager? = null
    private var overlayView: FrameLayout? = null
    private var packageName: String? = null

    companion object {
        const val EXTRA_PACKAGE_NAME = "package_name"
        const val ACTION_SHOW_BLOCKER = "com.app.thinktwice.SHOW_BLOCKER"
        const val ACTION_HIDE_BLOCKER = "com.app.thinktwice.HIDE_BLOCKER"

        private var isOverlayShowing = false

        fun showBlocker(context: Context, packageName: String) {
            android.util.Log.d("BlockerOverlayService", "showBlocker() called for: $packageName")
            val intent = Intent(context, BlockerOverlayService::class.java).apply {
                action = ACTION_SHOW_BLOCKER
                putExtra(EXTRA_PACKAGE_NAME, packageName)
            }
            context.startService(intent)
        }

        fun hideBlocker(context: Context) {
            android.util.Log.d("BlockerOverlayService", "hideBlocker() called")
            val intent = Intent(context, BlockerOverlayService::class.java).apply {
                action = ACTION_HIDE_BLOCKER
            }
            context.startService(intent)
        }

        fun isShowing() = isOverlayShowing
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_SHOW_BLOCKER -> {
                packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME)
                android.util.Log.d("BlockerOverlayService", "Showing overlay for: $packageName")
                showOverlay()
            }
            ACTION_HIDE_BLOCKER -> {
                android.util.Log.d("BlockerOverlayService", "Hiding overlay")
                hideOverlay()
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    private fun showOverlay() {
        if (isOverlayShowing) {
            android.util.Log.d("BlockerOverlayService", "Overlay already showing")
            return
        }

        // Check if we have overlay permission
        if (!android.provider.Settings.canDrawOverlays(this)) {
            android.util.Log.e("BlockerOverlayService", "SYSTEM_ALERT_WINDOW permission NOT granted!")
            android.util.Log.e("BlockerOverlayService", "User must enable 'Display over other apps' in Settings")

            // Show a fallback notification to inform user
            showPermissionNotification()
            return
        }

        try {
            windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

            // Create overlay layout using traditional Views (no Compose)
            overlayView = createBlockerView()

            // Configure window layout parameters
            val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE
            }

            val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                layoutType,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                PixelFormat.TRANSLUCENT
            ).apply {
                gravity = Gravity.TOP or Gravity.START
            }

            // Add view to window manager
            windowManager?.addView(overlayView, params)
            isOverlayShowing = true

            android.util.Log.d("BlockerOverlayService", "Overlay added successfully")
        } catch (e: Exception) {
            android.util.Log.e("BlockerOverlayService", "Error showing overlay", e)
            isOverlayShowing = false
        }
    }

    private fun hideOverlay() {
        try {
            if (overlayView != null && windowManager != null) {
                windowManager?.removeView(overlayView)
                overlayView = null
                isOverlayShowing = false
                android.util.Log.d("BlockerOverlayService", "Overlay removed successfully")
            }
        } catch (e: Exception) {
            android.util.Log.e("BlockerOverlayService", "Error hiding overlay", e)
        }
    }

    /**
     * Create the blocker view using traditional Android Views
     */
    private fun createBlockerView(): FrameLayout {
        return FrameLayout(this).apply {
            setBackgroundColor(Color.parseColor("#1A1A1A"))

            // Main content container
            val container = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                gravity = AndroidGravity.CENTER
                val padding = (32 * resources.displayMetrics.density).toInt()
                setPadding(padding, padding, padding, padding)

                // Emoji icon
                addView(TextView(context).apply {
                    text = "⛔"
                    textSize = 72f
                    gravity = AndroidGravity.CENTER
                    val bottomMargin = (24 * resources.displayMetrics.density).toInt()
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(0, 0, 0, bottomMargin)
                    }
                })

                // Title
                addView(TextView(context).apply {
                    text = "App Blocked"
                    textSize = 32f
                    setTypeface(null, Typeface.BOLD)
                    setTextColor(Color.WHITE)
                    gravity = AndroidGravity.CENTER
                    val bottomMargin = (16 * resources.displayMetrics.density).toInt()
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(0, 0, 0, bottomMargin)
                    }
                })

                // Description
                addView(TextView(context).apply {
                    text = "You've restricted access to this app"
                    textSize = 18f
                    setTextColor(Color.parseColor("#CCCCCC"))
                    gravity = AndroidGravity.CENTER
                    val bottomMargin = (8 * resources.displayMetrics.density).toInt()
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(0, 0, 0, bottomMargin)
                    }
                })

                // Package name
                addView(TextView(context).apply {
                    text = packageName ?: "Unknown App"
                    textSize = 14f
                    setTextColor(Color.parseColor("#888888"))
                    gravity = AndroidGravity.CENTER
                    val verticalMargin = (8 * resources.displayMetrics.density).toInt()
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(0, verticalMargin, 0, verticalMargin)
                    }
                })

                // Spacer
                addView(android.view.View(context).apply {
                    val height = (48 * resources.displayMetrics.density).toInt()
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        height
                    )
                })

                // Exit button
                addView(Button(context).apply {
                    text = "Exit to Home"
                    textSize = 18f
                    setTypeface(null, Typeface.BOLD)
                    setTextColor(Color.parseColor("#1A1A1A"))
                    setBackgroundColor(Color.WHITE)
                    val height = (56 * resources.displayMetrics.density).toInt()
                    layoutParams = LinearLayout.LayoutParams(
                        (resources.displayMetrics.widthPixels * 0.8).toInt(),
                        height
                    ).apply {
                        val topMargin = (0 * resources.displayMetrics.density).toInt()
                        setMargins(0, topMargin, 0, 0)
                    }
                    setOnClickListener {
                        exitToHome()
                    }
                })

                // Bottom text
                addView(TextView(context).apply {
                    text = "Think twice before spending"
                    textSize = 14f
                    setTextColor(Color.parseColor("#888888"))
                    gravity = AndroidGravity.CENTER
                    setTypeface(null, Typeface.ITALIC)
                    val topMargin = (16 * resources.displayMetrics.density).toInt()
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(0, topMargin, 0, 0)
                    }
                })
            }

            addView(container, FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT,
                AndroidGravity.CENTER
            ))
        }
    }

    private fun exitToHome() {
        // Hide overlay first
        hideOverlay()

        // Take user to home screen
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)

        // Stop service
        stopSelf()
    }

    private fun showPermissionNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager

        // Create notification channel if needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = android.app.NotificationChannel(
                "blocker_permission",
                "Blocker Permissions",
                android.app.NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications about required permissions for app blocker"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Create intent to open overlay permission settings
        val intent = Intent(
            android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            android.net.Uri.parse("package:$packageName")
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        val pendingIntent = android.app.PendingIntent.getActivity(
            this,
            0,
            intent,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
        )

        val notification = androidx.core.app.NotificationCompat.Builder(this, "blocker_permission")
            .setContentTitle("Permission Required")
            .setContentText("Enable 'Display over other apps' to block restricted apps")
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .addAction(
                android.R.drawable.ic_menu_preferences,
                "Open Settings",
                pendingIntent
            )
            .build()

        notificationManager.notify(9999, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        hideOverlay()
    }
}
