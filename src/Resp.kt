package com.phoenix

data class Resp<T> (var data:T,var code:Int=200,var msg:String="success")