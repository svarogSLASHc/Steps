package com.test.pedometer.domain.fileaccess

interface LoggerRepository{
    fun add(msg:String)

    fun saveAll():String

    fun save(msg:String)

    fun getRawText():String

    fun clear()
}