package com.shangying.plugin.model

import com.shangying.plugin.Constant

/**
 * 字段注释
 * Created by jinshubao on 2017/1/23.
 */
class FieldComment implements Comment {

    String comment

    @Override
    String toString() {
        StringBuffer buffer = new StringBuffer()
        buffer.append(Constant.tab_1).append("/**").append(Constant.newLine)
        buffer.append(Constant.tab_1).append(" * ").append(comment ?: "").append(Constant.newLine)
        buffer.append(Constant.tab_1).append(" */").append(Constant.newLine)
        return buffer.toString()
    }
}
