# Learn English Words

This project was created for educational purposes. (Probably not only)

Learn English Words is application for Android. It is similar to anki droid, but I am going to create a better design and another algorithm of studying. And probably support of different algorithms and ways of studying like LinguaLeo. 

---

Here I feel have to say I was not aware of such apps which can help in learning languages in this way. This was my own idea. But like most of the ideas in this big word it was not new idea and very soon I found out these apps. But I don't think I need to avoid them to create brand new experience. Vise versa I think it is good that I am not first here. I have opportunity to learn the mistakes of others and avoid them and find in these application something that I probably would never imagine.

How I see it for now
---

You create dictionaries which collect cards. Cards are cards in other words  some object that has two sides where user can write on. User can see only one side of card. It is called front side. Another side which user cannot  see has translation of front side and is called back side. Each card has property called scrutiny. This values symbolizes the degree of knowing of each card. It is integer value belonging to some neighborhood of zero(e.g. -25 <= scrutiny <= 25). Initial value is 0. If scrutiny is on the bottom limit it cannot become less. And if scrutiny becomes equal to the top limit the card disappears from dictionary and probably is moving into the extra dictionary of learned cards. Each time user guesses back side of card scrutiny increases. Each time user makes mistake it decreases. But this happens only once per day. So user can training cards for the whole day but it will make application to think that the user learned all cards. It's logical and easy to implement(so naive XD).I think such form of scrutiny is good because of two reasons. In case user know a card wery well user will not spend a lot of days repeating it. But from the other side if user face with troubles during learning a card user will not decrease scrutiny to the big negative value. So user will not have to repeat this card for the millenium to make it learned. But anyway user will need to try hard to learn it.

So when a user opens app with intent to repeat cards what happens. The app shows the front side to user. And the user needs to write down in the editable field below the variant of the back side inscription. Accoding to the equality or not of his answer with the actual back side insciption app changes scrutiny of the shown card.

*Glad to hear opinions and ideas.*

Build
---

I use Android Studio and nothing unusual. Hence you can easily import this project using menu File>Open Project. I have not tried yet but it seems so. Anyway if you have any trouble in importing write me email and I will try to help you.
