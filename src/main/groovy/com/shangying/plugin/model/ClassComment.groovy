package com.shangying.plugin.model

import com.shangying.plugin.Constant

import java.text.DateFormat
import java.text.SimpleDateFormat

/**
 *
 * Created by jinshubao on 2017/1/23.
 */
class ClassComment implements Comment {
    String author
    String comment
    Date date
    DateFormat dateFormat = new SimpleDateFormat("yyyy/M/d")


    @Override
    String toString() {
        StringBuffer buffer = new StringBuffer()
        buffer.append("/**").append(Constant.newLine)
        buffer.append(" * ").append(comment ?: "").append(Constant.newLine)
        buffer.append(" * Created by ").append(author ?: "Robot")
        if (dateFormat) {
            buffer.append(" on ").append(dateFormat.format(date ?: new Date())).append(".")
        }
        buffer.append(Constant.newLine)
        buffer.append(" */").append(Constant.newLine)
        return buffer.toString()
    }
}
