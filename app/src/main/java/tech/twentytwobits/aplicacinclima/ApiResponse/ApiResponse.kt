package tech.twentytwobits.aplicacinclima.ApiResponse

class ApiResponse(var name: String, var cod: Int, var weather: ArrayList<Weather>, var main: Main, var sys: Sys, var wind: Wind) {
}