package com.avcoding.avmediapicker.utils

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.avcoding.avmediapicker.model.Img
import com.avcoding.avmediapicker.model.MediaMode
import com.avcoding.avmediapicker.model.ModelList
import java.util.*
import kotlin.collections.ArrayList
import kotlin.coroutines.cancellation.CancellationException

fun Context.getImageVideoCursor(mode: MediaMode): Cursor? {
    val projection = when (mode) {
        MediaMode.Video -> VIDEO_SELECTION
        MediaMode.Picture -> IMAGE_SELECTION
        else -> IMAGE_VIDEO_SELECTION
    }
    return contentResolver
        .query(
            IMAGE_VIDEO_URI, IMAGE_VIDEO_PROJECTION,
            projection, null, null
        )
}

internal class LocalResourceManager(
    private val context: Context
) {
    private val className = LocalResourceManager::class.java.simpleName

    init {
        Log.v(TAG, "$className initiated")
    }

    var preSelectedUrls: List<Uri> = ArrayList()
    fun retrieveMedia(
        start: Int = 0,
        limit: Int = 2500,
        mode: MediaMode = MediaMode.All
    ): ModelList {
        val cursor = context.getImageVideoCursor(mode)
        Log.v(TAG, "$className retrieved images from $start to $limit and size ${cursor?.count}")
        val list = ArrayList<Img>()
        var header = ""
        val selectionList = ArrayList<Img>()

        try {
            if (cursor != null) {
                val mediaTypeColumnId =
                    cursor.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE)
                val contentUrl = cursor.getColumnIndex(MediaStore.Files.FileColumns._ID)
                val imageDate = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_MODIFIED)
                if (start > cursor.count) {
                    return ModelList(list = ArrayList(), selection = ArrayList())
                }
                var end = if (limit == 0) cursor.count - start - 1 else limit
                if (cursor.count - start < limit) {
                    end = cursor.count - 1
                }
                if (end < start) {
                    end += (start + 1)
                }
                if (start == 0) {
                    cursor.moveToFirst()
                } else {
                    cursor.moveToPosition(start)
                    header =
                        context.resources.getDateDifference(
                            Calendar.getInstance()
                                .apply { timeInMillis = cursor.getLong(imageDate) * 1000 })
                }
                synchronized(context) {
                    var pos = start
                    Log.e(TAG, "$className start $start till end $end")
                    for (i in start until end) {
                        try {
                            val path = try {
                                Uri.withAppendedPath(
                                    IMAGE_VIDEO_URI,
                                    "" + cursor.getInt(contentUrl)
                                )

                            } catch (ex: Exception) {
                                Log.e(TAG, "$className Exception ${ex.message}")
                                Uri.EMPTY
                            }

                            val dateDifference =
                                context.resources.getDateDifference(
                                    Calendar.getInstance()
                                        .apply { timeInMillis = cursor.getLong(imageDate) * 1000 })
                            val mediaType = cursor.getInt(mediaTypeColumnId)
                            if (!header.equals("" + dateDifference, ignoreCase = true)) {
                                header = "" + dateDifference
                                pos += 1
                                list.add(
                                    Img(
                                        headerDate = "" + dateDifference,
                                        mediaType = mediaType
                                    )
                                )
                            }
                            Img(
                                headerDate = header,
                                contentUrl = path,
                                scrollerDate = pos.toString(),
                                mediaType = mediaType
                            ).apply {
                                this.position = pos
                            }.also {
                                if (preSelectedUrls.contains(it.contentUrl)) {
                                    it.selected = true
                                    selectionList.add(it)
                                }
                                pos += 1
                                list.add(it)
                            }

                        } catch (ex: java.lang.Exception) {
                            ex.printStackTrace()
                            Log.e(TAG, "$className Exception ${ex.message}")
                        }
                        cursor.moveToNext()
                    }
                    cursor.close()
                }
            }

        } catch (ex: CancellationException) {
            Log.e(TAG, "$className CancellationException ${ex.message}")
            return ModelList(list = ArrayList(), selection = ArrayList())
        } catch (ex: Exception) {
            Log.e(TAG, "$className Exception ${ex.message}")
            ex.printStackTrace()
        }
        return ModelList(list = list, selection = selectionList)
    }
}