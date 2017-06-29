package com.alextanhongpin.app

import com.typesafe.config.ConfigFactory

trait Config {
    private val config = ConfigFactory.load()
    private val databaseConfig = config.getConfig("database")

    val database = databaseConfig.getString("database")
    val port = config.getInt("port")
}