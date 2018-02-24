# Clojure Course

This course is inspired by and borrows from the excellent [Clojure Mooc](http://iloveponies.github.io/120-hour-epic-sax-marathon/index.html) by the by the CS department of University of Helsinki.

Focuses on:

- use the repl for fast learning feedback.
- writing human friendly code from the start
- avoiding clojure's pain points 
- uses familiar problem domains

Setup
- Join [Clojurians on Slack.](http://clojurians.net/) (most important step - google then ask on beginners if you get stuck at any point) 
- Get [Git](https://git-scm.com/)
- get the repo:
 ```git clone https://github.com/peter-wilkins-mayden/clojure-course.git```

- Get  [Leiningen](https://leiningen.org/)

Editors - the instructions in the chapters assume cursive but if you prefer another and know how to repl already use whatever you want
- Get [Intellij Community Edition](https://www.jetbrains.com/idea/)
- Get [Cursive](https://cursive-ide.com/)

Alternatively if you don't want to install an IDE: [Nightlight](https://sekao.net/nightlight) (an editor that runs inside the same process and has an insta-repl)

- Add  
```{:user {:plugins [[nightlight/lein-nightlight "RELEASE"]]}}```
to your ~/.lein/profiles.clj
			
- Then, in your project do ```lein nightlight``` it will tell you where to open your browser





Recommended Order for Exercises:
- src/thread-the-needle.clj
- src/p-p-p-pokerface.clj
- src/specter-gadget.clj
- src/composing-transducers.clj

Work in progress - if you find any issues or have any suggestions please leave an issue.



