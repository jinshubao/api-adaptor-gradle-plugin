package com.shangying.plugin.model

/**
 * 方法参数
 * Created by jinshubao on 2017/1/23.
 */
class MethodParameter {
    String type
    String name

    @Override
    String toString() {
        return "${type} ${name}"
    }
}
