package com.shangying.plugin.model

import com.shangying.plugin.Constant

/**
 * 方法注释
 * Created by jinshubao on 2017/1/23.
 */
class MethodComment extends FieldComment implements Comment {
    List<CommentParameter> parameters = []
    List<CommentParameter> exceptions = []
    String returnDesc

    @Override
    String toString() {
        StringBuffer buffer = new StringBuffer()
        buffer.append(Constant.tab_1).append("/**").append(Constant.newLine)
        buffer.append(Constant.tab_1).append(" * ").append(comment ?: "").append(Constant.newLine)
        parameters.each {
            buffer.append(Constant.tab_1).append(" * @param ").append(it.name ?: "").append(" ").append(it.description ?: "").append(Constant.newLine)
        }
        exceptions.each {
            buffer.append(Constant.tab_1).append(" * @throws Exception ").append(it.name ?: "").append(" ").append(it.description ?: "").append(Constant.newLine)
        }
        buffer.append(Constant.tab_1).append(" * @return ").append(returnDesc ?: "").append(Constant.newLine)
        buffer.append(Constant.tab_1).append(" */").append(Constant.newLine)

        return super.toString()
    }
}
