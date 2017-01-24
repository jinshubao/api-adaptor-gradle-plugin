package com.shangying.plugin.model

import com.shangying.plugin.Constant

/**
 * Created by jinshubao on 2017/1/23.
 */
class ClassFiled {
    String type
    String declare
    String name

    Comment comment

    @Override
    String toString() {
        StringBuffer buffer = new StringBuffer()
        if (comment) {
            buffer.append(comment.toString())
        }
        buffer.append(Constant.tab_1)
        if (declare) {
            buffer.append(declare).append(" ")
        }
        if (type) {
            buffer.append(type).append(" ")
        } else {
            buffer.append("void").append(" ")
        }
        buffer.append(name)
        return buffer.toString()
    }
}
