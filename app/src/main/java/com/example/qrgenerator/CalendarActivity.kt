package com.example.qrgenerator

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.DateTime
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.calendar.model.Events
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream
import java.io.InputStreamReader


class CalendarActivity : AppCompatActivity() {
    private val applicationName = "Google Calendar API Java Quickstart"
    private val jsonFactory: JsonFactory = GsonFactory.getDefaultInstance()
    private val tokenDirectoryPath = "tokens"

    private val scopes = listOf(CalendarScopes.CALENDAR_READONLY)
    private val credentialFilePath = "./credentials.json"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        myFunc()

    }

    private fun myFunc() {
        val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
        val service = Calendar.Builder(httpTransport, jsonFactory, getCredentials(httpTransport))
            .setApplicationName(applicationName)
            .build()

        val now = DateTime(System.currentTimeMillis())
        val events: Events = service.events().list("primary")
            .setMaxResults(10)
            .setTimeMin(now)
            .setOrderBy("startTime")
            .setSingleEvents(true)
            .execute()
        val items: MutableList<com.google.api.services.calendar.model.Event>? = events.items
        if (items != null) {
            if (items.isEmpty()) {
                println("No upcoming events found.")
            } else {
                println("Upcoming events")
                for (event in items) {
                    val start = event.start.dateTime
                    System.out.printf("%s (%s)\n", event.summary, start)
                }
            }
        }
    }

    private fun getCredentials(HTTP_TRANSPORT: NetHttpTransport): Credential? {

        val inputStream: InputStream =
            CalendarActivity::class.java.getResourceAsStream(credentialFilePath)
                ?: throw FileNotFoundException("Resource not found: $credentialFilePath")

        val clientSecrets =
            GoogleClientSecrets.load(jsonFactory, InputStreamReader(inputStream))

        val flow = GoogleAuthorizationCodeFlow.Builder(
            HTTP_TRANSPORT, jsonFactory, clientSecrets, scopes
        )
            .setDataStoreFactory(FileDataStoreFactory(File(tokenDirectoryPath)))
            .setAccessType("offline")
            .build()
        val receiver = LocalServerReceiver.Builder().setPort(8888).build()
        return AuthorizationCodeInstalledApp(flow, receiver).authorize("user")
    }
}

/*
    private val applicationName = "Google Calendar API Java Quickstart"
    private val jsonFactory: JsonFactory = GsonFactory.getDefaultInstance()
    private val tokenDirectoryPath = "tokens"

    private val scopes = listOf(CalendarScopes.CALENDAR_READONLY)
    private val credentialFilePath = "/credentials.json"
     */

/*
@SuppressLint("Range")
private fun getEventsFromDeviceCalender() {
    val eventProjection = arrayOf(
        CalendarContract.Calendars._ID,
        CalendarContract.Calendars.ACCOUNT_NAME,
        CalendarContract.Events.TITLE,
        CalendarContract.Events.DTSTART,
        CalendarContract.Events.DTEND,
        CalendarContract.Events.EVENT_LOCATION,
        CalendarContract.Events.DESCRIPTION
    )

    val uri = CalendarContract.Events.CONTENT_URI

    val selection = "((${CalendarContract.Calendars.ACCOUNT_NAME} = ?) AND (" +
            "${CalendarContract.Calendars.ACCOUNT_TYPE} = ?) AND (" +
            "${CalendarContract.Calendars.OWNER_ACCOUNT} = ?))"

    val email = Firebase.auth.currentUser?.email
    val selectionArgs = arrayOf(email, "com.google", email)

    checkPermission()

    val cur = contentResolver.query(uri, eventProjection, selection, selectionArgs, null)

    if (cur!!.count > 0) {
        while (cur.moveToNext()) {
            val eventTitle = cur.getString(cur.getColumnIndex(CalendarContract.Events.TITLE))
            val startDate = cur.getString(cur.getColumnIndex(CalendarContract.Events.DTSTART))

            val endDate = cur.getString(cur.getColumnIndex(CalendarContract.Events.DTEND))

            list.add(Event(eventTitle, startDate, endDate))
        }
        setAdapter()
        cur.close()
    }

    val eventAddButton = findViewById<FloatingActionButton>(R.id.eventAddButton)
    eventAddButton.setOnClickListener {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Enter Event")

        builder.show()
    }
}

private fun setAdapter() {
    val rv = findViewById<RecyclerView>(R.id.eventRecyclerView)
    val eventAdapter = EventAdapter(this, list)
    rv.adapter = eventAdapter
    rv.layoutManager = LinearLayoutManager(this)
}

private fun checkPermission() {
    if (ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_CALENDAR
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        requestPermissionLauncher.launch(Manifest.permission.READ_CALENDAR)
    }
}

private val requestPermissionLauncher = registerForActivityResult(
    ActivityResultContracts.RequestPermission(),
) { isGranted: Boolean ->
    if (isGranted) {
        Toast.makeText(this, "success", Toast.LENGTH_SHORT).show()
    } else {
        Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show()
    }
}
*/

/*
private fun myFunc() {
    val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
    val service = Calendar.Builder(httpTransport, jsonFactory, getCredentials(httpTransport))
        .setApplicationName(applicationName)
        .build()

    val now = DateTime(System.currentTimeMillis())
    val events: Events = service.events().list("primary")
        .setMaxResults(10)
        .setTimeMin(now)
        .setOrderBy("startTime")
        .setSingleEvents(true)
        .execute()
    val items: MutableList<com.google.api.services.calendar.model.Event>? = events.items
    if (items != null) {
        if (items.isEmpty()) {
            println("No upcoming events found.")
        } else {
            println("Upcoming events")
            for (event in items) {
                val start = event.start.dateTime
                System.out.printf("%s (%s)\n", event.summary, start)
            }
        }
    }
}

private fun getCredentials(HTTP_TRANSPORT: NetHttpTransport): Credential? {
    val inputStream: InputStream =
        CalendarActivity::class.java.getResourceAsStream(credentialFilePath)
            ?: throw FileNotFoundException("Resource not found: $credentialFilePath")
    val clientSecrets =
        GoogleClientSecrets.load(jsonFactory, InputStreamReader(inputStream))

    val flow = GoogleAuthorizationCodeFlow.Builder(
        HTTP_TRANSPORT, jsonFactory, clientSecrets, scopes
    )
        .setDataStoreFactory(FileDataStoreFactory(File(tokenDirectoryPath)))
        .setAccessType("offline")
        .build()
    val receiver = LocalServerReceiver.Builder().setPort(8888).build()
    return AuthorizationCodeInstalledApp(flow, receiver).authorize("user")
}
 */






