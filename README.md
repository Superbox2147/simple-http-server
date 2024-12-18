**Usage:**
1. put the server files at the root of your web game's folder, which usually contains a file called index.html, files such as simpleWebServer.jar and run.bat should be in the same folder as files such as index.html
2. configure using the .env bundler file, refer to .envconfiguration (you can skip this step if you don't care or know about ports and the main file of your game is index.html)
3. run using the included run.batfile, this should open your game in your default browser

**Uploading:**
- to upload the game, include all of the files included in bundlerFiles.7z inside the archive you upload for the game, your game.zip (can be any name) should include all of your game files and the openjdk-21.0.2 folder, the configured .env  file, the simpleWebServer.jar file and the run.bat file

**Notes:**
- if your game runs as normal outside of the bundler but fails with the bundler, please report the error shown in the browser console (usually opened with f12 in the browser) or server console (the cmd window opened when running run.bat)
- if you have issues with anything or need more instructions, feel free to ping/DM me on Discord any time and I'll get to it as soon as I can
- This is designed to enhance the user experience of your application, allowing for anyone to run your game locally without downloading anything extra (refer to Vedal scuff last game jam, that should not happen with this)
- getting environment variables is done by sending a GET request to /$env/{VARIABLE_NAME} where {VARIABLE_NAME] is the name of the environment variable you want, this returns the contents of the variable as a string (make sure to NOT have the brackets around the variable name)



**`.env` configuration:**
- `.env` is the main configuration file, formatted as plain text
- Each line in .env starts with a key, which is the name of a setting, formatted as all uppercase, with no spaces (example: PORT_OVERRIDE)
- Each key should be followed by =
- Each = should be followed by a value, here are the supported keys for the current version and what they do:
- INITIAL_PATH, default value INITIAL_PATH = "index.html", the name of the file to open in the browser by default
- PORT_OVERRIDE, no default value, integer to override the port if you want to use a port different from 7272
