package com.example.caloriecounter

import android.app.Application

class MyApp:Application()
{
    var isCM:Boolean?=null
    var isKg:Boolean?=null
    var isLbs:Boolean?=null
    var isSt:Boolean?=null
    var heightcm:Int?=null
    var heightft:Int?=null
    var heightin:Int?=null
    var weightkg:Int?=null
    var weightlbs:Int?=null
    var weightst:Int?=null
    var age:Int?=null
    var Name:String?=null
    override fun onCreate() {
        super.onCreate()


    }
}