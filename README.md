## Tasks complete
*A quick overview of the tasks that I completed.*

1. Email selection:
    - Added a set selected function to update the UI state w/ the selected email and pass in the selected email as a parameter.
    - ReplyListPane: pass the selected email into the list item object so it can be highlighted correctly
    - ReplyListItem: visual UI modifications to show selection
2. Adaptive layouts and responsiveness:
    - Used the given resource to check if the layout is "COMPACT" (i.e. < 600.dp), and adjusted the layout based on that. Naive text comparison implementation for simplicity and speed.
    - UI code check is a relatively simple if statement between two layouts.
3. Star and reply buttons:
    - Vast variety of different "callback" functions implemented in `ReplyHomeViewModel.kt`
    - The process for most of these is the same:
        a. Push up the function signatures in all of the necessary locations (e.g. `ReplyApp`, `ReplyAppContent`, `MainActivity` + previews, etc.) with the correct parameters
        b. In the specific areas where the function needs to be called/passed down to a different UI element, do so with the right parameters.
        c. Ensure that the UI composes correctly such that updates are reflected as the user selects the content.
    - For the `reply`/`reply all` buttons in particular, I created a new pane (`ReplyPane`) that has very barebones and simple UI/functionality. At the moment, it just sends a logging message representing a "sent message", but could do more e.g. add it to the thread, attach it to a mail server, etc.
