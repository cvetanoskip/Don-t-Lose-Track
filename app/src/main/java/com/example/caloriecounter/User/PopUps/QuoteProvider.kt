package com.example.caloriecounter.User.PopUps

data class Quote(
    val q: String,
    val a: String
)

object QuoteProvider {
    val gymQuotes = listOf(
        Quote("Discipline is the bridge between goals and accomplishment.", "Jim Rohn"),
        Quote("Your body can stand almost anything. It’s your mind that you have to convince.", "Unknown"),
        Quote("Motivation gets you started. Habit keeps you going.", "Jim Ryun"),
        Quote("Rome wasn’t built in a day, but it burned in one. Keep building.", "Unknown"),
        Quote("No matter how slow you go, you’re still lapping everyone on the couch.", "Unknown"),


        Quote("The only bad workout is the one you didn’t do.", "Unknown"),
        Quote("Sore today, strong tomorrow.", "Unknown"),
        Quote("You don’t get the ass you want by sitting on it.", "Unknown"),
        Quote("Work hard in silence. Let success be your noise.", "Frank Ocean"),
        Quote("Don’t wish for it. Work for it.", "Unknown"),


        Quote("Strength does not come from the body. It comes from the will.", "Arnold Schwarzenegger"),
        Quote("Pain is weakness leaving the body.", "U.S. Marines"),
        Quote("The last three or four reps is what makes the muscle grow.", "Arnold Schwarzenegger"),
        Quote("Champions aren’t made in the gyms. Champions are made from something they have deep inside them – a desire, a dream, a vision.", "Muhammad Ali"),
        Quote("Success is usually the culmination of controlling failure.", "Sylvester Stallone"),


        Quote("If it doesn’t challenge you, it won’t change you.", "Fred DeVito"),
        Quote("The only limit is the one you set yourself.", "Unknown"),
        Quote("Train insane or remain the same.", "Jillian Michaels"),
        Quote("Fall in love with the process, and the results will come.", "Unknown"),
        Quote("When you feel like quitting, remember why you started.", "Unknown"))

    fun getRandomGymQuote(): Quote {
        return gymQuotes.random()
    }
}