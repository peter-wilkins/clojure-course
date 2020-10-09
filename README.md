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
 ```git clone https://github.com/peter-wilkins/clojure-course.git```

- Get  [Leiningen](https://leiningen.org/)

Editors - the instructions in the chapters assume cursive but if you prefer another and know how to repl already use whatever you want
- Get [Intellij Community Edition](https://www.jetbrains.com/idea/)
- Get [Cursive](https://cursive-ide.com/)

Shortcuts - Learn or change 2 shortcuts in Intellij for REPL happiness
- Load File in REPL
- Send Form Before Caret to REPL

Starting a REPL
Open `clojure-course` in intellij as a project
Right click on project.clj and select `+ Add as leiningen project`
wait for intellij to finish syncronising
Right click on project.clj and select `Run REPL for ...`
Open `src/thread_the_needle.clj`
Use your shortcut to `Load File in REPL`


Common Repl Error messages and what they mean:

Error: :namespace-not-found 
-> you need to `Load File in REPL`

CompilerException java.lang.ClassCastException: class XXX cannot be cast to class clojure.lang.IFn
-> "This XXX cannot be turned into a Function"
-> the first form inside a brackets always needs to be a function i.e. `(1)` errors while (inc 0) is fine

CompilerException clojure.lang.ArityException: Wrong number of args (0) passed to: core/inc--inliner--5258
-> `(inc)` errors as `inc` expects exactly one arg

IllegalArgumentException Don't know how to create ISeq from: XXX
-> "this XXX cannot be turned into a sequence"
-> collection functions expect the collection as the last arg e.g. `(map inc [1 2 3])` not `(map [1 2 3] inc)`

CompilerException java.lang.RuntimeException: EOF while reading
-> you are missing a closing bracket somewhere


Recommended Order for Exercises:
- src/thread-the-needle.clj
- src/p-p-p-pokerface.clj

Then do rest of the Mooc

Extras
- src/specter-gadget.clj
- src/composing-transducers.clj



