import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.gdms_front.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

class CheckWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    private val BASE_URL = "http://192.168.0.73:8080/"
    private val TAG = "BoardCheckWorkerTest"


    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Starting work")
            val noticeUrl = URL("${BASE_URL}api/board/notice")
            val eventUrl = URL("${BASE_URL}api/board/event")

            val noticeResponse = fetchBoardData(noticeUrl)
            val eventResponse = fetchBoardData(eventUrl)


            // 새로운 공지사항/이벤트 체크 로직 추가
            checkForNewItems(noticeResponse, "lastCheckedNoticeDate")
            checkForNewItems(eventResponse, "lastCheckedEventDate")

            Log.d(TAG, "Work completed successfully")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Work failed", e)
            Result.failure()
        }
    }

    private fun fetchBoardData(url: URL): JSONArray {
        Log.d(TAG, "Fetching data from URL: ${url}")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.setRequestProperty("Content-Type", "application/json")

        val responseCode = connection.responseCode
        Log.d(TAG, "Response code: $responseCode")
        val response = connection.inputStream.bufferedReader().use { it.readText() }
        Log.d(TAG, "Response: $response")

        connection.disconnect()

        return JSONArray(response)
    }

    private fun checkForNewItems(data: JSONArray, lastCheckedDateKey: String) {
        Log.d(TAG, "Checking for new items")
        val lastCheckedDate = applicationContext.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            .getString(lastCheckedDateKey, "1970-01-01") ?: "1970-01-01"
        Log.d(TAG, "Last checked date: $lastCheckedDate")

        for (i in 0 until data.length()) {
            val item = data.getJSONObject(i)
            val boardDate = item.getString("boardDate")
            Log.d(TAG, "Item board date: $boardDate")
            if (boardDate > lastCheckedDate) {
                // 알림 보내기
                val title = item.getString("title")
                val content = item.getString("content")
                Log.d(TAG, "New item found - Title: $title, Content: $content")
                sendNotification(title, content)
            }
        }

        val prefs = applicationContext.getSharedPreferences("app_prefs", Context.MODE_PRIVATE).edit()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            prefs.putString(lastCheckedDateKey, java.time.LocalDate.now().toString())
        }
        prefs.apply()
        Log.d(TAG, "Updated last checked date")
    }

    private fun sendNotification(title: String, content: String) {
        Log.d(TAG, "Sending notification - Title: $title, Content: $content")
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = applicationContext.getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.img_notification_1)
            .setContentTitle(title)
            .setContentText(content)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Channel human readable title", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
            Log.d(TAG, "Notification channel created")
        }

        notificationManager.notify(0, notificationBuilder.build())
        Log.d(TAG, "Notification sent")
    }
}