package com.pixie.android.ui.chat

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.pixie.android.R
import com.pixie.android.model.chat.MessageData
import kotlin.random.Random

class MessagingAdapter(context: Context) : BaseAdapter() {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    private var listOfMessage = ArrayList<MessageData>()

    fun add(message: MessageData) {
        this.listOfMessage.add(message)
        notifyDataSetChanged()
    }

    fun set(newMessages: ArrayList<MessageData>) {
        listOfMessage = ArrayList(newMessages)
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return listOfMessage.size
    }

    override fun getItem(position: Int): Any {
        return listOfMessage[position]
    }

    fun clear() {
        listOfMessage.clear()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("DefaultLocale")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val message: MessageData = listOfMessage[position]
        val messageBelongsToCurrentUser = message.belongsToCurrentUser
        val isVirtual = message.channelParticipant.isVirtual
        return if (messageBelongsToCurrentUser == true) {

            val rowView = inflater.inflate(R.layout.align_main_chat_right, parent, false)
            val txtTitle = rowView.findViewById<TextView>(R.id.text_title)
            val timePosted = rowView.findViewById<TextView>(R.id.time)
            val avatar = rowView.findViewById<ImageView>(R.id.avatar_participant)

            val time = message.timePosted.toLong()
            val timeFormatted = getDate(time, "HH:mm:ss")
            txtTitle.text = message.text
            timePosted.text = timeFormatted

            var foregroundColor: Int? = null
            if (!message.channelParticipant.avatarForeground.isNullOrEmpty()) {
                foregroundColor = Color.parseColor(message.channelParticipant.avatarForeground)
            }
            if (foregroundColor == null) {
                foregroundColor = Color.argb(255, Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))
            }


            var backgroundColor: Int? = null
            if (!message.channelParticipant.avatarBackground.isNullOrEmpty()) {
                backgroundColor = Color.parseColor(message.channelParticipant.avatarBackground)
            }
            if (backgroundColor == null) {
                backgroundColor = Color.argb(255, Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))
            }
            avatar.setColorFilter(
                backgroundColor
            )
            avatar.backgroundTintList = ColorStateList.valueOf(
                foregroundColor
            )

            rowView

        } else if (isVirtual != null && isVirtual) {
            val rowView = inflater.inflate(R.layout.host_main_chat_message, parent, false)

            val txtTitle = rowView.findViewById<TextView>(R.id.message_body)
            val userName = rowView.findViewById<TextView>(R.id.name)
            val timePosted = rowView.findViewById<TextView>(R.id.time)
            val avatar = rowView.findViewById<ImageView>(R.id.avatar_participant)

            val time = message.timePosted.toLong()
            val timeFormatted = getDate(time, "HH:mm:ss")


            txtTitle.text = message.text
            userName.text = message.userName
            timePosted.text = timeFormatted

            var foregroundColor: Int? = null
            if (!message.channelParticipant.avatarForeground.isNullOrEmpty()) {
                foregroundColor = Color.parseColor(message.channelParticipant.avatarForeground)
            }
            if (foregroundColor == null) {
                foregroundColor = Color.argb(255, Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))
            }


            var backgroundColor: Int? = null
            if (!message.channelParticipant.avatarBackground.isNullOrEmpty()) {
                backgroundColor = Color.parseColor(message.channelParticipant.avatarBackground)
            }
            if (backgroundColor == null) {
                backgroundColor = Color.argb(255, Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))
            }
            avatar.setColorFilter(
                backgroundColor
            )
            avatar.backgroundTintList = ColorStateList.valueOf(
                foregroundColor
            )
            rowView
        } else {
            val rowView = inflater.inflate(R.layout.other_main_chat_message, parent, false)

            val txtTitle = rowView.findViewById<TextView>(R.id.message_body)
            val userName = rowView.findViewById<TextView>(R.id.name)
            val timePosted = rowView.findViewById<TextView>(R.id.time)
            val avatar = rowView.findViewById<ImageView>(R.id.avatar_participant)

            val time = message.timePosted.toLong()
            val timeFormatted = getDate(time, "HH:mm:ss")


            txtTitle.text = message.text
            userName.text = message.userName
            timePosted.text = timeFormatted

            var foregroundColor: Int? = null
            if (!message.channelParticipant.avatarForeground.isNullOrEmpty()) {
                foregroundColor = Color.parseColor(message.channelParticipant.avatarForeground)
            }
            if (foregroundColor == null) {
                foregroundColor = Color.argb(255, Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))
            }


            var backgroundColor: Int? = null
            if (!message.channelParticipant.avatarBackground.isNullOrEmpty()) {
                backgroundColor = Color.parseColor(message.channelParticipant.avatarBackground)
            }
            if (backgroundColor == null) {
                backgroundColor = Color.argb(255, Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))
            }
            avatar.setColorFilter(
                backgroundColor
            )
            avatar.backgroundTintList = ColorStateList.valueOf(
                foregroundColor
            )
            rowView
        }

    }

    private fun getDate(milliSeconds: Long, dateFormat: String?): String? {
        // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat(dateFormat)

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }

    private fun formatTime(text: String): String {
        return text
    }
}