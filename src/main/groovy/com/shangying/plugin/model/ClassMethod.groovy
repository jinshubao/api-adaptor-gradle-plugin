package com.shangying.plugin.model

import com.shangying.plugin.Constant

/**
 * Created by jinshubao on 2017/1/23.
 */
class ClassMethod extends ClassFiled {

    List<MethodParameter> parameters = []

    String content

    @Override
    String toString() {
        StringBuffer buffer = new StringBuffer()
        buffer.append(super.toString())
        buffer.append("(${parameters.join(", ")}) {").append(Constant.newLine)
        if (content) {
            buffer.append(content).append(Constant.newLine)
        } else {
            if (type && type != "void") {
                buffer.append(Constant.tab_2).append("return null").append(Constant.newLine)
            }
        }
        buffer.append(Constant.tab_1).append("}").append(Constant.newLine)
        return buffer.toString()
    }
}
