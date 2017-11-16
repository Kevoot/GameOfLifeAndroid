# Week 2 Progress Report

Whats been done:
* Kevin
  * Refactored code into CellGridView class which extends from View
  * Got Randomize and new blank canvas working
  * Added stub functions for the rest of the group for plugging into the simulation
  * Added "painting" mode - Allows the user to finger paint into the simulation, which 
		is then translated into live cells
  * Added Context menu, which will allow for simulation speed and save managment 
		fragments
  * Added SerializeableCellGrid class for easy serialization of selected grid segments into
		byte arrays for easier sql storage
  * Updated main activty ui with new buttons and icons, as well as fixed bottom-bar layout
* Alex
* James
  * Added selection box to appear when touched
  * Added functionality for cut and copy to return the selected bit array’s
  * Added button disabling when painting 
* George
  * Added a fragment that can access and display the contents of a database
  * Added a ListView object that can display the information of the elements of a database
    to allow the users to select from the database.