# RoKUe Like
RoKUe Like is a rogue-like game for COMP302 Fall 2024 Term Project, designed by ChampionCoders!

# Some Useful Instructions for Running the Code
-> Check out ui/application/Main.java to Run the code. It calls the ModeController and provides the necessary ScreenTransitions for UI layer classes.

-> ScreenTransition is a functional interface and is given as a parameter to each screen for transitions by ModeController. ModeController provides its necessary methods through this functional interface and needed class objects to UI classes. (Observer and Controller Pattern)
